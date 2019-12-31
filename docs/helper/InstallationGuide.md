# Submarine Installation Guide

## Prerequisites

(Please note that all following prerequisites are just an example for you to install. You can always choose to install your own version of kernel, different users, different drivers, etc.).

### Operating System

The operating system and kernel versions we have tested are as shown in the following table, which is the recommneded minimum required versions.

| Enviroment | Verion |
| ------ | ------ |
| Operating System | centos-release-7-3.1611.el7.centos.x86_64 |
| Kernal | 3.10.0-514.el7.x86_64 |

### User & Group

As there are some specific users and groups recommended to be created to install hadoop/docker. Please create them if they are missing.

```
adduser hdfs
adduser mapred
adduser yarn
addgroup hadoop
usermod -aG hdfs,hadoop hdfs
usermod -aG mapred,hadoop mapred
usermod -aG yarn,hadoop yarn
usermod -aG hdfs,hadoop hadoop
groupadd docker
usermod -aG docker yarn
usermod -aG docker hadoop
```

### GCC Version

Check the version of GCC tool (to compile kernel).

```bash
gcc --version
gcc (GCC) 4.8.5 20150623 (Red Hat 4.8.5-11)
# install if needed
yum install gcc make g++
```

### Kernel header & Kernel devel

```bash
# Approach 1：
yum install kernel-devel-$(uname -r) kernel-headers-$(uname -r)
# Approach 2：
wget http://vault.centos.org/7.3.1611/os/x86_64/Packages/kernel-headers-3.10.0-514.el7.x86_64.rpm
rpm -ivh kernel-headers-3.10.0-514.el7.x86_64.rpm
```

### GPU Servers (Only for Nvidia GPU equipped nodes)

```
lspci | grep -i nvidia

# If the server has gpus, you can get info like this：
04:00.0 3D controller: NVIDIA Corporation Device 1b38 (rev a1)
82:00.0 3D controller: NVIDIA Corporation Device 1b38 (rev a1)
```



### Nvidia Driver Installation (Only for Nvidia GPU equipped nodes)

To make a clean installation, if you have requirements to upgrade GPU drivers. If nvidia driver/cuda has been installed before, They should be uninstalled firstly.

```
# uninstall cuda：
sudo /usr/local/cuda-10.0/bin/uninstall_cuda_10.0.pl

# uninstall nvidia-driver：
sudo /usr/bin/nvidia-uninstall
```

To check GPU version, install nvidia-detect

```
yum install nvidia-detect
# run 'nvidia-detect -v' to get reqired nvidia driver version：
nvidia-detect -v
Probing for supported NVIDIA devices...
[10de:13bb] NVIDIA Corporation GM107GL [Quadro K620]
This device requires the current xyz.nm NVIDIA driver kmod-nvidia
[8086:1912] Intel Corporation HD Graphics 530
An Intel display controller was also detected
```

