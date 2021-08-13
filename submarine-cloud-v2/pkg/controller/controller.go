/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controller

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	v1alpha1 "github.com/apache/submarine/submarine-cloud-v2/pkg/apis/submarine/v1alpha1"
	clientset "github.com/apache/submarine/submarine-cloud-v2/pkg/client/clientset/versioned"
	submarinescheme "github.com/apache/submarine/submarine-cloud-v2/pkg/client/clientset/versioned/scheme"
	informers "github.com/apache/submarine/submarine-cloud-v2/pkg/client/informers/externalversions/submarine/v1alpha1"
	listers "github.com/apache/submarine/submarine-cloud-v2/pkg/client/listers/submarine/v1alpha1"

	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	extensionsv1beta1 "k8s.io/api/extensions/v1beta1"
	rbacv1 "k8s.io/api/rbac/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	utilruntime "k8s.io/apimachinery/pkg/util/runtime"
	"k8s.io/apimachinery/pkg/util/wait"
	appsinformers "k8s.io/client-go/informers/apps/v1"
	coreinformers "k8s.io/client-go/informers/core/v1"
	extinformers "k8s.io/client-go/informers/extensions/v1beta1"
	rbacinformers "k8s.io/client-go/informers/rbac/v1"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/kubernetes/scheme"
	typedcorev1 "k8s.io/client-go/kubernetes/typed/core/v1"
	appslisters "k8s.io/client-go/listers/apps/v1"
	corelisters "k8s.io/client-go/listers/core/v1"
	extlisters "k8s.io/client-go/listers/extensions/v1beta1"
	rbaclisters "k8s.io/client-go/listers/rbac/v1"
	"k8s.io/client-go/tools/cache"
	"k8s.io/client-go/tools/record"
	"k8s.io/client-go/util/workqueue"
	"k8s.io/klog/v2"

	traefik "github.com/traefik/traefik/v2/pkg/provider/kubernetes/crd/generated/clientset/versioned"
	traefikinformers "github.com/traefik/traefik/v2/pkg/provider/kubernetes/crd/generated/informers/externalversions/traefik/v1alpha1"
	traefiklisters "github.com/traefik/traefik/v2/pkg/provider/kubernetes/crd/generated/listers/traefik/v1alpha1"
	traefikv1alpha1 "github.com/traefik/traefik/v2/pkg/provider/kubernetes/crd/traefik/v1alpha1"
)

const controllerAgentName = "submarine-controller"

const (
	serverName                  = "submarine-server"
	databaseName                = "submarine-database"
	tensorboardName             = "submarine-tensorboard"
	mlflowName                  = "submarine-mlflow"
	minioName                   = "submarine-minio"
	ingressName                 = serverName + "-ingress"
	databaseScName              = databaseName + "-sc"
	databasePvcName             = databaseName + "-pvc"
	tensorboardScName           = tensorboardName + "-sc"
	tensorboardPvcName          = tensorboardName + "-pvc"
	tensorboardServiceName      = tensorboardName + "-service"
	tensorboardIngressRouteName = tensorboardName + "-ingressroute"
	mlflowScName                = mlflowName + "-sc"
	mlflowPvcName               = mlflowName + "-pvc"
	mlflowServiceName           = mlflowName + "-service"
	mlflowIngressRouteName      = mlflowName + "-ingressroute"
	minioScName                 = minioName + "-sc"
	minioPvcName                = minioName + "-pvc"
	minioServiceName            = minioName + "-service"
	minioIngressRouteName       = minioName + "-ingressroute"
)

const (
	// SuccessSynced is used as part of the Event 'reason' when a Submarine is synced
	SuccessSynced = "Synced"
	// ErrResourceExists is used as part of the Event 'reason' when a Submarine fails
	// to sync due to a Deployment of the same name already existing.
	ErrResourceExists = "ErrResourceExists"

	// MessageResourceExists is the message used for Events when a resource
	// fails to sync due to a Deployment already existing
	MessageResourceExists = "Resource %q already exists and is not managed by Submarine"
	// MessageResourceSynced is the message used for an Event fired when a
	// Submarine is synced successfully
	MessageResourceSynced = "Submarine synced successfully"
)

