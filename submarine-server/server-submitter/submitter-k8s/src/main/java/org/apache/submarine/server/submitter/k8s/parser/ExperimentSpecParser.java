/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.submarine.server.submitter.k8s.parser;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.models.V1Container;
import io.kubernetes.client.models.V1EnvVar;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1PodSpec;
import io.kubernetes.client.models.V1PodTemplateSpec;
import io.kubernetes.client.models.V1ResourceRequirements;
import org.apache.submarine.server.api.exception.InvalidSpecException;
import org.apache.submarine.server.api.spec.ExperimentMeta;
import org.apache.submarine.server.api.spec.ExperimentSpec;
import org.apache.submarine.server.api.spec.ExperimentTaskSpec;
import org.apache.submarine.server.submitter.k8s.model.MLJob;
import org.apache.submarine.server.submitter.k8s.model.MLJobReplicaSpec;
import org.apache.submarine.server.submitter.k8s.model.MLJobReplicaType;
import org.apache.submarine.server.submitter.k8s.model.pytorchjob.PyTorchJob;
import org.apache.submarine.server.submitter.k8s.model.pytorchjob.PyTorchJobReplicaType;
import org.apache.submarine.server.submitter.k8s.model.pytorchjob.PyTorchJobSpec;
import org.apache.submarine.server.submitter.k8s.model.tfjob.TFJob;
import org.apache.submarine.server.submitter.k8s.model.tfjob.TFJobReplicaType;
import org.apache.submarine.server.submitter.k8s.model.tfjob.TFJobSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExperimentSpecParser {

  public static MLJob parseJob(ExperimentSpec experimentSpec) throws InvalidSpecException {
    String framework = experimentSpec.getMeta().getFramework();
    if (ExperimentMeta.SupportedMLFramework.TENSORFLOW.
        getName().equalsIgnoreCase(framework)) {
      return parseTFJob(experimentSpec);
    } else if (ExperimentMeta.SupportedMLFramework.PYTORCH.
        getName().equalsIgnoreCase(framework)) {
      return parsePyTorchJob(experimentSpec);
    } else {
      throw new InvalidSpecException("Unsupported framework name: " + framework +
          ". Supported frameworks are: " +
          String.join(",", ExperimentMeta.SupportedMLFramework.names()));
    }
  }

  public static PyTorchJob parsePyTorchJob(
      ExperimentSpec experimentSpec) throws InvalidSpecException {
    PyTorchJob pyTorchJob = new PyTorchJob();
    pyTorchJob.setMetadata(parseMetadata(experimentSpec));
    pyTorchJob.setSpec(parsePyTorchJobSpec(experimentSpec));
    return pyTorchJob;
  }

  public static PyTorchJobSpec parsePyTorchJobSpec(ExperimentSpec experimentSpec)
      throws InvalidSpecException {
    PyTorchJobSpec pyTorchJobSpec = new PyTorchJobSpec();
    Map<MLJobReplicaType, MLJobReplicaSpec> replicaSpecMap = new HashMap<>();
    for (Map.Entry<String, ExperimentTaskSpec> entry : experimentSpec.getSpec().entrySet()) {
      String replicaType = entry.getKey();
      ExperimentTaskSpec taskSpec = entry.getValue();
      if (PyTorchJobReplicaType.isSupportedReplicaType(replicaType)) {
        MLJobReplicaSpec replicaSpec = new MLJobReplicaSpec();
        replicaSpec.setReplicas(taskSpec.getReplicas());
        replicaSpec.setTemplate(parseTemplateSpec(taskSpec, experimentSpec));
        replicaSpecMap.put(PyTorchJobReplicaType.valueOf(replicaType), replicaSpec);
      } else {
        throw new InvalidSpecException("Unrecognized replica type name: " +
            entry.getKey() + ", it should be " +
            String.join(",", PyTorchJobReplicaType.names()) +
            " for PyTorch experiment.");
      }
    }
    pyTorchJobSpec.setReplicaSpecs(replicaSpecMap);
    return pyTorchJobSpec;
  }

  public static TFJob parseTFJob(ExperimentSpec experimentSpec)
      throws InvalidSpecException {
    TFJob tfJob = new TFJob();
    tfJob.setMetadata(parseMetadata(experimentSpec));
    tfJob.setSpec(parseTFJobSpec(experimentSpec));
    return tfJob;
  }

  private static V1ObjectMeta parseMetadata(ExperimentSpec experimentSpec) {
    V1ObjectMeta meta = new V1ObjectMeta();
    meta.setName(experimentSpec.getMeta().getName());
    meta.setNamespace(experimentSpec.getMeta().getNamespace());
    return meta;
  }

  private static TFJobSpec parseTFJobSpec(ExperimentSpec experimentSpec) throws InvalidSpecException {
    TFJobSpec tfJobSpec = new TFJobSpec();
    Map<MLJobReplicaType, MLJobReplicaSpec> replicaSpecMap = new HashMap<>();
    for (Map.Entry<String, ExperimentTaskSpec> entry : experimentSpec.getSpec().entrySet()) {
      String replicaType = entry.getKey();
      ExperimentTaskSpec taskSpec = entry.getValue();
      if (TFJobReplicaType.isSupportedReplicaType(replicaType)) {
        MLJobReplicaSpec replicaSpec = new MLJobReplicaSpec();
        replicaSpec.setReplicas(taskSpec.getReplicas());
        replicaSpec.setTemplate(parseTemplateSpec(taskSpec, experimentSpec));
        replicaSpecMap.put(TFJobReplicaType.valueOf(replicaType), replicaSpec);
      } else {
        throw new InvalidSpecException("Unrecognized replica type name: " +
            entry.getKey() +
            ", it should be " +
            String.join(",", TFJobReplicaType.names()) +
            " for TensorFlow experiment.");
      }
    }
    tfJobSpec.setReplicaSpecs(replicaSpecMap);
    return tfJobSpec;
  }

  private static V1PodTemplateSpec parseTemplateSpec(ExperimentTaskSpec taskSpec,
      ExperimentSpec experimentSpec) {
    V1PodTemplateSpec templateSpec = new V1PodTemplateSpec();
    V1PodSpec podSpec = new V1PodSpec();
    List<V1Container> containers = new ArrayList<>();
    V1Container container = new V1Container();
    container.setName(experimentSpec.getMeta().getFramework().toLowerCase());
    // image
    if (taskSpec.getImage() != null) {
      container.setImage(taskSpec.getImage());
    } else {
      container.setImage(experimentSpec.getEnvironment().getImage());
    }
    // cmd
    if (taskSpec.getCmd() != null) {
      container.setCommand(Arrays.asList(taskSpec.getCmd().split(" ")));
    } else {
      container.setCommand(Arrays.asList(experimentSpec.getMeta().getCmd().split(" ")));
    }
    // resources
    V1ResourceRequirements resources = new V1ResourceRequirements();
    resources.setLimits(parseResources(taskSpec));
    container.setResources(resources);
    container.setEnv(parseEnvVars(taskSpec, experimentSpec.getMeta().getEnvVars()));
    containers.add(container);
    podSpec.setContainers(containers);
    templateSpec.setSpec(podSpec);
    return templateSpec;
  }

  private static List<V1EnvVar> parseEnvVars(ExperimentTaskSpec spec,
      Map<String, String> defaultEnvs) {
    if (spec.getEnvVars() != null) {
      return parseEnvVars(spec.getEnvVars());
    }
    return parseEnvVars(defaultEnvs);
  }

  private static List<V1EnvVar> parseEnvVars(Map<String, String> envMap) {
    if (envMap == null)
      return null;
    List<V1EnvVar> envVars = new ArrayList<>();
    for (Map.Entry<String, String> entry : envMap.entrySet()) {
      V1EnvVar env = new V1EnvVar();
      env.setName(entry.getKey());
      env.setValue(entry.getValue());
      envVars.add(env);
    }
    return envVars;
  }

  private static Map<String, Quantity> parseResources(ExperimentTaskSpec taskSpec) {
    Map<String, Quantity> resources = new HashMap<>();
    taskSpec.setResources(taskSpec.getResources());
    if (taskSpec.getCpu() != null) {
      resources.put("cpu", new Quantity(taskSpec.getCpu()));
    }
    if (taskSpec.getMemory() != null) {
      resources.put("memory", new Quantity(taskSpec.getMemory()));
    }
    if (taskSpec.getGpu() != null) {
      resources.put("nvidia.com/gpu", new Quantity(taskSpec.getGpu()));
    }
    return resources;
  }
}
