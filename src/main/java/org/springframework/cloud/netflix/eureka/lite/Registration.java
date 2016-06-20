package org.springframework.cloud.netflix.eureka.lite;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Spencer Gibb
 */
@Data
@AllArgsConstructor
public class Registration {
	private final ApplicationInfoManager applicationInfoManager;
	private final EurekaClient eurekaClient;
	private final ApplicationStatus applicationStatus;

	public Registration(ApplicationInfoManager applicationInfoManager, EurekaClient eurekaClient, Application application) {
		this(applicationInfoManager, eurekaClient, new ApplicationStatus(application, null));
	}

	public String getRegistrationKey() {
		return this.applicationStatus.getApplication().getRegistrationKey();
	}

	public String getApplicationName() {
		return this.applicationStatus.getApplication().getName();
	}
}