// Controller is the controller implementation for Submarine resources
type Controller struct {
	// kubeclientset is a standard kubernetes clientset
	kubeclientset kubernetes.Interface
	// sampleclientset is a clientset for our own API group
	submarineclientset clientset.Interface
	traefikclientset   traefik.Interface

	submarinesLister listers.SubmarineLister
	submarinesSynced cache.InformerSynced

	namespaceLister             corelisters.NamespaceLister
	deploymentLister            appslisters.DeploymentLister
	serviceaccountLister        corelisters.ServiceAccountLister
	serviceLister               corelisters.ServiceLister
	persistentvolumeclaimLister corelisters.PersistentVolumeClaimLister
	ingressLister               extlisters.IngressLister
	ingressrouteLister          traefiklisters.IngressRouteLister
	roleLister                  rbaclisters.RoleLister
	rolebindingLister           rbaclisters.RoleBindingLister
	// workqueue is a rate limited work queue. This is used to queue work to be
	// processed instead of performing it as soon as a change happens. This
	// means we can ensure we only process a fixed amount of resources at a
	// time, and makes it easy to ensure we are never processing the same item
	// simultaneously in two different workers.
	workqueue workqueue.RateLimitingInterface
	// recorder is an event recorder for recording Event resources to the
	// Kubernetes API.
	recorder record.EventRecorder

	incluster bool
}

