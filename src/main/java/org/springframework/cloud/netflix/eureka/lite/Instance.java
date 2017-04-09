package org.springframework.cloud.netflix.eureka.lite;

import com.netflix.appinfo.InstanceInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author Spencer Gibb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class Instance {
	@NotNull
	private InstanceInfo.InstanceStatus status;

	@Min(1)
	private long lastDirtyTimestamp;

	@Min(1)
	private long lastUpdatedTimestamp;

	public Instance(Instance other) {
		this.status = other.getStatus();
		this.lastDirtyTimestamp = other.getLastDirtyTimestamp();
		this.lastUpdatedTimestamp = other.getLastUpdatedTimestamp();
	}

	public void update(InstanceInfo instanceInfo) {
		if (instanceInfo == null) {
			//TODO: log warning
			return;
		}
		setStatus(instanceInfo.getStatus());
		setLastDirtyTimestamp(instanceInfo.getLastDirtyTimestamp());
		setLastUpdatedTimestamp(instanceInfo.getLastUpdatedTimestamp());
	}
}
