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

import com.grookage.leia.core.ingestion.VersionIDGenerator;
import com.grookage.leia.models.schema.engine.SchemaContext;
import com.grookage.leia.models.schema.engine.SchemaEvent;
import com.grookage.leia.repository.SchemaRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.function.Supplier;

@SuperBuilder
@Data
@AllArgsConstructor
public abstract class SchemaProcessor {

    private final Supplier<SchemaRepository> repositorySupplier;
    private final Supplier<VersionIDGenerator> versionSupplier;

    public abstract SchemaEvent name();

    public abstract void process(final SchemaContext schemaContext);

}