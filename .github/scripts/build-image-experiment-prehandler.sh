#!/usr/bin/env bash
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

SUBMARINE_VERSION="0.8.0-RC0"
echo "Build Image apache/submarine:experiment-prehandler-${SUBMARINE_VERSION}"
echo "Execute ./dev-support/docker-images/experiment-prehandler/build.sh"
./dev-support/docker-images/experiment-prehandler/build.sh
kind load docker-image apache/submarine:experiment-prehandler-${SUBMARINE_VERSION}
docker rmi apache/submarine:experiment-prehandler-${SUBMARINE_VERSION}