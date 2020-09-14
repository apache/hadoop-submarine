#!/usr/bin/env bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
set -euo pipefail

readonly TRAEFIK_IMAGE="traefik:v2.2"

if [ -L "${BASH_SOURCE-$0}" ]; then
  PWD=$(dirname "$(readlink "${BASH_SOURCE-$0}")")
else
  PWD=$(dirname "${BASH_SOURCE-$0}")
fi
CURRENT_PATH=$(cd "${PWD}">/dev/null; pwd)
export CURRENT_PATH
export SUBMARINE_HOME=${CURRENT_PATH}/../..
# lib.sh use the ROOT variable
export ROOT="${SUBMARINE_HOME}/submarine-cloud/"
export KUBECONFIG="${HOME}/.kube/kind-config-${clusterName:-kind}"

# shellcheck source=./../../submarine-cloud/hack/lib.sh
source "${SUBMARINE_HOME}/submarine-cloud/hack/lib.sh"

###########################################
# Load local docker image into registry
# Globals:
#   KIND_BIN
# Arguments:
#   image
###########################################
function load_image_to_registry() {
  if [[ ! $(docker inspect "$1" > /dev/null) ]] ; then
    docker pull "$1"
  fi
  ${KIND_BIN} load docker-image "$1"
}

function main() {
  hack::ensure_kubectl
  echo "Setting up ingress on a kind cluster."
  load_image_to_registry "${TRAEFIK_IMAGE}"
  ${KUBECTL_BIN} apply -k "${CURRENT_PATH}/traefik"
}

main "@"
