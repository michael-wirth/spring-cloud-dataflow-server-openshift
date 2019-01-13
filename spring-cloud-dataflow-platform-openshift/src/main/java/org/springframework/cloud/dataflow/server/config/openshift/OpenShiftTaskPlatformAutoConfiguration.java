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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.dataflow.core.Launcher;
import org.springframework.cloud.dataflow.core.TaskPlatform;
import org.springframework.cloud.deployer.autoconfigure.DelegatingResourceLoaderBuilderCustomizer;
import org.springframework.cloud.deployer.resource.docker.DockerResourceLoader;
import org.springframework.cloud.deployer.resource.maven.MavenProperties;
import org.springframework.cloud.deployer.spi.openshift.OpenShiftDeployerProperties;
import org.springframework.cloud.deployer.spi.openshift.OpenShiftTaskLauncher;
import org.springframework.cloud.deployer.spi.openshift.ResourceAwareOpenShiftTaskLauncher;
import org.springframework.cloud.deployer.spi.openshift.ResourceHash;
import org.springframework.cloud.deployer.spi.openshift.maven.MavenOpenShiftTaskLauncher;
import org.springframework.cloud.deployer.spi.openshift.maven.MavenResourceJarExtractor;
import org.springframework.cloud.deployer.spi.openshift.resources.pod.OpenShiftContainerFactory;
import org.springframework.cloud.deployer.spi.openshift.resources.volumes.VolumeMountFactory;
import org.springframework.cloud.deployer.spi.task.TaskLauncher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the Spring Cloud TaskPlatform based on feature toggle settings and ifrunning on
 * Openshift.
 *
 * @author Michael Wirth
 */
@Configuration
@EnableConfigurationProperties(OpenShiftPlatformProperties.class)
public class OpenShiftTaskPlatformAutoConfiguration {

	@Bean
	public TaskPlatform openShiftTaskPlatform(
			OpenShiftPlatformProperties openShiftPlatformProperties,
			MavenProperties mavenProperties, VolumeMountFactory volumeMountFactory,
			MavenResourceJarExtractor mavenResourceJarExtractor,
			ResourceHash resourceHash) {
		List<Launcher> launchers = new ArrayList<>();
		Map<String, OpenShiftDeployerProperties> k8sDeployerPropertiesMap = openShiftPlatformProperties
				.getAccounts();
		k8sDeployerPropertiesMap.forEach((key, value) -> {
			Launcher launcher = createAndSaveKubernetesTaskLaunchers(key, value,
					mavenProperties, volumeMountFactory, mavenResourceJarExtractor,
					resourceHash);
			launchers.add(launcher);
		});

		return new TaskPlatform("openshift", launchers);
	}

	protected Launcher createAndSaveKubernetesTaskLaunchers(String account,
			OpenShiftDeployerProperties openShiftDeployerProperties,
			MavenProperties mavenProperties, VolumeMountFactory volumeMountFactory,
			MavenResourceJarExtractor mavenResourceJarExtractor,
			ResourceHash resourceHash) {
		OpenShiftClient openShiftClient = new DefaultOpenShiftClient()
				.inNamespace(openShiftDeployerProperties.getNamespace());
		OpenShiftContainerFactory containerFactory = new OpenShiftContainerFactory(
				openShiftDeployerProperties, volumeMountFactory);
		TaskLauncher openShiftTaskLauncher = new ResourceAwareOpenShiftTaskLauncher(
				new OpenShiftTaskLauncher(openShiftDeployerProperties, openShiftClient,
						containerFactory),
				new MavenOpenShiftTaskLauncher(openShiftDeployerProperties,
						mavenProperties, openShiftClient, mavenResourceJarExtractor,
						resourceHash, containerFactory));

		Launcher launcher = new Launcher(account, "openshift", openShiftTaskLauncher);
		launcher.setDescription(
				String.format("master url = [%s], namespace = [%s], api version = [%s]",
						openShiftClient.getMasterUrl(), openShiftClient.getNamespace(),
						openShiftClient.getApiVersion()));
		return launcher;
	}

	@Bean
	public DelegatingResourceLoaderBuilderCustomizer dockerDelegatingResourceLoaderBuilderCustomizer(
			MavenProperties mavenProperties) {
		return (customizer) -> customizer.loader("docker", new DockerResourceLoader());
	}

}