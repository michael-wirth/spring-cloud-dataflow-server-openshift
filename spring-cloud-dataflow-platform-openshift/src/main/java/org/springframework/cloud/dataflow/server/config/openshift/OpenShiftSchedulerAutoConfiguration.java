package org.springframework.cloud.dataflow.server.config.openshift;

import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
 * Configures the Spring Cloud Openshift Scheduler based on feature toggle settings and if
 * running on Openshift.
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
