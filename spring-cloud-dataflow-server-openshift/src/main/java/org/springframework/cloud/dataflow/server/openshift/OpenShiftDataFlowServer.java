package org.springframework.cloud.dataflow.server.openshift;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.dataflow.server.EnableDataFlowServer;
import org.springframework.cloud.deployer.spi.kubernetes.KubernetesAutoConfiguration;
import org.springframework.cloud.deployer.spi.local.LocalDeployerAutoConfiguration;

/**
 * Bootstrap class for the OpenShift 3 Spring Cloud Data Flow Server.
 *
 * @author Donovan Muller
 */
@SpringBootApplication(exclude = { LocalDeployerAutoConfiguration.class,
		KubernetesAutoConfiguration.class })
@EnableDataFlowServer
public class OpenShiftDataFlowServer {

	public static void main(String[] args) {
		SpringApplication.run(OpenShiftDataFlowServer.class, args);
	}

}
