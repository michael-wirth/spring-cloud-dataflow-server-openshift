/*
 * Copyright 2018-2019 the original author or authors.
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
 */

package org.springframework.cloud.dataflow.server.config.openshift;

import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.dataflow.server.config.features.SchedulerConfiguration;
import org.springframework.cloud.deployer.resource.maven.MavenProperties;
import org.springframework.cloud.deployer.spi.openshift.OpenShiftDeployerProperties;
import org.springframework.cloud.deployer.spi.openshift.ResourceHash;
import org.springframework.cloud.deployer.spi.openshift.maven.MavenResourceJarExtractor;
import org.springframework.cloud.deployer.spi.openshift.resources.pod.OpenShiftContainerFactory;
import org.springframework.cloud.deployer.spi.openshift.resources.volumes.VolumeMountFactory;
import org.springframework.cloud.scheduler.spi.core.Scheduler;
import org.springframework.cloud.scheduler.spi.openshift.OpenShiftScheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the Spring Cloud Scheduler based on feature toggle settings and if running
 * on Openshift.
 *
 * @author Chris Schaefer
 */
@Configuration
@Conditional({ SchedulerConfiguration.SchedulerConfigurationPropertyChecker.class })
@ConditionalOnProperty(name = "kubernetes.service.host")
@EnableConfigurationProperties(OpenShiftPlatformProperties.class)
public class OpenShiftSchedulerAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public Scheduler scheduler(OpenShiftPlatformProperties openShiftPlatformProperties,
			MavenProperties mavenProperties,
			MavenResourceJarExtractor mavenResourceJarExtractor,
			ResourceHash resourceHash, VolumeMountFactory volumeMountFactory) {

		OpenShiftDeployerProperties openShiftDeployerProperties = openShiftPlatformProperties
				.getAccounts().get("scheduler");
		if (openShiftDeployerProperties == null) {
			openShiftDeployerProperties = openShiftPlatformProperties.getAccounts()
					.values().stream().findFirst().get();
		}

		OpenShiftClient openShiftClient = new DefaultOpenShiftClient()
				.inNamespace(openShiftDeployerProperties.getNamespace());

		return new OpenShiftScheduler(openShiftClient, openShiftDeployerProperties,
				mavenProperties, mavenResourceJarExtractor, resourceHash,
				new OpenShiftContainerFactory(openShiftDeployerProperties,
						volumeMountFactory));
	}

}
