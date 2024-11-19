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

package com.grookage.leia.core.ingestion.utils;

import com.grookage.leia.core.ingestion.VersionIDGenerator;
import com.grookage.leia.models.ResourceHelper;
import com.grookage.leia.models.attributes.DataType;
import com.grookage.leia.models.schema.ingestion.CreateSchemaRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SchemaUtilsTest {

    @Test
    @SneakyThrows
    void testSchemaUtils() {
        final var createSchemaRequest = ResourceHelper.getResource(
                "schema/createSchemaRequest.json",
                CreateSchemaRequest.class
        );
        final var generator = new VersionIDGenerator() {
            @Override
            public String generateVersionId(String prefix) {
                return "V1234";
            }
        };
        final var schemaDetails = SchemaUtils.toSchemaDetails(createSchemaRequest, "testUser",
                "testEmail", () -> generator
        );
        Assertions.assertNotNull(schemaDetails);
        final var schemaKey = schemaDetails.getSchemaKey();
        Assertions.assertNotNull(schemaKey);
        Assertions.assertEquals("testNamespace", schemaKey.getNamespace());
        Assertions.assertEquals("testSchema", schemaKey.getSchemaName());
        Assertions.assertEquals("V1234", schemaKey.getVersion());
        final var schemaMeta = schemaDetails.getSchemaMeta();
        Assertions.assertNotNull(schemaMeta);
        Assertions.assertEquals("testUser", schemaMeta.getCreatedBy());
        final var schemaAttributes = schemaDetails.getAttributes();
        Assertions.assertNotNull(schemaAttributes);
        Assertions.assertTrue(schemaAttributes.stream().anyMatch(each -> each.getType() == DataType.ARRAY));
    }
}