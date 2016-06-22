package org.springframework.cloud.netflix.eureka.lite;

/**
 * @author Spencer Gibb
 */
public interface RegistrationRepository {
	boolean exists(String id);
	void delete(String id);
	void deleteAll();
	Iterable<Registration> findAll();
	Registration findOne(String id);
	Registration save(Registration registration);
}
