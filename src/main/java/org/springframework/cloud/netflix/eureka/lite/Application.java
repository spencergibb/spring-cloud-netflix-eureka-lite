package org.springframework.cloud.netflix.eureka.lite;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.URI;

/**
 * @author Spencer Gibb
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Application {
	@NotBlank
	private String name;
	@NotBlank
	private String instance_id;
	@NotBlank
	private String hostname;
	@Min(0)
	@Max(65535)
	private int port;

	@NotNull
	private URI health_uri;

	@JsonIgnore
	public String getRegistrationKey() {
		return computeRegistrationKey(name, instance_id);
	}

	static String computeRegistrationKey(String name, String instanceId) {
		return name + ":" + instanceId;
	}
}
