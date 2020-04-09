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

package org.apache.submarine.server.api.job;

import org.apache.submarine.commons.utils.SubmarineConfiguration;
import org.apache.submarine.commons.utils.exception.SubmarineRuntimeException;
import org.apache.submarine.server.api.spec.JobSpec;

/**
 * The submitter should implement this interface.
 */
public interface JobSubmitter {
  /**
   * Initialize the submitter related code
   */
  void initialize(SubmarineConfiguration conf);

  /**
   * Get the submitter type which is the unique identifier.
   *
   * @return unique identifier
   */
  String getSubmitterType();

  /**
   * Create job with job spec
   * @param jobSpec job spec
   * @return object
   * @throws SubmarineRuntimeException running error
   */
  Job createJob(JobSpec jobSpec) throws SubmarineRuntimeException;

  /**
   * Find job by job spec
   * @param jobSpec job spec
   * @return object
   * @throws SubmarineRuntimeException running error
   */
  Job findJob(JobSpec jobSpec) throws SubmarineRuntimeException;

  /**
   * Patch job with job spec
   * @param jobSpec job spec
   * @return object
   * @throws SubmarineRuntimeException running error
   */
  Job patchJob(JobSpec jobSpec) throws SubmarineRuntimeException;

  /**
   * Delete job by job spec
   * @param jobSpec job spec
   * @return object
   * @throws SubmarineRuntimeException running error
   */
  Job deleteJob(JobSpec jobSpec) throws SubmarineRuntimeException;
}
