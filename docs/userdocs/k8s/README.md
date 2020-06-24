<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

# Submarine on K8s
Submarine for K8s supports distributed TensorFlow and PyTorch.

Submarine can run on K8s >= 1.14, supports features like GPU isolation.

## Install Submarine
Submarine can be deployed on any K8s environment if version matches. If you don't have a running K8s, you can follow the steps to set up a K8s using [kind, Kubernetes-in-Docker](https://kind.sigs.k8s.io/) for testing purpose, we provides simple [tutorial](kind.md).

### Use Helm Charts
After you have an up-and-running K8s, you can follow [Submarine Helm Charts Guide](helm.md) to deploy Submarine services on K8s cluster in minutes.

## Use Submarine

### Model training (experiment) on K8s

#### Prepare Python Environment to run Submarine SDK

Submarine SDK assumes Python3.7+ is ready.
It's better to use a new Python environment created by `Anoconda` or Python `virtualenv` to try this to avoid trouble to existing Python environment.
A sample Python virtual env can be setup like this:
```bash
wget https://files.pythonhosted.org/packages/33/bc/fa0b5347139cd9564f0d44ebd2b147ac97c36b2403943dbee8a25fd74012/virtualenv-16.0.0.tar.gz
tar xf virtualenv-16.0.0.tar.gz

# Make sure to install using Python 3
python3 virtualenv-16.0.0/virtualenv.py venv
. venv/bin/activate
```

#### With Submarine SDK (Recommended)

- Install SDK from pypi.org

Starting from 0.4.0, Submarine provides Python SDK. Please change it to a proper version needed.

```bash
pip install submarine-sdk==0.4.0
```

- Install SDK from source code

Please first clone code from github or go to `http://submarine.apache.org/download.html` to download released source code.
```bash
git clone https://github.com/apache/submarine.git
git checkout <correct release tag/branch>
cd submarine/submarine-sdk/pysubmarine
pip install .
```

- Run with Submarine Python SDK

Assuming you've installed submarine on K8s and forward the service to localhost, now you can open a Python shell, Jupyter notebook or any tools with Submarine SDK installed.

Follow [SDK experiment example](../../../submarine-sdk/pysubmarine/example/submarine_experiment_sdk.ipynb) to try the SDK.

#### With REST API
- [Run model training using Tensorflow](run-tensorflow-experiment.md)
- [Run model training using PyTorch](run-pytorch-experiment.md)
- [Experiment API Reference](api/experiment.md)

