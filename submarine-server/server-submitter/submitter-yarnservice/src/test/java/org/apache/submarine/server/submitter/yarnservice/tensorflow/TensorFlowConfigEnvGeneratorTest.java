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

package org.apache.submarine.server.submitter.yarnservice.tensorflow;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Class to test some functionality of {@link TensorFlowConfigEnvGenerator}.
 */
public class TensorFlowConfigEnvGeneratorTest {

  @Test
  public void testSimpleDistributedTFConfigGeneratorWorker() {
    String json = TensorFlowConfigEnvGenerator.getTFConfigEnv("worker", 5, 3,
            "wtan", "tf-job-001", "example.com");
    assertEquals(json, "{\\\"cluster\\\":{\\\"master\\\":[\\\"master-0.wtan" +
        ".tf-job-001.example.com:8000\\\"],\\\"worker\\\":[\\\"worker-0.wtan" +
        ".tf-job-001.example.com:8000\\\",\\\"worker-1.wtan.tf-job-001" +
        ".example.com:8000\\\",\\\"worker-2.wtan.tf-job-001.example" +
        ".com:8000\\\",\\\"worker-3.wtan.tf-job-001.example.com:8000\\\"]," +
        "\\\"ps\\\":[\\\"ps-0.wtan.tf-job-001.example.com:8000\\\",\\\"ps-1" +
        ".wtan.tf-job-001.example.com:8000\\\",\\\"ps-2.wtan.tf-job-001" +
        ".example.com:8000\\\"]},\\\"task\\\":{ \\\"type\\\":\\\"worker\\\", " +
        "\\\"index\\\":$_TASK_INDEX},\\\"environment\\\":\\\"cloud\\\"}");
  }

  @Test
  public void testSimpleDistributedTFConfigGeneratorMaster() {
    String json = TensorFlowConfigEnvGenerator.getTFConfigEnv("master", 2, 1,
        "wtan", "tf-job-001", "example.com");
    assertEquals(json, "{\\\"cluster\\\":{\\\"master\\\":[\\\"master-0.wtan" +
        ".tf-job-001.example.com:8000\\\"],\\\"worker\\\":[\\\"worker-0.wtan" +
        ".tf-job-001.example.com:8000\\\"],\\\"ps\\\":[\\\"ps-0.wtan" +
        ".tf-job-001.example.com:8000\\\"]},\\\"task\\\":{ " +
        "\\\"type\\\":\\\"master\\\", \\\"index\\\":$_TASK_INDEX}," +
        "\\\"environment\\\":\\\"cloud\\\"}");
  }

  @Test
  public void testSimpleDistributedTFConfigGeneratorPS() {
    String json = TensorFlowConfigEnvGenerator.getTFConfigEnv("ps", 5, 3,
        "wtan", "tf-job-001", "example.com");
    assertEquals(json, "{\\\"cluster\\\":{\\\"master\\\":[\\\"master-0.wtan" +
        ".tf-job-001.example.com:8000\\\"],\\\"worker\\\":[\\\"worker-0.wtan" +
        ".tf-job-001.example.com:8000\\\",\\\"worker-1.wtan.tf-job-001" +
        ".example.com:8000\\\",\\\"worker-2.wtan.tf-job-001.example" +
        ".com:8000\\\",\\\"worker-3.wtan.tf-job-001.example.com:8000\\\"]," +
        "\\\"ps\\\":[\\\"ps-0.wtan.tf-job-001.example.com:8000\\\",\\\"ps-1" +
        ".wtan.tf-job-001.example.com:8000\\\",\\\"ps-2.wtan.tf-job-001" +
        ".example.com:8000\\\"]},\\\"task\\\":{ \\\"type\\\":\\\"ps\\\", " +
        "\\\"index\\\":$_TASK_INDEX},\\\"environment\\\":\\\"cloud\\\"}");
  }
}
