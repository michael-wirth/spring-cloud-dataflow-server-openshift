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

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.deployer.spi.openshift.OpenShiftDeployerProperties;

/**
 * {@link ConfigurationProperties} class to configure various settings of Data Flow
 * running on Openshift.
 *
 * @author Michael Wirth
 */
@ConfigurationProperties("spring.cloud.dataflow.task.platform.openshift")
public class OpenShiftPlatformProperties {

	private Map<String, OpenShiftDeployerProperties> accounts = new LinkedHashMap<>();

	public Map<String, OpenShiftDeployerProperties> getAccounts() {
		return this.accounts;
	}

	public void setAccounts(Map<String, OpenShiftDeployerProperties> accounts) {
		this.accounts = accounts;
	}

}