// NewController returns a new sample controller
func NewController(
	incluster bool,
	kubeclientset kubernetes.Interface,
	submarineclientset clientset.Interface,
	traefikclientset traefik.Interface,
	namespaceInformer coreinformers.NamespaceInformer,
	deploymentInformer appsinformers.DeploymentInformer,
	serviceInformer coreinformers.ServiceInformer,
	serviceaccountInformer coreinformers.ServiceAccountInformer,
	persistentvolumeclaimInformer coreinformers.PersistentVolumeClaimInformer,
	ingressInformer extinformers.IngressInformer,
	ingressrouteInformer traefikinformers.IngressRouteInformer,
	roleInformer rbacinformers.RoleInformer,
	rolebindingInformer rbacinformers.RoleBindingInformer,
	submarineInformer informers.SubmarineInformer) *Controller {

	// Add Submarine types to the default Kubernetes Scheme so Events can be
	// logged for Submarine types.
	utilruntime.Must(submarinescheme.AddToScheme(scheme.Scheme))
	klog.V(4).Info("Creating event broadcaster")
	eventBroadcaster := record.NewBroadcaster()
	eventBroadcaster.StartStructuredLogging(0)
	eventBroadcaster.StartRecordingToSink(&typedcorev1.EventSinkImpl{Interface: kubeclientset.CoreV1().Events("")})
	recorder := eventBroadcaster.NewRecorder(scheme.Scheme, corev1.EventSource{Component: controllerAgentName})

	// Initialize controller
	controller := &Controller{
		kubeclientset:               kubeclientset,
		submarineclientset:          submarineclientset,
		traefikclientset:            traefikclientset,
		submarinesLister:            submarineInformer.Lister(),
		submarinesSynced:            submarineInformer.Informer().HasSynced,
		namespaceLister:             namespaceInformer.Lister(),
		deploymentLister:            deploymentInformer.Lister(),
		serviceLister:               serviceInformer.Lister(),
		serviceaccountLister:        serviceaccountInformer.Lister(),
		persistentvolumeclaimLister: persistentvolumeclaimInformer.Lister(),
		ingressLister:               ingressInformer.Lister(),
		ingressrouteLister:          ingressrouteInformer.Lister(),
		roleLister:                  roleInformer.Lister(),
		rolebindingLister:           rolebindingInformer.Lister(),
		workqueue:                   workqueue.NewNamedRateLimitingQueue(workqueue.DefaultControllerRateLimiter(), "Submarines"),
		recorder:                    recorder,
		incluster:                   incluster,
	}

	// Setting up event handler for Submarine
	klog.Info("Setting up event handlers")
	submarineInformer.Informer().AddEventHandler(cache.ResourceEventHandlerFuncs{
		AddFunc: controller.enqueueSubmarine,
		UpdateFunc: func(old, new interface{}) {
			controller.enqueueSubmarine(new)
		},
	})

	// Setting up event handler for other resources
	namespaceInformer.Informer().AddEventHandler(cache.ResourceEventHandlerFuncs{
		AddFunc: controller.handleObject,
		UpdateFunc: func(old, new interface{}) {
			newNamespace := new.(*corev1.Namespace)
			oldNamespace := old.(*corev1.Namespace)
			if newNamespace.ResourceVersion == oldNamespace.ResourceVersion {
				return
			}
			controller.handleObject(new)
		},
		DeleteFunc: controller.handleObject,
	})
	deploymentInformer.Informer().AddEventHandler(cache.ResourceEventHandlerFuncs{
		AddFunc: controller.handleObject,
		UpdateFunc: func(old, new interface{}) {
			newDeployment := new.(*appsv1.Deployment)
			oldDeployment := old.(*appsv1.Deployment)
			if newDeployment.ResourceVersion == oldDeployment.ResourceVersion {
				return
			}
			controller.handleObject(new)
		},
		DeleteFunc: controller.handleObject,
	})
	serviceInformer.Informer().AddEventHandler(cache.ResourceEventHandlerFuncs{
		AddFunc: controller.handleObject,
		UpdateFunc: func(old, new interface{}) {
			newService := new.(*corev1.Service)
			oldService := old.(*corev1.Service)
			if newService.ResourceVersion == oldService.ResourceVersion {
				return
			}
			controller.handleObject(new)
		},
		DeleteFunc: controller.handleObject,
	})
	serviceaccountInformer.Informer().AddEventHandler(cache.ResourceEventHandlerFuncs{
		AddFunc: controller.handleObject,
		UpdateFunc: func(old, new interface{}) {
			newServiceAccount := new.(*corev1.ServiceAccount)
			oldServiceAccount := old.(*corev1.ServiceAccount)
			if newServiceAccount.ResourceVersion == oldServiceAccount.ResourceVersion {
				return
			}
			controller.handleObject(new)
		},
		DeleteFunc: controller.handleObject,
	})
	persistentvolumeclaimInformer.Informer().AddEventHandler(cache.ResourceEventHandlerFuncs{
		AddFunc: controller.handleObject,
		UpdateFunc: func(old, new interface{}) {
			newPVC := new.(*corev1.PersistentVolumeClaim)
			oldPVC := old.(*corev1.PersistentVolumeClaim)
			if newPVC.ResourceVersion == oldPVC.ResourceVersion {
				return
			}
			controller.handleObject(new)
		},
		DeleteFunc: controller.handleObject,
	})
	ingressInformer.Informer().AddEventHandler(cache.ResourceEventHandlerFuncs{
		AddFunc: controller.handleObject,
		UpdateFunc: func(old, new interface{}) {
			newIngress := new.(*extensionsv1beta1.Ingress)
			oldIngress := old.(*extensionsv1beta1.Ingress)
			if newIngress.ResourceVersion == oldIngress.ResourceVersion {
				return
			}
			controller.handleObject(new)
		},
		DeleteFunc: controller.handleObject,
	})
	ingressrouteInformer.Informer().AddEventHandler(cache.ResourceEventHandlerFuncs{
		AddFunc: controller.handleObject,
		UpdateFunc: func(old, new interface{}) {
			newIngressRoute := new.(*traefikv1alpha1.IngressRoute)
			oldIngressRoute := old.(*traefikv1alpha1.IngressRoute)
			if newIngressRoute.ResourceVersion == oldIngressRoute.ResourceVersion {
				return
			}
			controller.handleObject(new)
		},
		DeleteFunc: controller.handleObject,
	})
	roleInformer.Informer().AddEventHandler(cache.ResourceEventHandlerFuncs{
		AddFunc: controller.handleObject,
		UpdateFunc: func(old, new interface{}) {
			newRole := new.(*rbacv1.Role)
			oldRole := old.(*rbacv1.Role)
			if newRole.ResourceVersion == oldRole.ResourceVersion {
				return
			}
			controller.handleObject(new)
		},
		DeleteFunc: controller.handleObject,
	})
	rolebindingInformer.Informer().AddEventHandler(cache.ResourceEventHandlerFuncs{
		AddFunc: controller.handleObject,
		UpdateFunc: func(old, new interface{}) {
			newRoleBinding := new.(*rbacv1.RoleBinding)
			oldRoleBinding := old.(*rbacv1.RoleBinding)
			if newRoleBinding.ResourceVersion == oldRoleBinding.ResourceVersion {
				return
			}
			controller.handleObject(new)
		},
		DeleteFunc: controller.handleObject,
	})

	return controller
}

