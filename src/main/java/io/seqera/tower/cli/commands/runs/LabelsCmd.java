/*
 * Copyright (c) 2021, Seqera Labs.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */

package io.seqera.tower.cli.commands.runs;

import java.io.IOException;
import java.util.List;

import io.seqera.tower.ApiException;
import io.seqera.tower.api.DefaultApi;
import io.seqera.tower.cli.commands.labels.LabelsFinder;
import io.seqera.tower.cli.commands.labels.LabelsSubcmdOptions;
import io.seqera.tower.cli.commands.pipelines.AbstractPipelinesCmd;
import io.seqera.tower.cli.responses.Response;
import io.seqera.tower.cli.responses.labels.ManageLabels;
import io.seqera.tower.model.AssociateWorkflowLabelsRequest;
import picocli.CommandLine;

@CommandLine.Command(name = "labels", description = "Manages labels for runs.")
public class LabelsCmd extends AbstractPipelinesCmd {

    @CommandLine.Option(names = {"-i", "-id"}, description = "Pipeline run identifier.", required = true)
    public String id;

    @CommandLine.Mixin
    public LabelsSubcmdOptions labelsSubcmdOptions;

    @Override
    protected Response exec() throws ApiException, IOException {
        Long wspId = workspaceId(labelsSubcmdOptions.workspace.workspace);
        DefaultApi api = api();

        RunsLabelsCreator creator = new RunsLabelsCreator(api);

        return creator.execute(wspId,id,labelsSubcmdOptions);
    }
}
