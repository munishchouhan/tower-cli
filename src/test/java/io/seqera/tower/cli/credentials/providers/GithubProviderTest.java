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

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package io.seqera.tower.cli.credentials.providers;

import io.seqera.tower.cli.BaseCmdTest;
import io.seqera.tower.cli.commands.enums.OutputType;
import io.seqera.tower.cli.responses.CredentialsAdded;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.MediaType;

import static io.seqera.tower.cli.commands.AbstractApiCmd.USER_WORKSPACE_NAME;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

class GithubProviderTest extends BaseCmdTest {

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testAdd(OutputType format, MockServerClient mock) {

        mock.when(
                request()
                        .withMethod("POST")
                        .withPath("/credentials")
                        .withBody(json("{\"credentials\":{\"keys\":{\"username\":\"jordi@seqera.io\",\"password\":\"mysecret\"},\"name\":\"github\",\"provider\":\"github\"}}")),
                exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"credentialsId\":\"1cz5A8cuBkB5iJliCwJCFU\"}").withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(format, mock, "credentials", "add", "github", "-n", "github", "-u", "jordi@seqera.io", "-p", "mysecret");
        assertOutput(format, out, new CredentialsAdded("GITHUB", "1cz5A8cuBkB5iJliCwJCFU", "github", USER_WORKSPACE_NAME));

    }

}
