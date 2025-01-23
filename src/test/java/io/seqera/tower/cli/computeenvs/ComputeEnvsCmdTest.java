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

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package io.seqera.tower.cli.computeenvs;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.seqera.tower.ApiException;
import io.seqera.tower.JSON;
import io.seqera.tower.cli.BaseCmdTest;
import io.seqera.tower.cli.commands.enums.OutputType;
import io.seqera.tower.cli.exceptions.ComputeEnvNotFoundException;
import io.seqera.tower.cli.exceptions.InvalidResponseException;
import io.seqera.tower.cli.exceptions.TowerException;
import io.seqera.tower.cli.responses.computeenvs.ComputeEnvAdded;
import io.seqera.tower.cli.responses.computeenvs.ComputeEnvDeleted;
import io.seqera.tower.cli.responses.computeenvs.ComputeEnvExport;
import io.seqera.tower.cli.responses.computeenvs.ComputeEnvList;
import io.seqera.tower.cli.responses.computeenvs.ComputeEnvUpdated;
import io.seqera.tower.cli.responses.computeenvs.ComputeEnvView;
import io.seqera.tower.cli.responses.computeenvs.ComputeEnvsPrimaryGet;
import io.seqera.tower.cli.responses.computeenvs.ComputeEnvsPrimarySet;
import io.seqera.tower.model.AwsBatchConfig;
import io.seqera.tower.model.ComputeConfig;
import io.seqera.tower.model.ComputeEnvComputeConfig.PlatformEnum;
import io.seqera.tower.model.ComputeEnvResponseDto;
import io.seqera.tower.model.ComputeEnvStatus;
import io.seqera.tower.model.ForgeConfig;
import io.seqera.tower.model.ListComputeEnvsResponseEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.mockserver.model.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.seqera.tower.cli.commands.AbstractApiCmd.USER_WORKSPACE_NAME;
import static io.seqera.tower.cli.utils.JsonHelper.parseJson;
import static org.apache.commons.lang3.StringUtils.chop;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

class ComputeEnvsCmdTest extends BaseCmdTest {

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testDelete(OutputType format, MockServerClient mock) {
        mock.when(
                request().withMethod("DELETE").withPath("/compute-envs/vYOK4vn7spw7bHHWBDXZ2"), exactly(1)
        ).respond(
                response().withStatusCode(204)
        );

        ExecOut out = exec(format, mock, "compute-envs", "delete", "-i", "vYOK4vn7spw7bHHWBDXZ2");
        assertOutput(format, out, new ComputeEnvDeleted("vYOK4vn7spw7bHHWBDXZ2", USER_WORKSPACE_NAME));
    }

    @Test
    void testDeleteInvalidAuth(MockServerClient mock) {
        mock.when(
                request().withMethod("DELETE").withPath("/compute-envs/vYOK4vn7spw7bHHWBDXZ8"), exactly(1)
        ).respond(
                response().withStatusCode(401)
        );

        ExecOut out = exec(mock, "compute-envs", "delete", "-i", "vYOK4vn7spw7bHHWBDXZ8");

        assertEquals(errorMessage(out.app, new ApiException(401, "Unauthorized")), out.stdErr);
        assertEquals("", out.stdOut);
        assertEquals(1, out.exitCode);
    }

