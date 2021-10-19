package io.seqera.tower.cli.commands.workspaces;


import io.seqera.tower.ApiException;
import io.seqera.tower.cli.responses.Response;
import io.seqera.tower.cli.responses.workspaces.WorkspaceList;
import io.seqera.tower.model.ListWorkspacesAndOrgResponse;
import io.seqera.tower.model.OrgAndWorkspaceDbDto;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Command(
        name = "list",
        description = "List user workspaces"
)
public class ListCmd extends AbstractWorkspaceCmd {

    @CommandLine.Option(names = {"-o", "--org", "--organization"}, description = "The workspace organization name")
    public String organizationName;

    @Override
    protected Response exec() throws ApiException, IOException {
        ListWorkspacesAndOrgResponse response = api().listWorkspacesUser(userId());

        if (organizationName != null && response.getOrgsAndWorkspaces() != null) {
            List<OrgAndWorkspaceDbDto> responseOrg = response.getOrgsAndWorkspaces()
                    .stream()
                    .filter(item -> Objects.equals(item.getOrgName(), organizationName))
                    .collect(Collectors.toList());

            return new WorkspaceList(userName(), responseOrg);
        }

        return new WorkspaceList(userName(), response.getOrgsAndWorkspaces());
    }
}