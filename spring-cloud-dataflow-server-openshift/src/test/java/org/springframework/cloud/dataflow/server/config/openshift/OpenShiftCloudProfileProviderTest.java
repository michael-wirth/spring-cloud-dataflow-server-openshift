package org.springframework.cloud.dataflow.server.config.openshift;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

public class OpenShiftCloudProfileProviderTest {

	@Rule
	public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

	@Test
	public void isNotOpenshiftEnvironment() {
		Environment environment = new StandardEnvironment();

		assertThat(new OpenShiftCloudProfileProvider().isCloudPlatform(environment))
				.isFalse();
	}

	@Test
	public void isOpenshiftEnvironment() {
		environmentVariables.set("KUBERNETES_NAMESPACE", "default");
		Environment environment = new StandardEnvironment();

		assertThat(new OpenShiftCloudProfileProvider().isCloudPlatform(environment))
				.isTrue();
	}

}
