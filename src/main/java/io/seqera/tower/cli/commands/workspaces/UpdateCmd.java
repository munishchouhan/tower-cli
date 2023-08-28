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

package io.seqera.tower.cli.commands.workspaces;

import io.seqera.tower.ApiException;
import io.seqera.tower.cli.exceptions.ShowUsageException;
import io.seqera.tower.cli.responses.Response;
import io.seqera.tower.cli.responses.workspaces.WorkspaceUpdated;
import io.seqera.tower.model.DescribeWorkspaceResponse;
import io.seqera.tower.model.OrgAndWorkspaceDbDto;
import io.seqera.tower.model.UpdateWorkspaceRequest;
import io.seqera.tower.model.Visibility;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Command(
        name = "update",
        description = "Update an existing organization workspace."
)
public class UpdateCmd extends AbstractWorkspaceCmd {

    @CommandLine.Option(names = {"-i", "--id"}, description = "Workspace ID to delete.", required = true)
    public Long workspaceId;

    @Option(names = {"--new-name"}, description = "The workspace new name.")
    public String workspaceNewName;

    @Option(names = {"-f", "--fullName"}, description = "The workspace full name.")
    public String workspaceFullName;

    @Option(names = {"-d", "--description"}, description = "The workspace description.")
    public String description;

    @Override
    protected Response exec() throws ApiException, IOException {

        boolean updates =
                workspaceFullName != null
                || workspaceNewName != null
                || description != null;

        if (!updates) {
            throw new ShowUsageException(getSpec(), "Required at least one option to update");
        }

        OrgAndWorkspaceDbDto ws = workspaceById(workspaceId);

        DescribeWorkspaceResponse response = api().describeWorkspace(ws.getOrgId(), ws.getWorkspaceId());
        UpdateWorkspaceRequest request = new UpdateWorkspaceRequest()
                .fullName(response.getWorkspace().getFullName())
                .description(response.getWorkspace().getDescription());

        if (workspaceNewName != null) {
            request.setName(workspaceNewName);
        }

        if (workspaceFullName != null) {
            request.setFullName(workspaceFullName);
        }

        if (description != null) {
            request.setDescription(description);
        }

        request.setVisibility(Visibility.PRIVATE);
        api().updateWorkspace(ws.getOrgId(), ws.getWorkspaceId(), request);

        return new WorkspaceUpdated(response.getWorkspace().getName(), ws.getOrgName(), response.getWorkspace().getVisibility());
    }
}