func (c *Controller) Run(threadiness int, stopCh <-chan struct{}) error {
	defer utilruntime.HandleCrash()
	defer c.workqueue.ShutDown()

	// Start the informer factories to begin populating the informer caches
	klog.Info("Starting Submarine controller")

	// Wait for the caches to be synced before starting workers
	klog.Info("Waiting for informer caches to sync")
	if ok := cache.WaitForCacheSync(stopCh, c.submarinesSynced); !ok {
		return fmt.Errorf("failed to wait for caches to sync")
	}

	klog.Info("Starting workers")
	// Launch $threadiness workers to process Submarine resources
	for i := 0; i < threadiness; i++ {
		go wait.Until(c.runWorker, time.Second, stopCh)
	}

	klog.Info("Started workers")
	<-stopCh
	klog.Info("Shutting down workers")

	return nil
}

// runWorker is a long-running function that will continually call the
// processNextWorkItem function in order to read and process a message on the
// workqueue.
func (c *Controller) runWorker() {
	for c.processNextWorkItem() {
	}
}

// processNextWorkItem will read a single work item off the workqueue and
// attempt to process it, by calling the syncHandler.
func (c *Controller) processNextWorkItem() bool {
	obj, shutdown := c.workqueue.Get()
	if shutdown {
		return false
	}

	// We wrap this block in a func so we can defer c.workqueue.Done.
	err := func(obj interface{}) error {
		defer c.workqueue.Done(obj)
		var key string
		var ok bool
		if key, ok = obj.(string); !ok {
			// As the item in the workqueue is actually invalid, we call
			// Forget here else we'd go into a loop of attempting to
			// process a work item that is invalid.
			c.workqueue.Forget(obj)
			utilruntime.HandleError(fmt.Errorf("expected WorkQueueItem in workqueue but got %#v", obj))
			return nil
		}
		// Run the syncHandler
		if err := c.syncHandler(key); err != nil {
			// Put the item back on the workqueue to handle any transient errors.
			c.workqueue.AddRateLimited(key)
			return fmt.Errorf("error syncing '%s': %s, requeuing", key, err.Error())
		}
		// Finally, if no error occurs we Forget this item so it does not
		// get queued again until another change happens.
		c.workqueue.Forget(obj)
		klog.Infof("Successfully synced '%s'", key)
		return nil
	}(obj)

	if err != nil {
		utilruntime.HandleError(err)
		return true
	}

	return true
}

