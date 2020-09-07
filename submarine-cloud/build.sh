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
set -euo pipefail

if [ -L ${BASH_SOURCE-$0} ]; then
  PWD=$(dirname $(readlink "${BASH_SOURCE-$0}"))
else
  PWD=$(dirname ${BASH_SOURCE-$0})
fi
export CURRENT_PATH=$(cd "${PWD}">/dev/null; pwd)
cd $CURRENT_PATH

echo "${1} submarine-cloud by docker ..."
if [[ "${1}"x == "test"x ]]; then
  echo "Test submarine-cloud by docker ..."
elif [ "${1}"x == "clean"x ]; then
  rm -rf ./bin
else
  docker run --rm -v "$CURRENT_PATH":/go/src/submarine-cloud -w /go/src/submarine-cloud -e GOOS="${GOOS:-darwin}" -e GOARCH="${GOARCH:-amd64}" apache/submarine:build /bin/sh -c "make ${1} && chown -R $(id -u):$(id -g) ./bin"
fi;
