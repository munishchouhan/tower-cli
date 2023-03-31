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

package io.seqera.tower.cli.commands.labels;

import io.seqera.tower.ApiException;
import io.seqera.tower.cli.commands.computeenvs.AbstractComputeEnvCmd;
import io.seqera.tower.cli.commands.global.WorkspaceOptionalOptions;
import io.seqera.tower.cli.exceptions.ShowUsageException;
import io.seqera.tower.cli.exceptions.TowerException;
import io.seqera.tower.cli.responses.Response;
import io.seqera.tower.cli.responses.labels.LabelAdded;
import io.seqera.tower.model.CreateLabelRequest;
import io.seqera.tower.model.CreateLabelResponse;
import picocli.CommandLine;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.PrintWriter;

@CommandLine.Command(
        name = "add",
        description = "Add new label"
)
public class AddLabelsCmd extends AbstractLabelsCmd {

    @CommandLine.Option(names = {"-n", "--name"}, description = "Label name.", required = true)
    public String labelName;

    @CommandLine.Mixin
    public WorkspaceOptionalOptions workspaceOptionalOptions;

    @CommandLine.Option(names = {"-v", "--value"}, description = "Label value")
    @Nullable
    public String labelValue;

    @Override
    protected Response exec() throws ApiException, IOException, IllegalArgumentException, TowerException {

        CreateLabelRequest req = new CreateLabelRequest()
                .name(labelName)
                .value(labelValue)
                .resource(labelValue != null);

        if (labelValue != null) {
            req.setValue(labelValue);
            req.setResource(true);
        } else {
            req.setResource(false);
        }

        Long wspId = workspaceId(workspaceOptionalOptions.workspace);

        try {
            CreateLabelResponse res = api().createLabel(req, wspId);
            return new LabelAdded(res.getId(), res.getName(), res.getResource(),res.getValue(),workspaceOptionalOptions.workspace);
        } catch (Exception e) {
            throw new TowerException(String.format("Unable to create label for workspace '%d'", wspId));
        }
    }

}
