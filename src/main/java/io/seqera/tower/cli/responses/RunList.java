package io.seqera.tower.cli.responses;

import io.seqera.tower.cli.utils.TableList;
import io.seqera.tower.model.ListWorkflowsResponseListWorkflowsElement;

import java.io.PrintWriter;
import java.util.List;

public class RunList extends Response {

    public final String workspaceRef;
    public final List<ListWorkflowsResponseListWorkflowsElement> workflows;

    public RunList(String workspaceRef, List<ListWorkflowsResponseListWorkflowsElement> runs) {
        this.workspaceRef = workspaceRef;
        this.workflows = runs;
    }

    @Override
    public void toString(PrintWriter out) {
        out.println(ansi(String.format("%n  @|bold Pipeline's runs at %s workspace:|@%n", workspaceRef)));

        if (workflows.isEmpty()) {
            out.println(ansi("    @|yellow No pipeline's runs found|@"));
            return;
        }

        TableList table = new TableList(out, 6, "ID", "Status", "Project Name", "Run Name", "Username", "Submit Date").sortBy(0);
        table.setPrefix("    ");
        workflows.forEach(wf -> table.addRow(
                wf.getWorkflow().getId(),
                wf.getWorkflow().getStatus() != null ? wf.getWorkflow().getStatus().getValue() : "Not reported",
                wf.getWorkflow().getProjectName(),
                wf.getWorkflow().getRunName(),
                wf.getWorkflow().getUserName(),
                formatTime(wf.getWorkflow().getSubmit())
        ));
        table.print();
        out.println("");
    }
}