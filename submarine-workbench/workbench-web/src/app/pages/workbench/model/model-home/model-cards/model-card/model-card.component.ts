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

import { Component, Input, OnInit } from '@angular/core';
import { ModelInfo } from '@submarine/interfaces/model-info';
@Component({
  selector: 'submarine-model-card',
  templateUrl: './model-card.component.html',
  styleUrls: ['./model-card.component.scss'],
})
export class ModelCardComponent implements OnInit {
  @Input() card: ModelInfo;
  description: string;

  constructor() {}

  ngOnInit() {
    if (this.card.description.length > 15) {
      this.description = this.card.description.substring(0,15) + "...";
    }
    else {
      this.description = this.card.description;
    }
  }
}
