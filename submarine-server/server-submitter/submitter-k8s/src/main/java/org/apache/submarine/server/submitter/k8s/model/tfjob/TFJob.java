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

package org.apache.submarine.server.submitter.k8s.model.tfjob;

import com.google.gson.annotations.SerializedName;
import org.apache.submarine.server.submitter.k8s.model.MLJob;

/**
 * It's the tf-operator's entry model.
 */
public class TFJob extends MLJob {

  @SerializedName("spec")
  private TFJobSpec spec;

  public TFJob() {
    setApiVersion("kubeflow.org/v1");
    setKind("TFJob");
  }

  /**
   * Get the job spec which contains all the info for TFJob.
   * @return job spec
   */
  public TFJobSpec getSpec() {
    return spec;
  }

  /**
   * Set the spec, the entry of the TFJob
   * @param spec job spec
   */
  public void setSpec(TFJobSpec spec) {
    this.spec = spec;
  }
}
