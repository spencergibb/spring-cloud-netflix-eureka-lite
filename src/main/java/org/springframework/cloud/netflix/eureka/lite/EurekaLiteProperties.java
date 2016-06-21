package org.springframework.cloud.netflix.eureka.lite;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Spencer Gibb
 */
@Data
@ConfigurationProperties("eureka.lite")
public class EurekaLiteProperties {
	private boolean unregisterOnShutdown = false;
}
