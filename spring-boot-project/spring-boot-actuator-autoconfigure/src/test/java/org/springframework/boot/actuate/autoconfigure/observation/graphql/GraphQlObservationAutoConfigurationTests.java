/*
 * Copyright 2012-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.actuate.autoconfigure.observation.graphql;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.tck.TestObservationRegistry;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.observation.GraphQlObservationInstrumentation;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GraphQlObservationAutoConfiguration}.
 *
 * @author Brian Clozel
 */
class GraphQlObservationAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withBean(TestObservationRegistry.class, TestObservationRegistry::create)
			.withConfiguration(AutoConfigurations.of(GraphQlObservationAutoConfiguration.class));

	@Test
	void backsOffWhenObservationRegistryIsMissing() {
		new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GraphQlObservationAutoConfiguration.class))
				.run((context) -> assertThat(context).doesNotHaveBean(GraphQlObservationInstrumentation.class));
	}

	@Test
	void definesInstrumentationWhenObservationRegistryIsPresent() {
		this.contextRunner.run((context) -> assertThat(context).hasSingleBean(GraphQlObservationInstrumentation.class));
	}

	@Test
	void instrumentationBacksOffIfAlreadyPresent() {
		this.contextRunner.withUserConfiguration(InstrumentationConfiguration.class)
				.run((context) -> assertThat(context).hasSingleBean(GraphQlObservationInstrumentation.class)
						.hasBean("customInstrumentation"));
	}

	@Configuration(proxyBeanMethods = false)
	static class InstrumentationConfiguration {

		@Bean
		GraphQlObservationInstrumentation customInstrumentation(ObservationRegistry registry) {
			return new GraphQlObservationInstrumentation(registry);
		}

	}

}
