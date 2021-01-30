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

import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormArray, FormBuilder, Validators } from '@angular/forms';
import { EnvironmentService } from '@submarine/services/environment-services/environment.service';
import { ExperimentValidatorService } from '@submarine/services/experiment.validator.service';
import { NzMessageService } from 'ng-zorro-antd';

@Component({
  selector: 'submarine-environment-form',
  templateUrl: './environment-form.component.html',
  styleUrls: ['./environment-form.component.scss'],
})
export class EnvironmentFormComponent implements OnInit {
  @Output() private updater = new EventEmitter<string>();

  isVisible: boolean;
  environmentForm;

  constructor(
    private fb: FormBuilder,
    private experimentValidatorService: ExperimentValidatorService,
    private environmentService: EnvironmentService,
    private nzMessageService: NzMessageService
  ) {}

  ngOnInit() {
    this.environmentForm = this.fb.group({
      environmentName: [null, Validators.required],
      dockerImage: [null, Validators.required],
      name: [null, Validators.required],
      channels: this.fb.array([]),
      dependencies: this.fb.array([]),
    });
  }

  initModal() {
    this.isVisible = true;
    this.initFormStatus();
  }

  sendUpdate() {
    this.updater.emit('Update List');
  }

  get environmentName() {
    return this.environmentForm.get('environmentName');
  }

  get dockerImage() {
    return this.environmentForm.get('dockerImage');
  }

  get name() {
    return this.environmentForm.get('name');
  }

  get channels() {
    return this.environmentForm.get('channels') as FormArray;
  }

  get dependencies() {
    return this.environmentForm.get('dependencies') as FormArray;
  }

  initFormStatus() {
    this.isVisible = true;
    this.environmentName.reset();
    this.dockerImage.reset();
    this.name.reset();
    this.channels.clear();
    this.dependencies.clear();
  }

  checkStatus() {
    return (
      this.environmentName.invalid ||
      this.dockerImage.invalid ||
      this.name.invalid ||
      this.channels.invalid ||
      this.dependencies.invalid
    );
  }

  closeModal() {
    this.isVisible = false;
  }

  addChannel() {
    this.channels.push(this.fb.control(null, Validators.required));
  }

  addDependencies() {
    this.dependencies.push(this.fb.control(null, Validators.required));
  }

  deleteItem(arr: FormArray, index: number) {
    arr.removeAt(index);
  }

  createEnvironment() {
    this.isVisible = false;
    const newEnvironmentSpec = this.createEnvironmentSpec();
    this.environmentService.createEnvironment(newEnvironmentSpec).subscribe(
      () => {
        this.nzMessageService.success('Create Environment Success!');
        this.sendUpdate();
      },
      (err) => {
        this.nzMessageService.error(`${err}, please try again`, {
          nzPauseOnHover: true,
        });
      }
    );
  }

  createEnvironmentSpec() {
    const environmentSpec = {
      name: this.environmentForm.get('environmentName').value,
      dockerImage: this.environmentForm.get('dockerImage').value,
      kernelSpec: {
        name: this.environmentForm.get('name').value,
        channels: [],
        dependencies: [],
      },
    };

    for (const channel of this.channels.controls) {
      environmentSpec.kernelSpec.channels.push(channel.value);
    }

    for (const dependency of this.dependencies.controls) {
      environmentSpec.kernelSpec.dependencies.push(dependency.value);
    }

    return environmentSpec;
  }
}
