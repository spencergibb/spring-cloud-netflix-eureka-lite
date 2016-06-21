package org.springframework.cloud.netflix.eureka.lite;

import com.netflix.appinfo.InstanceInfo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Spencer Gibb
 */
@Data
@AllArgsConstructor
public class Registration {
	private final InstanceInfo instanceInfo;
	private final ApplicationStatus applicationStatus;

	public Registration(InstanceInfo instanceInfo, Application application) {
		this(instanceInfo, new ApplicationStatus(application, null));
	}

	public String getRegistrationKey() {
		return this.applicationStatus.getApplication().getRegistrationKey();
	}

	public String getApplicationName() {
		return this.applicationStatus.getApplication().getName();
	}
}
