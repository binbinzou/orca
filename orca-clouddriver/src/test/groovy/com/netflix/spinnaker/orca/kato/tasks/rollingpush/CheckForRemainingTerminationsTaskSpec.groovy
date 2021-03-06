/*
 * Copyright 2015 Netflix, Inc.
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

package com.netflix.spinnaker.orca.kato.tasks.rollingpush

import com.netflix.spinnaker.orca.ExecutionStatus
import com.netflix.spinnaker.orca.pipeline.model.Orchestration
import com.netflix.spinnaker.orca.pipeline.model.Stage
import spock.lang.Specification

class CheckForRemainingTerminationsTaskSpec extends Specification {

  def 'should redirect when there are remaining terminationInstances'() {
    given:
    def task = new CheckForRemainingTerminationsTask()
    def context = [
      terminationInstanceIds: terminationInstanceIds
    ]
    def stage = new Stage<>(new Orchestration(), 'check', context)


    when:
    def result = task.execute(stage)

    then:

    result.status == expectedStatus

    where:
    terminationInstanceIds | expectedStatus
    null                   | ExecutionStatus.SUCCEEDED
    []                     | ExecutionStatus.SUCCEEDED
    ['i-12345']            | ExecutionStatus.REDIRECT
  }
}
