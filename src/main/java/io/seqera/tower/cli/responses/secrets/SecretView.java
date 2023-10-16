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

package io.seqera.tower.cli.responses.secrets;

import io.seqera.tower.cli.responses.Response;
import io.seqera.tower.cli.utils.TableList;
import io.seqera.tower.model.PipelineSecret;

import java.io.PrintWriter;

import static io.seqera.tower.cli.utils.FormatHelper.formatDate;

public class SecretView extends Response {

    public final String workspaceRef;
    public final PipelineSecret secret;

    public SecretView(String workspaceRef, PipelineSecret secret) {
        this.workspaceRef = workspaceRef;
        this.secret = secret;
    }

    @Override
    public void toString(PrintWriter out) {

        out.println(ansi(String.format("%n  @|bold Secret at %s workspace:|@%n", workspaceRef)));
        TableList table = new TableList(out, 2);
        table.setPrefix("    ");
        table.addRow("ID", secret.getId().toString());
        table.addRow("Name", secret.getName());
        table.addRow("Created", formatDate(secret.getDateCreated()));
        table.addRow("Updated", formatDate(secret.getLastUpdated()));
        table.addRow("Used", formatDate(secret.getLastUsed()));
        table.print();
        out.println("");
    }
}
