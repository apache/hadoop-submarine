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

# Run PyTorch Experiment Guide

## Experiment Spec
The experiment is represented in [JSON](https://www.json.org) or [YAML](https://yaml.org) format.

**YAML Format:**
```yaml
meta:
  name: pytorch-mnist-yaml
  namespace: default
  framework: PyTorch
  cmd: python /var/mnist.py --backend gloo
  envVars:
    ENV_1: ENV1
environment:
  image: apache/submarine:pytorch-dist-mnist-1.0
spec:
  Master:
    replicas: 1
    resources: cpu=1,memory=1024M
  Worker:
    replicas: 1
    resources: cpu=1,memory=1024M
```

**JSON Format:**
```json
{
  "meta": {
    "name": "pytorch-mnist-json",
    "namespace": "default",
    "framework": "PyTorch",
    "cmd": "python /var/mnist.py --backend gloo",
    "envVars": {
      "ENV_1": "ENV1"
    }
  },
  "environment": {
    "image": "apache/submarine:pytorch-dist-mnist-1.0"
  },
  "spec": {
    "Master": {
      "replicas": 1,
      "resources": "cpu=1,memory=1024M"
    },
    "Worker": {
      "replicas": 1,
      "resources": "cpu=1,memory=1024M"
    }
  }
}

```

## Create Experiment by REST API
`POST /api/v1/experiment`

**Example Request**
```sh
curl -X POST -H "Content-Type: application/json" -d '
{
  "meta": {
    "name": "pytorch-mnist-json",
    "namespace": "default",
    "framework": "PyTorch",
    "cmd": "python /var/mnist.py --backend gloo",
    "envVars": {
      "ENV_1": "ENV1"
    }
  },
  "environment": {
    "image": "apache/submarine:pytorch-dist-mnist-1.0"
  },
  "spec": {
    "Master": {
      "replicas": 1,
      "resources": "cpu=1,memory=1024M"
    },
    "Worker": {
      "replicas": 1,
      "resources": "cpu=1,memory=1024M"
    }
  }
}
' http://127.0.0.1:8080/api/v1/experiment
```

**Example Response:**
```json
{
    "status": "OK",
    "code": 200,
    "result": {
        "experimentId": "experiment_1592057447228_0002",
        "name": "mnist",
        "uid": "38e39dcd-77d4-11ea-8dbb-0242ac110003",
        "status": "Accepted",
        "acceptedTime": "2020-06-13T22:19:29.000+08:00",
        "spec": {
            "meta": {
                "name": "pytorch-mnist-json",
                "namespace": "default",
                "framework": "PyTorch",
                "cmd": "python /var/mnist.py --backend gloo",
                "envVars": {
                    "ENV_1": "ENV1"
                }
            },
            "environment": {
                "image": "apache/submarine:pytorch-dist-mnist-1.0"
            },
            "spec": {
                "Master": {
                    "replicas": 1,
                    "resources": "cpu=1,memory=1024M"
                },
                "Worker": {
                    "replicas": 1,
                    "resources": "cpu=1,memory=1024M"
                }
            }
        }
    }
}
```

More info see [Experiment API Reference](api/experiment.md).
