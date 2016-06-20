package org.springframework.cloud.netflix.eureka.lite;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import org.springframework.beans.BeansException;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.CloudEurekaClient;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.eureka.InstanceInfoFactory;
import org.springframework.cloud.netflix.eureka.MutableDiscoveryClientOptionalArgs;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Spencer Gibb
 */
public class Eureka implements ApplicationContextAware {

	private InetUtils inetUtils;
	private ApplicationContext context;

	public Eureka(InetUtils inetUtils) {
		this.inetUtils = inetUtils;
	}

	public Registration register(Application application) {
		EurekaInstanceConfigBean instanceConfig = new EurekaInstanceConfigBean(inetUtils);
		instanceConfig.setAppname(application.getName());
		instanceConfig.setVirtualHostName(application.getName());
		instanceConfig.setInstanceId(application.getInstance_id());
		instanceConfig.setHostname(application.getHostname());
		instanceConfig.setNonSecurePort(application.getPort());

		InstanceInfo instanceInfo = new InstanceInfoFactory().create(instanceConfig);

		ApplicationInfoManager applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);

		EurekaClientConfigBean clientConfig = new EurekaClientConfigBean();
		CloudEurekaClient eurekaClient = new CloudEurekaClient(applicationInfoManager, clientConfig, new MutableDiscoveryClientOptionalArgs(), this.context);

		Registration registration = new Registration(applicationInfoManager, eurekaClient, application);

		eurekaClient.registerHealthCheck(new ApplicationHealthCheckHandler(registration.getApplicationStatus()));

		applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);

		return registration;
	}

	public void shutdown(Registration registration) {
		registration.getEurekaClient().shutdown();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}
}
