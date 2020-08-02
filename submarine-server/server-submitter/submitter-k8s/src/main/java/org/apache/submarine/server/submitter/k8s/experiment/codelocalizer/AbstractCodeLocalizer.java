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

package org.apache.submarine.server.submitter.k8s.experiment.codelocalizer;

import org.apache.submarine.server.api.exception.InvalidSpecException;

import io.kubernetes.client.models.V1EmptyDirVolumeSource;
import io.kubernetes.client.models.V1PodSpec;
import io.kubernetes.client.models.V1Volume;

public abstract class AbstractCodeLocalizer implements CodeLocalizer {

  private String url;
  
  public AbstractCodeLocalizer(String url) {
    this.url = url;
  }
  
  /**
   * @return the url
   */
  public String getUrl() {
    return url;
  }
  
  @Override
  public void localize(V1PodSpec podSpec) {
    V1Volume volume = new V1Volume();
    volume.setName("code-dir");
    volume.setEmptyDir(new V1EmptyDirVolumeSource());
    podSpec.addVolumesItem(volume);
  }

  public static CodeLocalizer getCodeLocalizer(String syncMode, String url)
      throws InvalidSpecException {
    if (syncMode.equals(CodeLocalizerModes.GIT.getMode())) {
      return GitCodeLocalizer.getGitCodeLocalizer(url);
    } else {
      return new DummyCodeLocalizer(url);
    }
  }

  public enum CodeLocalizerModes {

    GIT("git"), HDFS("hdfs"), NFS("nfs"), S3("s3");

    private final String mode;

    CodeLocalizerModes(String mode) {
      this.mode = mode;
    }

    public String getMode() {
      return this.mode;
    }
  }
}
