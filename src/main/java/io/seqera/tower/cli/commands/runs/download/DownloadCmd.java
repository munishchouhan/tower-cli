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

package io.seqera.tower.cli.commands.runs.download;

import io.seqera.tower.ApiException;
import io.seqera.tower.cli.commands.runs.AbstractRunsCmd;
import io.seqera.tower.cli.commands.runs.ViewCmd;
import io.seqera.tower.cli.commands.runs.download.enums.RunDownloadFileType;
import io.seqera.tower.cli.responses.Response;
import io.seqera.tower.cli.responses.RunFileDownloaded;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;

@CommandLine.Command(
        name = "download",
        description = "Download a pipeline's run related files"
)
public class DownloadCmd extends AbstractRunsCmd {

    @CommandLine.Option(names = {"--type"}, description = "File type to download. Options are console, log, error (for tasks only) and timeline (workflow only) (default is console)")
    public RunDownloadFileType type;

    @CommandLine.Option(names = {"-t"}, description = "Task identifier")
    public Long task;

    @CommandLine.ParentCommand
    ViewCmd parentCommand;

    @Override
    protected Response exec() throws ApiException, IOException {
        String fileName;
        File file;

        if (task == null) {
            fileName = String.format("nf-%s.txt", parentCommand.id);

            if (type == RunDownloadFileType.log) {
                fileName = String.format("nf-%s.log", parentCommand.id);
            } else if (type == RunDownloadFileType.timeline) {
                fileName = String.format("timeline-%s.html", parentCommand.id);
            }

            file = api().downloadWorkflowLog(parentCommand.id, fileName, parentCommand.workspace.workspaceId);

        } else {
            fileName = ".command.out";

            if (type == RunDownloadFileType.log) {
                fileName = ".command.log";
            } else if (type == RunDownloadFileType.error) {
                fileName = ".command.err";
            }

            file = api().downloadWorkflowTaskLog(parentCommand.id, task, fileName, parentCommand.workspace.workspaceId);
        }

        return new RunFileDownloaded(file);
    }
}
