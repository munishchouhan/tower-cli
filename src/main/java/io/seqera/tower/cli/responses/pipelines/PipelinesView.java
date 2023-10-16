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

package io.seqera.tower.cli.responses.pipelines;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.seqera.tower.JSON;
import io.seqera.tower.cli.responses.Response;
import io.seqera.tower.cli.utils.ModelHelper;
import io.seqera.tower.cli.utils.TableList;
import io.seqera.tower.model.Launch;
import io.seqera.tower.model.PipelineDbDto;
import io.seqera.tower.model.WorkflowLaunchRequest;

import java.io.PrintWriter;

import static io.seqera.tower.cli.utils.FormatHelper.formatLabels;
import static io.seqera.tower.cli.utils.FormatHelper.formatPipelineId;

public class PipelinesView extends Response {

    public final String workspaceRef;
    public final PipelineDbDto info;
    public final Launch launch;

    @JsonIgnore
    private final String baseWorkspaceUrl;

    public PipelinesView(String workspaceRef, PipelineDbDto info, Launch launch, String baseWorkspaceUrl) {
        this.workspaceRef = workspaceRef;
        this.info = info;
        this.launch = launch;
        this.baseWorkspaceUrl = baseWorkspaceUrl;
    }

    @Override
    public void toString(PrintWriter out) {
        String configJson = "";
        try {
            WorkflowLaunchRequest request = ModelHelper.createLaunchRequest(launch);
            configJson = new JSON().getContext(WorkflowLaunchRequest.class).writerWithDefaultPrettyPrinter().writeValueAsString(request);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        out.println(ansi(String.format("%n  @|bold Pipeline at %s workspace:|@%n", workspaceRef)));
        TableList table = new TableList(out, 2);
        table.setPrefix("    ");
        table.addRow("ID", formatPipelineId(info.getPipelineId(), baseWorkspaceUrl));
        table.addRow("Name", info.getName());
        table.addRow("Description", info.getDescription());
        table.addRow("Repository", info.getRepository());
        table.addRow("Compute env.", launch.getComputeEnv() == null ? "(not defined)" : launch.getComputeEnv().getName());
        table.addRow("Labels", info.getLabels() == null || info.getLabels().isEmpty() ? "No labels found" : formatLabels(info.getLabels()));
        table.print();

        out.println(String.format("%n  Configuration:%n%n%s%n", configJson.replaceAll("(?m)^", "     ")));
    }
}
