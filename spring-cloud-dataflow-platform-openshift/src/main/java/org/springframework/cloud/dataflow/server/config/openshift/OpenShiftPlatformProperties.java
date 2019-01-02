package org.springframework.cloud.dataflow.server.config.openshift;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.deployer.spi.openshift.OpenShiftDeployerProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Michael Wirth
 */
@ConfigurationProperties("spring.cloud.dataflow.task.platform.openshift")
public class OpenShiftPlatformProperties {

	private Map<String, OpenShiftDeployerProperties> accounts = new LinkedHashMap<>();

	public Map<String, OpenShiftDeployerProperties> getAccounts() {
		return accounts;
	}

	public void setAccounts(Map<String, OpenShiftDeployerProperties> accounts) {
		this.accounts = accounts;
	}
}
