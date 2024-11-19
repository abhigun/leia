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

package com.grookage.leia.client.refresher;

import com.grookage.leia.client.datasource.NamespaceDataSource;
import com.grookage.leia.models.request.NamespaceRequest;
import com.grookage.leia.models.schema.SchemaDetails;
import com.grookage.leia.models.utils.MapperUtils;
import com.grookage.leia.provider.config.LeiaHttpConfiguration;
import com.grookage.leia.provider.suppliers.LeiaHttpSupplier;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.List;

@SuppressWarnings({"deprecation", "KotlinInternalInJava"})
@Getter
public class LeiaClientSupplier extends LeiaHttpSupplier<List<SchemaDetails>> {

    private final NamespaceDataSource namespaceDataSource;

    @Builder
    public LeiaClientSupplier(LeiaHttpConfiguration httpConfiguration, NamespaceDataSource namespaceDataSource) {
        super(httpConfiguration, LeiaClientMarshaller.getInstance(), "getClientNamespaces");
        this.namespaceDataSource = namespaceDataSource;
    }

    @Override
    protected String url() {
        return "/v1/schema/details/current";
    }

    @Override
    @SneakyThrows
    protected Request getRequest(String url) {
        final var requestBody = RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"),
                MapperUtils.mapper().writeValueAsString(NamespaceRequest.builder()
                        .namespaces(namespaceDataSource.getNamespaces())
                        .build()));
        return new Request.Builder()
                .url(endPoint(url))
                .post(requestBody)
                .build();
    }
}