    @Test
    void testDeleteNotFound(MockServerClient mock) {
        mock.when(
                request().withMethod("DELETE").withPath("/compute-envs/vYOK4vn7spw7bHHWBDXZ3"), exactly(1)
        ).respond(
                response().withStatusCode(403)
        );

        ExecOut out = exec(mock, "compute-envs", "delete", "-i", "vYOK4vn7spw7bHHWBDXZ3");

        assertEquals(errorMessage(out.app, new ComputeEnvNotFoundException("vYOK4vn7spw7bHHWBDXZ3", USER_WORKSPACE_NAME)), out.stdErr);
        assertEquals("", out.stdOut);
        assertEquals(1, out.exitCode);
    }

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testList(OutputType format, MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"computeEnvs\":[{\"id\":\"vYOK4vn7spw7bHHWBDXZ2\",\"name\":\"demo\",\"platform\":\"aws-batch\",\"status\":\"AVAILABLE\",\"message\":null,\"lastUsed\":null,\"primary\":null,\"workspaceName\":null,\"visibility\":null}]}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/user-info"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("user")).withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(format, mock, "compute-envs", "list");
        assertOutput(format, out, new ComputeEnvList(USER_WORKSPACE_NAME, List.of(
                new ListComputeEnvsResponseEntry()
                        .id("vYOK4vn7spw7bHHWBDXZ2")
                        .name("demo")
                        .platform("aws-batch")
                        .status(ComputeEnvStatus.AVAILABLE)
        ), baseUserUrl(mock, USER_WORKSPACE_NAME)));
    }

    @Test
    void testListEmpty(MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"computeEnvs\":[]}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/user-info"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("user")).withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "compute-envs", "list");

        assertEquals("", out.stdErr);
        assertEquals(chop(new ComputeEnvList(USER_WORKSPACE_NAME, List.of(), baseUserUrl(mock, USER_WORKSPACE_NAME)).toString()), out.stdOut);
        assertEquals(0, out.exitCode);
    }


    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testViewAwsForge(OutputType format, MockServerClient mock) throws JsonProcessingException {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/isnEDBLvHDAIteOEF44ow"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("compute_env_view")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/user-info"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("user")).withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(format, mock, "compute-envs", "view", "-i", "isnEDBLvHDAIteOEF44ow");
        assertOutput(format, out, new ComputeEnvView("isnEDBLvHDAIteOEF44ow", USER_WORKSPACE_NAME,
                parseJson("{\"id\": \"isnEDBLvHDAIteOEF44ow\", \"dateCreated\": \"2021-09-08T11:19:24Z\", \"lastUpdated\": \"2021-09-08T11:20:08Z\"}", ComputeEnvResponseDto.class)
                        .name("demo")
                        .platform(ComputeEnvResponseDto.PlatformEnum.AWS_BATCH)
                        .status(ComputeEnvStatus.AVAILABLE)
                        .credentialsId("6g0ER59L4ZoE5zpOmUP48D")
                        .config(
                                parseJson(" {\"discriminator\": \"aws-batch\"}", AwsBatchConfig.class)
                                        .region("eu-west-1")
                                        .cliPath("/home/ec2-user/miniconda/bin/aws")
                                        .workDir("s3://nextflow-ci/jordeu")
                                        .volumes(Collections.emptyList())
                                        .computeQueue("TowerForge-isnEDBLvHDAIteOEF44ow-work")
                                        .headQueue("TowerForge-isnEDBLvHDAIteOEF44ow-head")
                                        .forge(
                                                new ForgeConfig()
                                                        .type(ForgeConfig.TypeEnum.SPOT)
                                                        .minCpus(0)
                                                        .maxCpus(123)
                                                        .gpuEnabled(false)
                                                        .ebsAutoScale(true)
                                                        .disposeOnDeletion(true)
                                                        .fusionEnabled(true)
                                        ).forgedResources(
                                                List.of(
                                                        Map.of("IamRole", "arn:aws:iam::195996028523:role/TowerForge-isnEDBLvHDAIteOEF44ow-ServiceRole"),
                                                        Map.of("IamRole", "arn:aws:iam::195996028523:role/TowerForge-isnEDBLvHDAIteOEF44ow-FleetRole")
                                                )
                                        )

                        ),
                baseUserUrl(mock, USER_WORKSPACE_NAME)
        ));
    }

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testViewAwsManual(OutputType format, MockServerClient mock) throws JsonProcessingException {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/53aWhB2qJroy0i51FOrFAC"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("compute_env_view_aws_manual")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/user-info"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("user")).withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(format, mock, "compute-envs", "view", "-i", "53aWhB2qJroy0i51FOrFAC");
        assertOutput(format, out, new ComputeEnvView("53aWhB2qJroy0i51FOrFAC", USER_WORKSPACE_NAME,
                parseJson("{\"id\": \"53aWhB2qJroy0i51FOrFAC\", \"dateCreated\": \"2021-09-08T15:19:08Z\", \"lastUpdated\": \"2021-09-08T15:19:08Z\"}", ComputeEnvResponseDto.class)
                        .name("manual")
                        .platform(ComputeEnvResponseDto.PlatformEnum.AWS_BATCH)
                        .status(ComputeEnvStatus.ERRORED)
                        .credentialsId("6g0ER59L4ZoE5zpOmUP48D")
                        .config(
                                parseJson(" {\"discriminator\": \"aws-batch\"}", AwsBatchConfig.class)
                                        .region("eu-west-1")
                                        .computeQueue("TowerForge-isnEDBLvHDAIteOEF44ow-work")
                                        .headQueue("TowerForge-isnEDBLvHDAIteOEF44ow-head")
                                        .cliPath("/home/ec2-user/miniconda/bin/aws")
                                        .workDir("s3://nextflow-ci/jordeu")
                                        .volumes(Collections.emptyList())
                        ),
                baseUserUrl(mock, USER_WORKSPACE_NAME)
        ));
    }

    @Test
    void testViewNotFound(MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/isnEDBLvHDAIteOEF44or"), exactly(1)
        ).respond(
                response().withStatusCode(403)
        );

        ExecOut out = exec(mock, "compute-envs", "view", "-i", "isnEDBLvHDAIteOEF44or");

        assertEquals(errorMessage(out.app, new TowerException(String.format("Compute environment '%s' not found at user workspace", "isnEDBLvHDAIteOEF44or"))), out.stdErr);
        assertEquals("", out.stdOut);
        assertEquals(1, out.exitCode);
    }

    @Test
    void testViewInvalidAuth(MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/isnEDBLvHDAIteOEF44om"), exactly(1)
        ).respond(
                response().withStatusCode(401)
        );

        ExecOut out = exec(mock, "compute-envs", "view", "-i", "isnEDBLvHDAIteOEF44om");

        assertEquals(errorMessage(out.app, new ApiException(401, "Unauthorized")), out.stdErr);
        assertEquals("", out.stdOut);
        assertEquals(1, out.exitCode);
    }

    @Test
    void testAddWithoutSubCommands(MockServerClient mock) {
        ExecOut out = exec(mock, "compute-envs", "add");
        assertEquals(1, out.exitCode);
        assertTrue(out.stdErr.contains("Missing Required Subcommand"));
    }

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testExport(OutputType format, MockServerClient mock) throws JsonProcessingException {
        mock.when(
                request().withMethod("GET").withPath("/compute-envs"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("compute_envs_list")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/3xkkzYH2nbD3nZjrzKm0oR"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("compute_env_view")).withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(format, mock, "compute-envs", "export", "-n", "ce1");
        ComputeConfig computeConfig = parseJson("{\n" +
                "      \"region\": \"eu-west-1\",\n" +
                "      \"computeJobRole\": null,\n" +
                "      \"headJobRole\": null,\n" +
                "      \"cliPath\": \"/home/ec2-user/miniconda/bin/aws\",\n" +
                "      \"workDir\": \"s3://nextflow-ci/jordeu\",\n" +
                "      \"preRunScript\": null,\n" +
                "      \"postRunScript\": null,\n" +
                "      \"headJobCpus\": null,\n" +
                "      \"headJobMemoryMb\": null,\n" +
                "      \"forge\": {\n" +
                "        \"type\": \"SPOT\",\n" +
                "        \"minCpus\": 0,\n" +
                "        \"maxCpus\": 123,\n" +
                "        \"gpuEnabled\": false,\n" +
                "        \"ebsAutoScale\": true,\n" +
                "        \"instanceTypes\": null,\n" +
                "        \"allocStrategy\": null,\n" +
                "        \"imageId\": null,\n" +
                "        \"vpcId\": null,\n" +
                "        \"subnets\": null,\n" +
                "        \"securityGroups\": null,\n" +
                "        \"fsxMount\": null,\n" +
                "        \"fsxName\": null,\n" +
                "        \"fsxSize\": null,\n" +
                "        \"disposeOnDeletion\": true,\n" +
                "        \"ec2KeyPair\": null,\n" +
                "        \"allowBuckets\": null,\n" +
                "        \"ebsBlockSize\": null,\n" +
                "        \"fusionEnabled\": true,\n" +
                "        \"bidPercentage\": null,\n" +
                "        \"efsCreate\": null,\n" +
                "        \"efsId\": null,\n" +
                "        \"efsMount\": null\n" +
                "      },\n" +
                "      \"discriminator\": \"aws-batch\"\n" +
                "    },\n" +
                "    \"lastUsed\": null,\n" +
                "    \"deleted\": null,\n" +
                "    \"message\": null,\n" +
                "    \"primary\": null,\n" +
                "    \"credentialsId\": \"6g0ER59L4ZoE5zpOmUP48D\"\n" +
                "  }", ComputeConfig.class);

        String configOutput = new JSON().getContext(ComputeConfig.class).writerWithDefaultPrettyPrinter().writeValueAsString(computeConfig);

        assertOutput(format, out, new ComputeEnvExport(configOutput, null));

    }

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testImport(OutputType format, MockServerClient mock) throws IOException {
        mock.when(
                request().withMethod("GET").withPath("/credentials").withQueryStringParameter("platformId", "aws-batch"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"credentials\":[{\"id\":\"6g0ER59L4ZoE5zpOmUP48D\",\"name\":\"aws\",\"description\":null,\"discriminator\":\"aws\",\"baseUrl\":null,\"category\":null,\"deleted\":null,\"lastUsed\":\"2021-09-09T07:20:53Z\",\"dateCreated\":\"2021-09-08T05:48:51Z\",\"lastUpdated\":\"2021-09-08T05:48:51Z\"}]}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/labels")
        ).respond(
                response().withStatusCode(200).withBody(JsonBody.json("{}"))
        );
        mock.when(
                request().withMethod("POST").withPath("/labels")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(JsonBody.json("{\n" +
                                "\"id\": 10,\n" +
                                "\"name\": \"res\",\n" +
                                "\"resource\": true,\n" +
                                "\"value\": \"val\"\n" +
                                "}"
                        ))
        );

        mock.when(
                request().withMethod("POST").withPath("/compute-envs").withBody(JsonBody.json("{\n" +
                        "  \"computeEnv\":{\n" +
                        "    \"name\": \"json\",\n" +
                        "    \"platform\": \"aws-batch\",\n" +
                        "    \"config\": {\n" +
                        "      \"region\": \"eu-west-1\",\n" +
                        "      \"cliPath\": \"/home/ec2-user/miniconda/bin/aws\",\n" +
                        "      \"workDir\": \"s3://nextflow-ci/jordeu\",\n" +
                        "      \"forge\": {\n" +
                        "        \"type\": \"SPOT\",\n" +
                        "        \"minCpus\": 0,\n" +
                        "        \"maxCpus\": 123,\n" +
                        "        \"gpuEnabled\": false,\n" +
                        "        \"ebsAutoScale\": true,\n" +
                        "        \"disposeOnDeletion\": true,\n" +
                        "        \"fusionEnabled\": false,\n" +
                        "        \"efsCreate\": true\n" +
                        "      },\n" +
                        "      \"discriminator\": \"aws-batch\"\n" +
                        "    },\n" +
                        "    \"credentialsId\": \"6g0ER59L4ZoE5zpOmUP48D\"\n" +
                        "  }\n" +
                        "}")), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"computeEnvId\":\"3T6xWeFD63QIuzdAowvSTC\"}").withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(format, mock, "compute-envs", "import", tempFile(new String(loadResource("cejson"), StandardCharsets.UTF_8), "ce", "json"), "-n", "json", "-c", "6g0ER59L4ZoE5zpOmUP48D");
        assertOutput(format, out, new ComputeEnvAdded(PlatformEnum.AWS_BATCH.getValue(), "3T6xWeFD63QIuzdAowvSTC", "json", null, USER_WORKSPACE_NAME));
    }

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testImportWithOverwrite(OutputType format, MockServerClient mock) throws IOException {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{" +
                        "\"computeEnvs\":[" +
                            "{" +
                                "\"id\":\"isnEDBLvHDAIteOEF44ow\"," +
                                "\"name\":\"demo\"," +
                                "\"platform\":\"aws-batch\"," +
                                "\"status\":\"AVAILABLE\"," +
                                "\"message\":null," +
                                "\"lastUsed\":null," +
                                "\"primary\":null," +
                                "\"workspaceName\":null," +
                                "\"visibility\":null" +
                            "}" +
                        "]}"
                ).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/isnEDBLvHDAIteOEF44ow"), exactly(1)
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(loadResource("compute_env_view"))
                        .withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("POST").withPath("/compute-envs")
                        .withBody(JsonBody.json("{\n" +
                                "  \"computeEnv\":{\n" +
                                "    \"name\": \"demo\",\n" +
                                "    \"platform\": \"aws-batch\",\n" +
                                "    \"config\": {\n" +
                                "      \"region\": \"eu-west-1\",\n" +
                                "      \"cliPath\": \"/home/ec2-user/miniconda/bin/aws\",\n" +
                                "      \"workDir\": \"s3://nextflow-ci/jordeu\",\n" +
                                "      \"forge\": {\n" +
                                "        \"type\": \"SPOT\",\n" +
                                "        \"minCpus\": 0,\n" +
                                "        \"maxCpus\": 123,\n" +
                                "        \"gpuEnabled\": false,\n" +
                                "        \"ebsAutoScale\": true,\n" +
                                "        \"disposeOnDeletion\": true,\n" +
                                "        \"fusionEnabled\": false,\n" +
                                "        \"efsCreate\": true\n" +
                                "      },\n" +
                                "      \"discriminator\": \"aws-batch\"\n" +
                                "    },\n" +
                                "    \"credentialsId\": \"6g0ER59L4ZoE5zpOmUP48D\"\n" +
                                "}\n" +
                                "}\n" +
                                "  ")), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"computeEnvId\":\"3T6xWeFD63QIuzdAowvSTC\"}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("DELETE").withPath("/compute-envs/isnEDBLvHDAIteOEF44ow"),
                exactly(1)
        ).respond(
                response().withStatusCode(200)
        );

        mock.when(
                request().withMethod("GET").withPath("/credentials").withQueryStringParameter("platformId", "aws-batch"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"credentials\":[{\"id\":\"6g0ER59L4ZoE5zpOmUP48D\",\"name\":\"aws\",\"description\":null,\"discriminator\":\"aws\",\"baseUrl\":null,\"category\":null,\"deleted\":null,\"lastUsed\":\"2021-09-09T07:20:53Z\",\"dateCreated\":\"2021-09-08T05:48:51Z\",\"lastUpdated\":\"2021-09-08T05:48:51Z\"}]}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/labels")
        ).respond(
                response().withStatusCode(200).withBody(JsonBody.json("{}"))
        );
        mock.when(
                request().withMethod("POST").withPath("/labels")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(JsonBody.json("{\n" +
                                "\"id\": 10,\n" +
                                "\"name\": \"res\",\n" +
                                "\"resource\": true,\n" +
                                "\"value\": \"val\"\n" +
                                "}"
                        ))
        );

        ExecOut out = exec(format, mock, "compute-envs", "import", "--overwrite", tempFile(new String(loadResource("cejson"), StandardCharsets.UTF_8), "ce", "json"), "-n", "demo", "-c", "6g0ER59L4ZoE5zpOmUP48D");
        assertOutput(format, out, new ComputeEnvAdded(PlatformEnum.AWS_BATCH.getValue(), "3T6xWeFD63QIuzdAowvSTC", "demo", null, USER_WORKSPACE_NAME));
    }

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testPrimaryGet(OutputType format, MockServerClient mock) throws JsonProcessingException {
        mock.when(
                request().withMethod("GET").withPath("/compute-envs"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"computeEnvs\":[{\"id\":\"isnEDBLvHDAIteOEF44ow\",\"name\":\"demo\",\"platform\":\"aws-batch\",\"status\":\"AVAILABLE\",\"message\":null,\"lastUsed\":null,\"primary\":true,\"workspaceName\":null,\"visibility\":null}]}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/isnEDBLvHDAIteOEF44ow"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("compute_env_view")).withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(format, mock, "compute-envs", "primary", "get");

        ComputeEnvResponseDto ce = parseJson("{\"id\":\"isnEDBLvHDAIteOEF44ow\",\"name\":\"demo\",\"platform\":\"aws-batch\",\"status\":\"AVAILABLE\",\"message\":null,\"lastUsed\":null,\"primary\":true,\"workspaceName\":null,\"visibility\":null}", ComputeEnvResponseDto.class);

        assertOutput(format, out, new ComputeEnvsPrimaryGet(USER_WORKSPACE_NAME, ce));
    }

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testPrimarySet(OutputType format, MockServerClient mock) throws JsonProcessingException {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/isnEDBLvHDAIteOEF44ow"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("compute_env_view")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("POST").withPath("/compute-envs/isnEDBLvHDAIteOEF44ow/primary"), exactly(1)
        ).respond(
                response().withStatusCode(204)
        );

        ExecOut out = exec(format, mock, "compute-envs", "primary", "set", "-i", "isnEDBLvHDAIteOEF44ow");

        ComputeEnvResponseDto ce = parseJson("{\"id\":\"isnEDBLvHDAIteOEF44ow\",\"name\":\"demo\",\"platform\":\"aws-batch\",\"status\":\"AVAILABLE\",\"message\":null,\"lastUsed\":null,\"primary\":true,\"workspaceName\":null,\"visibility\":null}", ComputeEnvResponseDto.class);
        assertOutput(format, out, new ComputeEnvsPrimarySet(USER_WORKSPACE_NAME, ce));
    }

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testPrimarySetAzure(OutputType format, MockServerClient mock) throws JsonProcessingException {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/lkasjdlkfwerjbEcrycwrSSe").withQueryStringParameter("workspaceId", "75887156211590"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"computeEnv\":{\"id\":\"lkasjdlkfwerjbEcrycwrSSe\",\"name\":\"demo\",\"description\":null,\"platform\":\"azure-batch\",\"config\":{\"workDir\":\"az://scratch/work\",\"preRunScript\":null,\"postRunScript\":null,\"region\":\"eastus\",\"headPool\":null,\"autoPoolMode\":null,\"forge\":{\"vmType\":\"Standard_D4_v3\",\"vmCount\":1,\"autoScale\":true,\"disposeOnDeletion\":true},\"tokenDuration\":null,\"deleteJobsOnCompletion\":\"on_success\",\"deletePoolsOnCompletion\":null,\"environment\":null,\"discriminator\":\"azure-batch\"},\"dateCreated\":\"2021-12-03T07:20:26Z\",\"lastUpdated\":\"2021-12-03T07:20:27Z\",\"lastUsed\":null,\"deleted\":null,\"status\":\"AVAILABLE\",\"message\":null,\"primary\":null,\"credentialsId\":\"XXbouwqopeiruiopqDplGsgEJxad\"}}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("POST").withPath("/compute-envs/lkasjdlkfwerjbEcrycwrSSe/primary"), exactly(1)
        ).respond(
                response().withStatusCode(204)
        );

        mock.when(
                request().withMethod("GET").withPath("/user-info"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("user")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/user/1264/workspaces"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("workspaces/workspaces_list")).withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(format, mock, "compute-envs", "primary", "set", "-i", "lkasjdlkfwerjbEcrycwrSSe", "-w", "75887156211590");

        ComputeEnvResponseDto ce = parseJson("{\"id\":\"lkasjdlkfwerjbEcrycwrSSe\",\"name\":\"demo\",\"platform\":\"azure-batch\"}", ComputeEnvResponseDto.class);
        assertOutput(format, out, new ComputeEnvsPrimarySet("[organization2 / workspace2]", ce));
    }

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testUpdateName(OutputType format, MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/validate")
                        .withQueryStringParameter("name", "testCE")
                        .withQueryStringParameter("workspaceId", "75887156211590"),
                exactly(1)
        ).respond(
                response().withStatusCode(204)
        );

        mock.when(
                request().withMethod("GET").withPath("/compute-envs"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("compute_envs_list")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/vYOK4vn7spw7bHHWBDXZ2"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("compute_env_demo")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("PUT").withPath("/compute-envs/vYOK4vn7spw7bHHWBDXZ2"), exactly(1)
        ).respond(
                response().withStatusCode(204)
        );

        mock.when(
                request().withMethod("GET").withPath("/user-info"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("user")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/user/1264/workspaces"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("workspaces/workspaces_list")).withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(format, mock, "compute-envs", "update", "-n", "demo", "-w", "75887156211590", "--new-name", "testCE");

        assertEquals("", out.stdErr);
        assertOutput(format, out, new ComputeEnvUpdated("[organization2 / workspace2]", "demo"));
        assertEquals(0, out.exitCode);

    }

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testUpdateInvalidName(OutputType format, MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/validate")
                        .withQueryStringParameter("name", "testCE")
                        .withQueryStringParameter("workspaceId", "75887156211590"),
                exactly(1)
        ).respond(
                response().withStatusCode(400)
        );

        mock.when(
                request().withMethod("GET").withPath("/user-info"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("user")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/user/1264/workspaces"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("workspaces/workspaces_list")).withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(format, mock, "compute-envs", "update", "-n", "demo", "-w", "75887156211590", "--new-name", "testCE");

        assertEquals(errorMessage(out.app, new InvalidResponseException("Compute environment name 'testCE' is not valid")), out.stdErr);
        assertEquals("", out.stdOut);
        assertEquals(1, out.exitCode);
    }
}