Pay attention to `This device requires the current xyz.nm NVIDIA driver kmod-nvidia`.
Download the installer like [NVIDIA-Linux-x86_64-390.87.run](https://www.nvidia.com/object/linux-amd64-display-archive.html).


Some preparatory work for nvidia driver installation. (This is follow normal Nvidia GPU driver installation, just put here for your convenience)

```
# It may take a while to update
yum -y update
yum -y install kernel-devel

yum -y install epel-release
yum -y install dkms

# Disable nouveau
vim /etc/default/grub
# Add the following configuration in “GRUB_CMDLINE_LINUX” part
rd.driver.blacklist=nouveau nouveau.modeset=0

# Generate configuration
grub2-mkconfig -o /boot/grub2/grub.cfg

vim /etc/modprobe.d/blacklist.conf
# Add confiuration:
blacklist nouveau

mv /boot/initramfs-$(uname -r).img /boot/initramfs-$(uname -r)-nouveau.img
dracut /boot/initramfs-$(uname -r).img $(uname -r)
reboot
```

Check whether nouveau is disabled

```
lsmod | grep nouveau  # return null

# install nvidia driver
sh NVIDIA-Linux-x86_64-390.87.run
```

Some options during the installation

```
Install NVIDIA's 32-bit compatibility libraries (Yes)
centos Install NVIDIA's 32-bit compatibility libraries (Yes)
Would you like to run the nvidia-xconfig utility to automatically update your X configuration file... (NO)
```


Check nvidia driver installation

```
nvidia-smi
```

Reference：
https://docs.nvidia.com/cuda/cuda-installation-guide-linux/index.html



### Docker Installation

We recommend to use Docker version >= 1.12.5, following steps are just for your reference. You can always to choose other approaches to install Docker.

```
yum -y update
yum -y install yum-utils
yum-config-manager --add-repo https://yum.dockerproject.org/repo/main/centos/7
yum -y update

# Show available packages
yum search --showduplicates docker-engine

# Install docker 1.12.5
yum -y --nogpgcheck install docker-engine-1.12.5*
systemctl start docker

chown hadoop:netease /var/run/docker.sock
chown hadoop:netease /usr/bin/docker
```

Reference：https://docs.docker.com/cs-engine/1.12/

### Docker Configuration

Add a file, named daemon.json, under the path of /etc/docker/. Please replace the variables of image_registry_ip, etcd_host_ip, localhost_ip, yarn_dns_registry_host_ip, dns_host_ip with specific ips according to your environments.

```
{
    "insecure-registries": ["${image_registry_ip}:5000"],
    "cluster-store":"etcd://${etcd_host_ip1}:2379,${etcd_host_ip2}:2379,${etcd_host_ip3}:2379",
    "cluster-advertise":"{localhost_ip}:2375",
    "dns": ["${yarn_dns_registry_host_ip}", "${dns_host_ip1}"],
    "hosts": ["tcp://{localhost_ip}:2375", "unix:///var/run/docker.sock"]
}
```

Restart docker daemon：

```
sudo systemctl restart docker
```



### Docker EE version

```bash
$ docker version

Client:
 Version:      1.12.5
 API version:  1.24
 Go version:   go1.6.4
 Git commit:   7392c3b
 Built:        Fri Dec 16 02:23:59 2016
 OS/Arch:      linux/amd64

Server:
 Version:      1.12.5
 API version:  1.24
 Go version:   go1.6.4
 Git commit:   7392c3b
 Built:        Fri Dec 16 02:23:59 2016
 OS/Arch:      linux/amd64
```

### Nvidia-docker Installation (Only for Nvidia GPU equipped nodes)

Submarine depends on nvidia-docker 1.0 version

```
wget -P /tmp https://github.com/NVIDIA/nvidia-docker/releases/download/v1.0.1/nvidia-docker-1.0.1-1.x86_64.rpm
sudo rpm -i /tmp/nvidia-docker*.rpm
# Start nvidia-docker
sudo systemctl start nvidia-docker

# Check nvidia-docker status：
systemctl status nvidia-docker

# Check nvidia-docker log：
journalctl -u nvidia-docker

# Test nvidia-docker-plugin
curl http://localhost:3476/v1.0/docker/cli
```

According to `nvidia-driver` version, add folders under the path of  `/var/lib/nvidia-docker/volumes/nvidia_driver/`

```
mkdir /var/lib/nvidia-docker/volumes/nvidia_driver/390.87
# 390.8 is nvidia driver version

mkdir /var/lib/nvidia-docker/volumes/nvidia_driver/390.87/bin
mkdir /var/lib/nvidia-docker/volumes/nvidia_driver/390.87/lib64

cp /usr/bin/nvidia* /var/lib/nvidia-docker/volumes/nvidia_driver/390.87/bin
cp /usr/lib64/libcuda* /var/lib/nvidia-docker/volumes/nvidia_driver/390.87/lib64
cp /usr/lib64/libnvidia* /var/lib/nvidia-docker/volumes/nvidia_driver/390.87/lib64

# Test with nvidia-smi
nvidia-docker run --rm nvidia/cuda:9.0-devel nvidia-smi
```

Test docker, nvidia-docker, nvidia-driver installation

```
# Test 1
nvidia-docker run -rm nvidia/cuda nvidia-smi
```

```
# Test 2
nvidia-docker run -it tensorflow/tensorflow:1.9.0-gpu bash
# In docker container
python
import tensorflow as tf
tf.test.is_gpu_available()
```

[The way to uninstall nvidia-docker 1.0](https://github.com/nvidia/nvidia-docker/wiki/Installation-(version-2.0))

Reference:
https://github.com/NVIDIA/nvidia-docker/tree/1.0


### Tensorflow Image

How to build a Tensorflow Image, please refer to [WriteDockerfileTF.md](WriteDockerfileTF.md)


### Test tensorflow in a docker container

After docker image is built, we can check
Tensorflow environments before submitting a yarn job.

```shell
$ docker run -it ${docker_image_name} /bin/bash
# >>> In the docker container
$ python
$ python >> import tensorflow as tf
$ python >> tf.__version__
```

If there are some errors, we could check the following configuration.

1. LD_LIBRARY_PATH environment variable

   ```
   echo $LD_LIBRARY_PATH
   /usr/local/cuda/extras/CUPTI/lib64:/usr/local/nvidia/lib:/usr/local/nvidia/lib64
   ```

2. The location of libcuda.so.1, libcuda.so

   ```
   ls -l /usr/local/nvidia/lib64 | grep libcuda.so
   ```


## Hadoop Installation

### Get Hadoop Release
You can either get Hadoop release binary or compile from source code. Please follow the guides from [Hadoop Homepage](https://hadoop.apache.org/).
For hadoop cluster setup, please refer to [Hadoop Cluster Setup](https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/ClusterSetup.html)


### Start yarn services

```
YARN_LOGFILE=resourcemanager.log ./sbin/yarn-daemon.sh start resourcemanager
YARN_LOGFILE=nodemanager.log ./sbin/yarn-daemon.sh start nodemanager
YARN_LOGFILE=timeline.log ./sbin/yarn-daemon.sh start timelineserver
YARN_LOGFILE=mr-historyserver.log ./sbin/mr-jobhistory-daemon.sh start historyserver
```

### Test with a MR wordcount job

```
./bin/hadoop jar /home/hadoop/hadoop-current/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.2.0-SNAPSHOT.jar wordcount /tmp/wordcount.txt /tmp/wordcount-output4
```

## Yarn Configurations for GPU (Only if Nvidia GPU is used)

### GPU configurations for both resourcemanager and nodemanager

Add the yarn resource configuration file, named resource-types.xml

   ```
   <configuration>
     <property>
       <name>yarn.resource-types</name>
       <value>yarn.io/gpu</value>
     </property>
   </configuration>
   ```

#### GPU configurations for resourcemanager

The scheduler used by resourcemanager must be  capacity scheduler, and yarn.scheduler.capacity.resource-calculator in  capacity-scheduler.xml should be DominantResourceCalculator

   ```
   <configuration>
     <property>
       <name>yarn.scheduler.capacity.resource-calculator</name>
       <value>org.apache.hadoop.yarn.util.resource.DominantResourceCalculator</value>
     </property>
   </configuration>
   ```

#### GPU configurations for nodemanager

Add configurations in yarn-site.xml

   ```
   <configuration>
     <property>
       <name>yarn.nodemanager.resource-plugins</name>
       <value>yarn.io/gpu</value>
     </property>
   </configuration>
   ```

Add configurations in container-executor.cfg

   ```
   [docker]
   ...
   # Add configurations in `[docker]` part：
   # /usr/bin/nvidia-docker is the path of nvidia-docker command
   # nvidia_driver_375.26 means that nvidia driver version is <version>. nvidia-smi command can be used to check the version
   docker.allowed.volume-drivers=/usr/bin/nvidia-docker
   docker.allowed.devices=/dev/nvidiactl,/dev/nvidia-uvm,/dev/nvidia-uvm-tools,/dev/nvidia1,/dev/nvidia0
   docker.allowed.ro-mounts=nvidia_driver_<version>

   [gpu]
   module.enabled=true

   [cgroups]
   # /sys/fs/cgroup is the cgroup mount destination
   # /hadoop-yarn is the path yarn creates by default
   root=/sys/fs/cgroup
   yarn-hierarchy=/hadoop-yarn
   ```

## Tensorflow Job with yarn runtime.

### Run a tensorflow job in a zipped python virtual environment

Refer to build_python_virtual_env.sh in the directory of
${SUBMARINE_REPO_PATH}/dev-support/mini-submarine/submarine/ to build a zipped python virtual
environment. ${SUBMARINE_REPO_PATH} indicates submarine repo location.
The generated zipped file can be named myvenv.zip.

Copy ${SUBMARINE_REPO_PATH}/dev-support/mini-submarine/submarine/run_submarine_mnist_tony.sh
to the server on which you submit jobs. And modify the variables, SUBMARINE_VERSION, HADOOP_VERSION, SUBMARINE_PATH,
HADOOP_CONF_PATH and MNIST_PATH in it, according to your environment. If Kerberos
is enabled, please delete the parameter, --insecure, in the command.

Run a distributed tensorflow job.
```
./run_submarine_mnist_tony.sh -d http://yann.lecun.com/exdb/mnist/
```
The parameter -d is used to specify the url from which we can get the mnist data.

### Run a tensorflow job in a docker container(TODO)


## Yarn Service Runtime Requirement (Deprecated)

The function of "yarn native service" is available since hadoop 3.1.0.
Submarine supports to utilize yarn native service to submit a ML job. However, as
there are several other components required. It is hard to enable
and maintain the components. So yarn service runtime is deprecated since submarine 0.3.0.
We recommend to use YarnRuntime instead. If you still want to enable it, please
follow these steps.

### Etcd Installation

etcd is a distributed reliable key-value store for the most critical data of a distributed system, Registration and discovery of services used in containers.
You can also choose alternatives like zookeeper, Consul.

To install Etcd on specified servers, we can run Submarine-installer/install.sh

```shell
$ ./Submarine-installer/install.sh
# Etcd status
systemctl status Etcd.service
```

Check Etcd cluster health

```shell
$ etcdctl cluster-health
member 3adf2673436aa824 is healthy: got healthy result from http://${etcd_host_ip1}:2379
member 85ffe9aafb7745cc is healthy: got healthy result from http://${etcd_host_ip2}:2379
member b3d05464c356441a is healthy: got healthy result from http://${etcd_host_ip3}:2379
cluster is healthy

$ etcdctl member list
3adf2673436aa824: name=etcdnode3 peerURLs=http://${etcd_host_ip1}:2380 clientURLs=http://${etcd_host_ip1}:2379 isLeader=false
85ffe9aafb7745cc: name=etcdnode2 peerURLs=http://${etcd_host_ip2}:2380 clientURLs=http://${etcd_host_ip2}:2379 isLeader=false
b3d05464c356441a: name=etcdnode1 peerURLs=http://${etcd_host_ip3}:2380 clientURLs=http://${etcd_host_ip3}:2379 isLeader=true
```

### Calico Installation

Calico creates and manages a flat three-tier network, and each container is assigned a routable ip. We just add the steps here for your convenience.
You can also choose alternatives like Flannel, OVS.

To install Calico on specified servers, we can run Submarine-installer/install.sh

```
systemctl start calico-node.service
systemctl status calico-node.service
```

#### Check Calico Network

```shell
# Run the following command to show the all host status in the cluster except localhost.
$ calicoctl node status
Calico process is running.

IPv4 BGP status
+---------------+-------------------+-------+------------+-------------+
| PEER ADDRESS  |     PEER TYPE     | STATE |   SINCE    |    INFO     |
+---------------+-------------------+-------+------------+-------------+
| ${host_ip1} | node-to-node mesh | up    | 2018-09-21 | Established |
| ${host_ip2} | node-to-node mesh | up    | 2018-09-21 | Established |
| ${host_ip3} | node-to-node mesh | up    | 2018-09-21 | Established |
+---------------+-------------------+-------+------------+-------------+

IPv6 BGP status
No IPv6 peers found.
```

Create containers to validate calico network

```
docker network create --driver calico --ipam-driver calico-ipam calico-network
docker run --net calico-network --name workload-A -tid busybox
docker run --net calico-network --name workload-B -tid busybox
docker exec workload-A ping workload-B
```

### Enable calico network for docker container
Set yarn-site.xml to use bridge for docker container

```
<property>
    <name>yarn.nodemanager.runtime.linux.docker.default-container-network</name>
    <value>calico-network</value>
  </property>
  <property>
    <name>yarn.nodemanager.runtime.linux.allowed-runtimes</name>
    <value>default,docker</value>
  </property>
  <property>
    <name>yarn.nodemanager.runtime.linux.docker.allowed-container-networks</name>
    <value>host,none,bridge,calico-network</value>
  </property>
```

Add calico-network to container-executor.cfg

```
docker.allowed.networks=bridge,host,none,calico-network
```

Then restart all nodemanagers.

### Start yarn registery dns service

Yarn registry nds server exposes existing service-discovery information via DNS
and enables docker containers to IP mappings. By using it, the containers of a 
ML job knows how to communicate with each other.

Please specify a server to start yarn registery dns service. For details please
refer to [Registry DNS Server](http://hadoop.apache.org/docs/r3.1.0/hadoop-yarn/hadoop-yarn-site/yarn-service/RegistryDNS.html)

```
sudo YARN_LOGFILE=registrydns.log ./yarn-daemon.sh start registrydns
```

### Run a submarine job with yarn service runtime

Please refer to [Running Distributed CIFAR 10 Tensorflow Job_With_Yarn_Service_Runtime](RunningDistributedCifar10TFJobsWithYarnService.md)