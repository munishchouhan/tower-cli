/*
 * Copyright 2021-2023, Seqera.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.seqera.tower.cli.responses.runs;

import io.seqera.tower.cli.responses.Response;

public class RunSubmited extends Response {

    public final String workflowId;

    public final String workflowUrl;

    public final Long workspaceId;
    public final String workspaceRef;

    public RunSubmited(String workflowId, Long workspaceId, String baseWorkspaceUrl, String workspaceRef) {
        this.workflowId = workflowId;
        this.workflowUrl = String.format("%s/watch/%s", baseWorkspaceUrl, workflowId);
        this.workspaceId = workspaceId;
        this.workspaceRef = workspaceRef;
    }

    @Override
    public String toString() {
        return ansi(String.format("%n  @|yellow Workflow %s submitted at %s workspace.|@%n%n    @|bold %s|@%n", workflowId, workspaceRef, workflowUrl));
    }

}
