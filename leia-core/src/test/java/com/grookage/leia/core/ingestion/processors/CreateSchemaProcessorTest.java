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

package com.grookage.leia.core.ingestion.processors;

import com.grookage.leia.models.ResourceHelper;
import com.grookage.leia.models.exception.LeiaException;
import com.grookage.leia.models.request.SearchRequest;
import com.grookage.leia.models.schema.SchemaDetails;
import com.grookage.leia.models.schema.SchemaKey;
import com.grookage.leia.models.schema.engine.SchemaContext;
import com.grookage.leia.models.schema.engine.SchemaState;
import com.grookage.leia.models.schema.ingestion.CreateSchemaRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

class CreateSchemaProcessorTest extends SchemaProcessorTest {

    @Override
    SchemaProcessor getSchemaProcessor() {
        return CreateSchemaProcessor.builder()
                .repositorySupplier(getRepositorySupplier())
                .build();
    }

    @Test
    @SneakyThrows
    void testCreateSchema() {
        final var schemaProcessor = getSchemaProcessor();
        final var schemaContext = new SchemaContext();
        Assertions.assertThrows(LeiaException.class, () -> schemaProcessor.process(schemaContext));
        final var createSchemaRequest = ResourceHelper.getResource(
                "schema/createSchemaRequest.json",
                CreateSchemaRequest.class
        );
        schemaContext.addContext(CreateSchemaRequest.class.getSimpleName(), createSchemaRequest);

        Mockito.when(getRepositorySupplier().get().getSchemas(Mockito.any(SearchRequest.class)))
                .thenReturn(Collections.emptyList());

        Assertions.assertThrows(LeiaException.class, () -> schemaProcessor.process(schemaContext));
        schemaContext.addContext("USER", "testUser");
        schemaContext.addContext("EMAIL", "testEmail");
        schemaContext.addContext("USER_ID", "testUserId");
        getSchemaProcessor().process(schemaContext);
        Mockito.verify(getRepositorySupplier().get(), Mockito.times(1)).create(Mockito.any(SchemaDetails.class));
    }

    @Test
    @SneakyThrows
    void testCreateSchemaAlreadyExists_withDifferentVersion() {
        final var schemaContext = new SchemaContext();
        final var schemaProcessor = getSchemaProcessor();
        final var createSchemaRequest = ResourceHelper.getResource(
                "schema/createSchemaRequest.json",
                CreateSchemaRequest.class
        );
        schemaContext.addContext("USER", "testUser");
        schemaContext.addContext("EMAIL", "testEmail");
        schemaContext.addContext("USER_ID", "testUserId");
        schemaContext.addContext(CreateSchemaRequest.class.getSimpleName(), createSchemaRequest);

        SchemaKey schemaKey = createSchemaRequest.getSchemaKey();
        SchemaDetails existingSchema = new SchemaDetails();
        existingSchema.setSchemaKey(SchemaKey.builder()
                .orgId(schemaKey.getOrgId())
                .namespace(schemaKey.getNamespace())
                .tenantId(schemaKey.getTenantId())
                .schemaName(schemaKey.getSchemaName())
                .type(schemaKey.getType())
                .version("random")
                .build());
        existingSchema.setSchemaState(SchemaState.CREATED);

        Mockito.when(getRepositorySupplier().get().getSchemas(Mockito.any(SearchRequest.class)))
               .thenReturn(List.of(existingSchema));

        Assertions.assertThrows(LeiaException.class, () -> schemaProcessor.process(schemaContext));
    }

    @Test
    @SneakyThrows
    void testCreateSchemaAlreadyExists() {
        final var schemaContext = new SchemaContext();
        final var schemaProcessor = getSchemaProcessor();
        final var createSchemaRequest = ResourceHelper.getResource(
                "schema/createSchemaRequest.json",
                CreateSchemaRequest.class
        );
        schemaContext.addContext("USER", "testUser");
        schemaContext.addContext("EMAIL", "testEmail");
        schemaContext.addContext("USER_ID", "testUserId");
        schemaContext.addContext(CreateSchemaRequest.class.getSimpleName(), createSchemaRequest);

        SchemaKey schemaKey = createSchemaRequest.getSchemaKey();
        SchemaDetails existingSchema = new SchemaDetails();
        existingSchema.setSchemaKey(schemaKey);  // Same key as the request

        Mockito.when(getRepositorySupplier().get().getSchemas(Mockito.any(SearchRequest.class)))
               .thenReturn(List.of(existingSchema));

        Assertions.assertThrows(LeiaException.class, () -> schemaProcessor.process(schemaContext));
    }
}
