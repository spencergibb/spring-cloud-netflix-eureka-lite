package org.springframework.cloud.netflix.eureka.lite;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.repository.CrudRepository;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Spencer Gibb
 */
public interface RedisCrudRepository extends CrudRepository<RedisCrudRepository.RedisRegistration, String> {

	@Data
	@NoArgsConstructor
	@RedisHash("eureka_lite_registration")
	class RedisRegistration {
		@Id
		private String key;
		@NotBlank
		private String name;
		@NotBlank
		private String instance_id;
		@NotBlank
		private String hostname;
		@Min(0)
		@Max(65535)
		private int port;
		@NotBlank
		private String instanceStatus;

		private long lastDirtyTimestamp;

		private long lastUpdatedTimestamp;
	}
}
