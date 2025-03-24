/*
 * Copyright (c) 2024. Koushik R <rkoushik.14@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grookage.leia.models.schema;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Joiner;
import com.grookage.leia.models.SchemaConstants;
import com.grookage.leia.models.attributes.SchemaAttribute;
import com.grookage.leia.models.schema.engine.SchemaState;
import com.grookage.leia.models.schema.transformer.TransformationTarget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchemaDetails implements Comparable<SchemaDetails> {
    @NotBlank
    String namespace;
    @NotBlank
    String schemaName;
    @NotBlank
    String version;
    String description;
    @NotNull
    SchemaState schemaState;
    @NotNull
    SchemaType schemaType;
    SchemaValidationType validationType = SchemaValidationType.MATCHING;
    SchemaReference parentReference;
    List<SchemaReference> childReferences;
    @NotEmpty
    Set<SchemaAttribute> attributes;
    @Builder.Default
    Set<TransformationTarget> transformationTargets = Set.of();
    @Builder.Default
    Set<SchemaHistoryItem> histories = new HashSet<>();
    @Builder.Default
    List<String> tags = new ArrayList<>();

    @JsonIgnore
    public String getReferenceId() {
        return Joiner.on(SchemaConstants.KEY_DELIMITER).join(namespace, schemaName, version).toUpperCase(Locale.ROOT);
    }

    @JsonIgnore
    public String getReferenceTag() {
        return Joiner.on(SchemaConstants.KEY_DELIMITER).join(namespace, schemaName).toUpperCase(Locale.ROOT);
    }

    @JsonIgnore
    public SchemaKey getSchemaKey() {
        return SchemaKey.builder()
                .schemaName(schemaName)
                .namespace(namespace)
                .version(version)
                .build();
    }

    @Override
    public int compareTo(SchemaDetails o) {
        return this.version.compareTo(o.getVersion());
    }

    @JsonIgnore
    public synchronized void addHistory(SchemaHistoryItem historyItem) {
        if (null == histories) {
            histories = new HashSet<>();
        }
        histories.add(historyItem);
    }
}
