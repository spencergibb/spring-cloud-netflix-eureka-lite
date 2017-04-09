package org.springframework.cloud.netflix.eureka.lite;

import javax.validation.Valid;
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
public class Registration {

	@NotNull
	@Valid
	private Application application;

	@NotNull
	@Valid
	private Instance instance;

	public Registration(Registration other) {
		this.application = other.getApplication();
		this.instance = other.getInstance();
	}

	//TODO: refactor
	public void update(InstanceInfo instanceInfo) {
		this.instance.update(instanceInfo);
	}
}
