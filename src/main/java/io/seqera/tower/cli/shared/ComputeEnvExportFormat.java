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

package io.seqera.tower.cli.shared;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.seqera.tower.model.ComputeConfig;
import io.seqera.tower.model.LabelDbDto;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * This is the class used by ExportCmd to structure CE data as JSON.
 * The class {@link ComputeConfig} does not include labels so this envelope
 * class is needed to export/import them along the rest of the data.
 */
public final class ComputeEnvExportFormat {

    private ComputeConfig config;

    private List<LabelDbDto> labels;

    public ComputeEnvExportFormat(final ComputeConfig config, final List<LabelDbDto> labels) {
        this.config = config;
        this.labels = labels;
    }

    public ComputeConfig getConfig() {
        return config;
    }

    public List<LabelDbDto> getLabels() {
        return labels;
    }

    public static ComputeEnvExportFormat deserialize(final String json) throws JsonProcessingException {
        return buildMapper()
                .registerModule(
                        new SimpleModule()
                                .addDeserializer(ComputeEnvExportFormat.class, new ComputeEnvExportFormatDeserializer())
                )
                .readValue(json, ComputeEnvExportFormat.class);
    }

    public static String serialize(final ComputeEnvExportFormat ceExport) throws JsonProcessingException {
        return buildMapper()
                .writerWithDefaultPrettyPrinter()
                .withAttribute("labels", ceExport.getLabels())
                .writeValueAsString(ceExport.getConfig());
    }

    private static ObjectMapper buildMapper() {
        ObjectMapper mapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .addMixIn(ComputeConfig.class, ComputeConfigMixin.class);
        mapper.configOverride(List.class).setSetterInfo(JsonSetter.Value.forContentNulls(Nulls.AS_EMPTY));
        return mapper;
    }

    /**
     * This mixin class allows to add a virtual 'labels' field to {@link ComputeConfig}.
     * See attribute inclusion in {@link ComputeEnvExportFormat#serialize(ComputeEnvExportFormat)}
     */
    @JsonAppend(
            attrs = {
                    @JsonAppend.Attr(value = "labels")
            }
    )
    public static abstract class ComputeConfigMixin {}

    /**
     * Custom deserializer for extended {@link ComputeConfig} JSON representation including 'labels' field.
     * Handles the case where 'labels' field is non-existent.
     */
    public static class ComputeEnvExportFormatDeserializer extends StdDeserializer<ComputeEnvExportFormat> {

        public ComputeEnvExportFormatDeserializer() {
            this(null);
        }

        public ComputeEnvExportFormatDeserializer(Class<ComputeEnvExportFormat> vc) {
            super(vc);
        }

        @Override
        public ComputeEnvExportFormat deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JsonProcessingException {

            JsonNode root = parser.getCodec().readTree(parser);

            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // ignore 'labels' field for now

            ComputeConfig cfg = mapper.readValue(root.toString(), ComputeConfig.class);

            JsonNode labels = root.get("labels");
            if (labels != null) {
                List<LabelDbDto> labelDbDtos = mapper.readValue(labels.toString(), new TypeReference<List<LabelDbDto>>() {});
                return new ComputeEnvExportFormat(cfg, labelDbDtos);
            }

            return new ComputeEnvExportFormat(cfg, Collections.emptyList());
        }
    }
}
