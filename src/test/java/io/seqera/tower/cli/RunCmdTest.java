/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package io.seqera.tower.cli;

import io.seqera.tower.cli.responses.RunSubmited;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

class RunCmdTest extends BaseCmdTest {

    @Test
    void testInvalidAuth(MockServerClient mock) {

        // Create server expectation
        mock.when(
                request().withMethod("GET").withPath("/pipelines"), exactly(1)
        ).respond(
                response().withStatusCode(401)
        );

        // Run the command
        ExecOut out = exec(mock, "run", "hello");

        // Assert results
        assertEquals(String.format("[401] Unauthorized"), out.stdErr);
        assertEquals("", out.stdOut);
        assertEquals(-1, out.exitCode);

    }

    @Test
    void testPipelineNotfound(MockServerClient mock) {

        // Create server expectation
        mock.when(
                request().withMethod("GET").withPath("/pipelines"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResponse("pipelines_none"))
        );

        // Run the command
        ExecOut out = exec(mock, "run", "hello");

        // Assert results
        assertEquals(-1, out.exitCode);
        assertEquals("Pipeline 'hello' not found on this workspace.", out.stdErr);

    }

    @Test
    void testMultiplePipelinesFound(MockServerClient mock) {

        // Create server expectation
        mock.when(
                request().withMethod("GET").withPath("/pipelines"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResponse("pipelines_multiple"))
        );

        // Run the command
        ExecOut out = exec(mock, "run", "hello");

        // Assert results
        assertEquals(-1, out.exitCode);
        assertEquals("Multiple pipelines match 'hello'", out.stdErr);
    }

    @Test
    void testSubmitUserPipeline(MockServerClient mock) {

        // Create server expectation
        mock.when(
                request().withMethod("GET").withPath("/pipelines"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResponse("pipelines_sarek"))
        );

        mock.when(
                request().withMethod("GET").withPath("/pipelines/250911634275687/launch"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResponse("pipeline_launch_describe"))
        );

        mock.when(
                request().withMethod("POST").withPath("/workflow/launch"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResponse("workflow_launch"))
        );

        mock.when(
                request().withMethod("GET").withPath("/user"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResponse("user"))
        );

        // Run the command
        ExecOut out = exec(mock, "run", "sarek");

        // Assert results
        assertEquals(0, out.exitCode);
        assertEquals(
                new RunSubmited("35aLiS0bIM5efd", String.format("%s/user/jordi/watch/35aLiS0bIM5efd", url(mock)), "personal").toString(),
                out.stdOut
        );
        assertEquals("", out.stdErr);

    }

}
