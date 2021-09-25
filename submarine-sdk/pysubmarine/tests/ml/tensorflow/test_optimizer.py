# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements. See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import pytest
import tensorflow as tf

from submarine.ml.tensorflow.optimizer import get_optimizer


@pytest.mark.skipif(tf.__version__ >= "2.0.0", reason="requires tf1")
def test_get_optimizer():
    optimizer_keys = ["adam", "adagrad", "momentum", "ftrl"]
    invalid_optimizer_keys = ["adddam"]

    for optimizer_key in optimizer_keys:
        get_optimizer(optimizer_key=optimizer_key, learning_rate=0.3)

    for invalid_optimizer_key in invalid_optimizer_keys:
        with pytest.raises(ValueError, match="Invalid optimizer_key :"):
            get_optimizer(optimizer_key=invalid_optimizer_key, learning_rate=0.3)
