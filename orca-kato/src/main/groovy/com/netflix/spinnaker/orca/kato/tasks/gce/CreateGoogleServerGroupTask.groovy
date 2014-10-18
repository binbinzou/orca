/*
 * Copyright 2014 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.orca.kato.tasks.gce

import com.netflix.spinnaker.orca.DefaultTaskResult
import com.netflix.spinnaker.orca.PipelineStatus
import com.netflix.spinnaker.orca.Task
import com.netflix.spinnaker.orca.TaskResult
import com.netflix.spinnaker.orca.kato.api.KatoService
import com.netflix.spinnaker.orca.kato.api.TaskId
import com.netflix.spinnaker.orca.kato.api.ops.gce.DeployGoogleServerGroupOperation
import com.netflix.spinnaker.orca.pipeline.Stage
import org.springframework.beans.factory.annotation.Autowired

class CreateGoogleServerGroupTask implements Task {

  @Autowired
  KatoService kato

  @Override
  TaskResult execute(Stage stage) {
    def operation = convert(stage)
    def taskId = deploy(operation)
    new DefaultTaskResult(PipelineStatus.SUCCEEDED,
      [
        "notification.type"  : "createdeploy",
        "kato.last.task.id"  : taskId,
        "kato.task.id"       : taskId, // TODO retire this.
        "deploy.account.name": operation.credentials,
      ]
    )
  }

  DeployGoogleServerGroupOperation convert(Stage stage) {
    new DeployGoogleServerGroupOperation(
      application: stage.context.application,
      stack: stage.context.stack,
      freeFormDetails: stage.context.freeFormDetails,
      image: stage.context.image,
      type: stage.context.machineType,
      zone: stage.context.zones ? stage.context.zones[0] : null,
      initialNumReplicas: stage.context.capacity.desired,
      credentials: stage.context.credentials
    )
  }

  private TaskId deploy(DeployGoogleServerGroupOperation deployOperation) {
    kato.requestOperations([[basicGoogleDeployDescription: deployOperation]]).toBlocking().first()
  }
}
