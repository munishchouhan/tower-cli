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

package io.seqera.tower.cli.commands.datastudios;

import io.seqera.tower.ApiException;
import io.seqera.tower.cli.commands.global.WorkspaceOptionalOptions;
import io.seqera.tower.cli.exceptions.DataStudioNotFoundException;
import io.seqera.tower.cli.exceptions.TowerException;
import io.seqera.tower.cli.responses.Response;
import io.seqera.tower.cli.responses.datastudios.DataStudiosView;
import io.seqera.tower.model.DataStudioDto;
import picocli.CommandLine;

@CommandLine.Command(
        name = "view",
        description = "View data studio."
)
public class ViewCmd extends AbstractStudiosCmd {

    @CommandLine.Mixin
    public WorkspaceOptionalOptions workspace;

    @CommandLine.Mixin
    public DataStudioRefOptions dataStudioRefOptions;

    @Override
    protected Response exec() throws ApiException {
        Long wspId = workspaceId(workspace.workspace);

        try {
            DataStudioDto dataStudioDto = fetchDataStudio(dataStudioRefOptions, wspId);

            return new DataStudiosView(dataStudioDto, workspaceRef(wspId));
        } catch (ApiException e) {
            if (e.getCode() == 404) {
                throw new DataStudioNotFoundException(dataStudioRefOptions.dataStudio.sessionId, wspId);
            }
            if (e.getCode() == 403) {
                throw new TowerException(String.format("User not entitled to view studio '%s' at %s workspace", dataStudioRefOptions.dataStudio.sessionId, wspId));
            }
            throw e;
        }
    }

}