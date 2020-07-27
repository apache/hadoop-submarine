/*!
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

import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'submarine-environment',
  templateUrl: './environment.component.html',
  styleUrls: ['./environment.component.scss']
})
export class EnvironmentComponent implements OnInit {
  constructor() {}
  environmentList = [
    {
      environmentName: 'my-submarine-env',
      environmentId: 'environment_1586156073228_0001',
      dockerImage: 'continuumio/anaconda3',
      kernelName: 'team_default_python_3.7',
      kernelChannels: 'defaults',
      kernelDependencies: ['_ipyw_jlab_nb_ext_conf=0.1.0=py37_0', 'alabaster=0.7.12=py37_0']
    },
    {
      environmentName: 'my-submarine-env-2',
      environmentId: 'environment_1586156073228_0002',
      dockerImage: 'continuumio/miniconda',
      kernelName: 'team_default_python_3.8',
      kernelChannels: 'defaults',
      kernelDependencies: ['_ipyw_jlab_nb_ext_conf=0.1.0=py38_0']
    }
  ];

  ngOnInit() {}
}
