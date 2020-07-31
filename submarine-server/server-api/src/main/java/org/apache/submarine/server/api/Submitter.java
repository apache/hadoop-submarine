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

package org.apache.submarine.server.api;

import org.apache.submarine.commons.utils.SubmarineConfiguration;
import org.apache.submarine.commons.utils.exception.SubmarineRuntimeException;
import org.apache.submarine.server.api.experiment.Experiment;
import org.apache.submarine.server.api.experiment.ExperimentLog;
import org.apache.submarine.server.api.notebook.Notebook;
import org.apache.submarine.server.api.spec.ExperimentSpec;
import org.apache.submarine.server.api.spec.NotebookSpec;

/**
 * The submitter should implement this interface.
 */
public interface Submitter {
  /**
   * Initialize the submitter related code
   */
  void initialize(SubmarineConfiguration conf);

  /**
   * Create experiment with spec
   * @param spec experiment spec
   * @return object
   * @throws SubmarineRuntimeException running error
   */
  Experiment createExperiment(ExperimentSpec spec) throws SubmarineRuntimeException;

  /**
   * Find experiment by spec
   * @param spec spec
   * @return object
   * @throws SubmarineRuntimeException running error
   */
  Experiment findExperiment(ExperimentSpec spec) throws SubmarineRuntimeException;

  /**
   * Patch one experiment with spec
   * @param spec spec
   * @return object
   * @throws SubmarineRuntimeException running error
   */
  Experiment patchExperiment(ExperimentSpec spec) throws SubmarineRuntimeException;

  /**
   * Delete experiment by spec
   * @param spec spec
   * @return object
   * @throws SubmarineRuntimeException running error
   */
  Experiment deleteExperiment(ExperimentSpec spec) throws SubmarineRuntimeException;

  /**
   * Get the pod log list in the job
   * @param spec spec
   * @param id experiment id
   * @return object
   * @throws SubmarineRuntimeException running error
   */
  ExperimentLog getExperimentLog(ExperimentSpec spec, String id) throws SubmarineRuntimeException;

  /**
   * Get the pod name list in the job
   * @param spec spec
   * @param id experiment id
   * @return object
   * @throws SubmarineRuntimeException running error
   */
  ExperimentLog getExperimentLogName(ExperimentSpec spec, String id) throws SubmarineRuntimeException;

  /**
   * Create a notebook with spec
   * @param spec notebook spec
   * @return object
   * @throws SubmarineRuntimeException running error
   */
  Notebook createNotebook(NotebookSpec spec) throws SubmarineRuntimeException;

  /**
   * Find a notebook with spec
   * @param spec spec
   * @return object
   * @throws SubmarineRuntimeException running error
   */
  Notebook findNotebook(NotebookSpec spec) throws SubmarineRuntimeException;

  /**
   * Delete a notebook with spec
   * @param spec spec
   * @return object
   * @throws SubmarineRuntimeException running error
   */
  Notebook deleteNotebook(NotebookSpec spec) throws SubmarineRuntimeException;
}
