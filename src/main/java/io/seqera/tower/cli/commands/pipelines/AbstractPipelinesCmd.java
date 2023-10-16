/*
 * Copyright 2023, Seqera.
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

package io.seqera.tower.cli.commands.pipelines;

import io.seqera.tower.ApiException;
import io.seqera.tower.cli.commands.AbstractApiCmd;
import io.seqera.tower.cli.exceptions.MultiplePipelinesFoundException;
import io.seqera.tower.cli.exceptions.PipelineNotFoundException;
import io.seqera.tower.model.ListPipelinesResponse;
import io.seqera.tower.model.PipelineDbDto;
import io.seqera.tower.model.PipelineQueryAttribute;
import picocli.CommandLine.Command;

import java.util.Collections;
import java.util.List;

@Command
public abstract class AbstractPipelinesCmd extends AbstractApiCmd {

    public AbstractPipelinesCmd() {
    }

    protected PipelineDbDto pipelineByName(Long workspaceId, String name) throws ApiException {

        ListPipelinesResponse list = api().listPipelines(Collections.emptyList(), workspaceId, null, null, name, "all");

        if (list.getPipelines().isEmpty()) {
            throw new PipelineNotFoundException(name, workspaceRef(workspaceId));
        }

        if (list.getPipelines().size() > 1) {
            throw new MultiplePipelinesFoundException(name, workspaceRef(workspaceId));
        }

        return list.getPipelines().get(0);
    }

    protected PipelineDbDto fetchPipeline(PipelineRefOptions pipelineRefOptions, Long wspId, PipelineQueryAttribute... attributes) throws ApiException {
        Long pipelineId = pipelineRefOptions.pipeline.pipelineId;
        if (pipelineId == null) {
            pipelineId = pipelineByName(wspId, pipelineRefOptions.pipeline.pipelineName).getPipelineId();
        }
        return api().describePipeline(pipelineId, List.of(attributes), wspId, null).getPipeline();
    }

}


