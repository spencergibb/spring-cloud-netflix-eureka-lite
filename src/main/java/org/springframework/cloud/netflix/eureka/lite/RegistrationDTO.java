package org.springframework.cloud.netflix.eureka.lite;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.netflix.appinfo.InstanceInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Spencer Gibb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class RegistrationDTO {

	@NotNull
	private Application application;

	@NotNull
	private InstanceInfo.InstanceStatus instanceStatus;

	@Min(1)
	private long lastDirtyTimestamp;

	@Min(1)
	private long lastUpdatedTimestamp;

	//TODO: remove one of Registration/RegistrationDTO
	public RegistrationDTO(Registration reg) {
		ApplicationStatus applicationStatus = reg.getApplicationStatus();
		this.application = applicationStatus.getApplication();
		this.instanceStatus = applicationStatus.getStatus();
		this.lastDirtyTimestamp = reg.getInstanceInfo().getLastDirtyTimestamp();
		this.lastUpdatedTimestamp = reg.getInstanceInfo().getLastUpdatedTimestamp();
	}

	public RegistrationDTO(RegistrationDTO other) {
		this.application = other.getApplication();
		this.instanceStatus = other.getInstanceStatus();
		this.lastDirtyTimestamp = other.getLastDirtyTimestamp();
		this.lastUpdatedTimestamp = other.getLastUpdatedTimestamp();
	}

	public void update(InstanceInfo instanceInfo) {
		if (instanceInfo == null) {
			//TODO: log warning
			return;
		}
		setInstanceStatus(instanceInfo.getStatus());
		setLastDirtyTimestamp(instanceInfo.getLastDirtyTimestamp());
		setLastUpdatedTimestamp(instanceInfo.getLastUpdatedTimestamp());
	}
}
