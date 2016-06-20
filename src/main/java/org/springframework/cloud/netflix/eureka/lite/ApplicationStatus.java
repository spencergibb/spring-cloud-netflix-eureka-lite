package org.springframework.cloud.netflix.eureka.lite;

import com.netflix.appinfo.InstanceInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Spencer Gibb
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationStatus {
	private Application application;
	private InstanceInfo.InstanceStatus status;
}
