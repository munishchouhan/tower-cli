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
package io.seqera.tower.cli.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.seqera.tower.cli.BaseCmdTest;
import io.seqera.tower.cli.commands.data.links.AbstractDataLinksCmd;
import io.seqera.tower.cli.commands.enums.OutputType;
import io.seqera.tower.cli.responses.data.DataLinksList;
import io.seqera.tower.cli.responses.datasets.DatasetDelete;
import io.seqera.tower.cli.utils.PaginationInfo;
import io.seqera.tower.model.DataLinkCreateRequest;
import io.seqera.tower.model.DataLinkDto;
import io.seqera.tower.model.DataLinkProvider;
import io.seqera.tower.model.DataLinkType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.MediaType;

import java.util.Arrays;
import java.util.Collections;

import static io.seqera.tower.cli.utils.JsonHelper.parseJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

public class DataLinksCmdTest extends BaseCmdTest {

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
            "bucket-name;aws;eu-west-1;s3://some-bucket/is/path;bucket-name provider:aws region:eu-west-1 resourceRef:s3://some-bucket/is/path",
            ";aws,azure;eu-west-1;;provider:aws,azure region:eu-west-1",
            ";google;;;provider:google",
            ";;;;"
    })
    void testBuildingSearchParameter(String name, String providers, String region, String uri, String expected) {
        String result = AbstractDataLinksCmd.buildSearch(name, providers, region, uri);
        assertEquals(result, expected);
    }

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testList(OutputType format, MockServerClient mock) throws JsonProcessingException {
        // status check
        mock.when(
                request()
                        .withMethod("GET").withPath("/data-links")
                        .withQueryStringParameter("workspaceId", "75887156211589")
                        .withQueryStringParameter("offset", "0")
                        .withQueryStringParameter("max", "1"),
                exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("data/links/datalinks_list")).withContentType(MediaType.APPLICATION_JSON)
        );
        // mock fetch data links
        mock.when(
                request()
                        .withMethod("GET").withPath("/data-links")
                        .withQueryStringParameter("workspaceId", "75887156211589")
                        .withQueryStringParameter("offset", "0")
                        .withQueryStringParameter("max", "100"),
                exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("data/links/datalinks_list")).withContentType(MediaType.APPLICATION_JSON)
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

        ExecOut out = exec(format, mock, "data-links", "list", "-w", "75887156211589");

        assertOutput(format, out, new DataLinksList("[organization1 / workspace1]",
                Arrays.asList(
                        parseJson("{\n" +
                                "      \"id\": \"v1-cloud-c2875f38a7b5c8fe34a5b382b5f9e0c4\",\n" +
                                "      \"name\": \"a-test-bucket-eend-us-east-1\",\n" +
                                "      \"description\": null,\n" +
                                "      \"resourceRef\": \"s3://a-test-bucket-eend-us-east-1\",\n" +
                                "      \"type\": \"bucket\",\n" +
                                "      \"provider\": \"aws\",\n" +
                                "      \"region\": \"us-east-1\",\n" +
                                "      \"credentials\": [\n" +
                                "        {\n" +
                                "          \"id\": \"3irHXig7k3TG3mKRnFR75e\",\n" +
                                "          \"name\": \"aws\",\n" +
                                "          \"provider\": \"aws\"\n" +
                                "        }\n" +
                                "      ],\n" +
                                "      \"publicAccessible\": false,\n" +
                                "      \"hidden\": false,\n" +
                                "      \"status\": null,\n" +
                                "      \"message\": null\n" +
                                "    }", DataLinkDto.class),
                        parseJson("{\n" +
                                "      \"id\": \"v1-cloud-b89b60014c225c11f59048294354d174\",\n" +
                                "      \"name\": \"adrian-navarro-test\",\n" +
                                "      \"description\": null,\n" +
                                "      \"resourceRef\": \"s3://adrian-navarro-test\",\n" +
                                "      \"type\": \"bucket\",\n" +
                                "      \"provider\": \"aws\",\n" +
                                "      \"region\": \"us-east-1\",\n" +
                                "      \"credentials\": [\n" +
                                "        {\n" +
                                "          \"id\": \"3irHXig7k3TG3mKRnFR75e\",\n" +
                                "          \"name\": \"aws\",\n" +
                                "          \"provider\": \"aws\"\n" +
                                "        }\n" +
                                "      ],\n" +
                                "      \"publicAccessible\": false,\n" +
                                "      \"hidden\": false,\n" +
                                "      \"status\": null,\n" +
                                "      \"message\": null\n" +
                                "    }", DataLinkDto.class),
                        parseJson("{\n" +
                                "      \"id\": \"v1-cloud-422306eddadfc64de0676a5923517733\",\n" +
                                "      \"name\": \"adrian-navarro-test-us-west-2\",\n" +
                                "      \"description\": null,\n" +
                                "      \"resourceRef\": \"s3://adrian-navarro-test-us-west-2\",\n" +
                                "      \"type\": \"bucket\",\n" +
                                "      \"provider\": \"aws\",\n" +
                                "      \"region\": \"us-west-2\",\n" +
                                "      \"credentials\": [\n" +
                                "        {\n" +
                                "          \"id\": \"3irHXig7k3TG3mKRnFR75e\",\n" +
                                "          \"name\": \"aws\",\n" +
                                "          \"provider\": \"aws\"\n" +
                                "        }\n" +
                                "      ],\n" +
                                "      \"publicAccessible\": false,\n" +
                                "      \"hidden\": false,\n" +
                                "      \"status\": null,\n" +
                                "      \"message\": null\n" +
                                "    }", DataLinkDto.class)
                ), false, PaginationInfo.from(0, 100)));

        // No errors thrown
        assertEquals("", out.stdErr);
        assertEquals(0, out.exitCode);
    }

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testFilteredList(OutputType format, MockServerClient mock) throws JsonProcessingException {
        String json = "{\n" +
                "      \"id\": \"v1-cloud-b89b60014c225c11f59048294354d174\",\n" +
                "      \"name\": \"adrian-navarro-test\",\n" +
                "      \"description\": null,\n" +
                "      \"resourceRef\": \"s3://adrian-navarro-test\",\n" +
                "      \"type\": \"bucket\",\n" +
                "      \"provider\": \"aws\",\n" +
                "      \"region\": \"us-east-1\",\n" +
                "      \"credentials\": [\n" +
                "        {\n" +
                "          \"id\": \"3irHXig7k3TG3mKRnFR75e\",\n" +
                "          \"name\": \"aws\",\n" +
                "          \"provider\": \"aws\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"publicAccessible\": false,\n" +
                "      \"hidden\": false,\n" +
                "      \"status\": null,\n" +
                "      \"message\": null\n" +
                "    }";
        DataLinkDto link = parseJson(json, DataLinkDto.class);
        // status check
        mock.when(
                request()
                        .withMethod("GET").withPath("/data-links")
                        .withQueryStringParameter("workspaceId", "75887156211589")
                        .withQueryStringParameter("offset", "0")
                        .withQueryStringParameter("max", "1"),
                exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("data/links/datalinks_list")).withContentType(MediaType.APPLICATION_JSON)
        );
        // mock fetch data links
        mock.when(
                request()
                        .withMethod("GET").withPath("/data-links")
                        .withQueryStringParameter("workspaceId", "75887156211589")
                        .withQueryStringParameter("search", "adrian provider:aws region:us-east-1")
                        .withQueryStringParameter("offset", "0")
                        .withQueryStringParameter("max", "100"),
                exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"dataLinks\": [" + json + "] }").withContentType(MediaType.APPLICATION_JSON)
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

        ExecOut out = exec(format, mock, "data-links", "list", "-w", "75887156211589", "-n", "adrian", "-p", "aws", "-r", "us-east-1");

        assertOutput(format, out,
                new DataLinksList(
                        "[organization1 / workspace1]",
                        Collections.singletonList(parseJson(json, DataLinkDto.class)),
                        false,
                        PaginationInfo.from(0, 100)
                )
        );

        // No errors thrown
        assertEquals("", out.stdErr);
        assertEquals(0, out.exitCode);
    }

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testAdd(OutputType format, MockServerClient mock) throws JsonProcessingException {
        DataLinkCreateRequest req = new DataLinkCreateRequest();
        req.name("name");
        req.description("desc");
        req.type(DataLinkType.BUCKET);
        req.resourceRef("s3://bucket");
        req.provider(DataLinkProvider.AWS);
        req.credentialsId("credsId");
        req.publicAccessible(false);

        // status check
        mock.when(
                request()
                        .withMethod("POST").withPath("/data-links")
                        .withQueryStringParameter("workspaceId", "75887156211589")
                        .withBody(json("{\n" +
                                "   \"name\":\"name\",\n" +
                                "   \"description\":\"somedesc\",\n" +
                                "   \"type\":\"bucket\",\n" +
                                "   \"provider\":\"aws\",\n" +
                                "   \"resourceRef\":\"s3://bucket\",\n" +
                                "   \"publicAccessible\":false,\n" +
                                "   \"credentialsId\":\"57Ic6reczFn78H1DTaaXkp\"\n" +
                                "}")),
                exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\n" +
                        "    \"id\": \"v1-user-ad5192871d3d65e1218ec6c4a2f7cde5\",\n" +
                        "    \"name\": \"name\",\n" +
                        "    \"description\": \"somedesc\",\n" +
                        "    \"resourceRef\": \"s3://bucket\",\n" +
                        "    \"type\": \"bucket\",\n" +
                        "    \"provider\": \"aws\",\n" +
                        "    \"region\": \"us-east-1\",\n" +
                        "    \"credentials\": [\n" +
                        "        {\n" +
                        "            \"id\": \"57Ic6reczFn78H1DTaaXkp\",\n" +
                        "            \"name\": \"aws\",\n" +
                        "            \"provider\": \"aws\"\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"publicAccessible\": false,\n" +
                        "    \"hidden\": false,\n" +
                        "    \"status\": null,\n" +
                        "    \"message\": null\n" +
                        "}").withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(format, mock, "data-links", "add", "-w", "75887156211589", "-n", "name", "-d", "somedesc", "-p", "aws",
                "-u", "s3://bucket", "-c", "57Ic6reczFn78H1DTaaXkp");

        // No errors thrown
        assertEquals("", out.stdErr);
        assertEquals(0, out.exitCode);
    }

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testDelete(OutputType format, MockServerClient mock) {
        mock.when(
                request().withMethod("DELETE").withPath("/data-links/v1-datalinkid")
                        .withQueryStringParameter("workspaceId", "75887156211589"),
                exactly(1)
        ).respond(
                response().withStatusCode(200)
        );

        ExecOut out = exec(format, mock, "data-links", "delete", "-w", "75887156211589", "-i", "v1-datalinkid");

        assertEquals("", out.stdErr);
        assertEquals(0, out.exitCode);
    }

}
