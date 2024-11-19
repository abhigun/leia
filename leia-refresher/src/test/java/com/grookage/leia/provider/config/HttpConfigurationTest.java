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

package com.grookage.leia.provider.config;

import com.grookage.leia.models.ResourceHelper;
import com.grookage.leia.provider.endpoint.EndPointScheme;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HttpConfigurationTest {

    @Test
    @SneakyThrows
    void testHttpConfiguration() {
        final var httpConfiguration = ResourceHelper.getResource(
                "httpConfiguration.json",
                LeiaHttpConfiguration.class
        );
        Assertions.assertNotNull(httpConfiguration);
        Assertions.assertEquals("testHost", httpConfiguration.getHost());
        Assertions.assertEquals(8080, httpConfiguration.getPort());
        Assertions.assertEquals(EndPointScheme.HTTPS, httpConfiguration.getScheme());
        Assertions.assertEquals(30, httpConfiguration.getRefreshTimeSeconds());
        Assertions.assertNull(httpConfiguration.getRootPathPrefix());
    }
}