// syncHandler compares the actual state with the desired, and attempts to
// converge the two. It then updates the Status block of the Submarine resource
// with the current status of the resource.
func (c *Controller) syncHandler(key string) error {
	// Convert the namespace/name string into a distinct namespace and name
	namespace, name, err := cache.SplitMetaNamespaceKey(key)
	if err != nil {
		utilruntime.HandleError(fmt.Errorf("Invalid resource key: %s", key))
		return nil
	}
	klog.Info("syncHandler: ", key)

	// Get the Submarine resource with this namespace/name
	submarine, err := c.submarinesLister.Submarines(namespace).Get(name)
	if err != nil {
		// The Submarine resource may no longer exist, in which case we stop
		// processing
		if errors.IsNotFound(err) {
			utilruntime.HandleError(fmt.Errorf("submarine '%s' in work queue no longer exists", key))
			return nil
		}
		return err
	}

	// Submarine is in the terminating process
	if !submarine.DeletionTimestamp.IsZero() {
		return nil
	}

	// Print out the spec of the Submarine resource
	b, err := json.MarshalIndent(submarine.Spec, "", "  ")
	fmt.Println(string(b))

	storageType := submarine.Spec.Storage.StorageType
	if storageType != "nfs" && storageType != "host" {
		utilruntime.HandleError(fmt.Errorf("Invalid storageType '%s' found in submarine spec, nothing will be created. Valid storage types are 'nfs' and 'host'", storageType))
		return nil
	}

	var serverDeployment *appsv1.Deployment
	var databaseDeployment *appsv1.Deployment

	if err != nil {
		return err
	}

	serverDeployment, err = c.createSubmarineServer(submarine)
	if err != nil {
		return err
	}

	databaseDeployment, err = c.createSubmarineDatabase(submarine)
	if err != nil {
		return err
	}

	err = c.createIngress(submarine)
	if err != nil {
		return err
	}

	err = c.createSubmarineServerRBAC(submarine)
	if err != nil {
		return err
	}

	err = c.createSubmarineTensorboard(submarine)
	if err != nil {
		return err
	}

	err = c.createSubmarineMlflow(submarine)
	if err != nil {
		return err
	}

	err = c.createSubmarineMinio(submarine)
	if err != nil {
		return err
	}

	err = c.updateSubmarineStatus(submarine, serverDeployment, databaseDeployment)
	if err != nil {
		return err
	}

	c.recorder.Event(submarine, corev1.EventTypeNormal, SuccessSynced, MessageResourceSynced)

	return nil
}

func (c *Controller) updateSubmarineStatus(submarine *v1alpha1.Submarine, serverDeployment *appsv1.Deployment, databaseDeployment *appsv1.Deployment) error {
	submarineCopy := submarine.DeepCopy()
	submarineCopy.Status.AvailableServerReplicas = serverDeployment.Status.AvailableReplicas
	submarineCopy.Status.AvailableDatabaseReplicas = databaseDeployment.Status.AvailableReplicas
	_, err := c.submarineclientset.SubmarineV1alpha1().Submarines(submarine.Namespace).Update(context.TODO(), submarineCopy, metav1.UpdateOptions{})
	return err
}

// enqueueSubmarine takes a Submarine resource and converts it into a namespace/name
// string which is then put onto the work queue. This method should *not* be
// passed resources of any type other than Submarine.
func (c *Controller) enqueueSubmarine(obj interface{}) {
	var key string
	var err error
	if key, err = cache.MetaNamespaceKeyFunc(obj); err != nil {
		utilruntime.HandleError(err)
		return
	}

	// key: [namespace]/[CR name]
	// Example: default/example-submarine
	c.workqueue.Add(key)
}

// handleObject will take any resource implementing metav1.Object and attempt
// to find the Submarine resource that 'owns' it. It does this by looking at the
// objects metadata.ownerReferences field for an appropriate OwnerReference.
// It then enqueues that Submarine resource to be processed. If the object does not
// have an appropriate OwnerReference, it will simply be skipped.
func (c *Controller) handleObject(obj interface{}) {
	var object metav1.Object
	var ok bool
	if object, ok = obj.(metav1.Object); !ok {
		tombstone, ok := obj.(cache.DeletedFinalStateUnknown)
		if !ok {
			utilruntime.HandleError(fmt.Errorf("error decoding object, invalid type"))
			return
		}
		object, ok = tombstone.Obj.(metav1.Object)
		if !ok {
			utilruntime.HandleError(fmt.Errorf("error decoding object tombstone, invalid type"))
			return
		}
		klog.V(4).Infof("Recovered deleted object '%s' from tombstone", object.GetName())
	}
	klog.V(4).Infof("Processing object: %s", object.GetName())
	if ownerRef := metav1.GetControllerOf(object); ownerRef != nil {
		// If this object is not owned by a Submarine, we should not do anything
		// more with it.
		if ownerRef.Kind != "Submarine" {
			return
		}

		submarine, err := c.submarinesLister.Submarines(object.GetNamespace()).Get(ownerRef.Name)
		if err != nil {
			klog.V(4).Infof("ignoring orphaned object '%s' of submarine '%s'", object.GetSelfLink(), ownerRef.Name)
			return
		}

		c.enqueueSubmarine(submarine)
		return
	}
}
