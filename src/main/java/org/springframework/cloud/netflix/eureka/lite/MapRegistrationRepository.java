package org.springframework.cloud.netflix.eureka.lite;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Spencer Gibb
 */
public class MapRegistrationRepository implements RegistrationRepository {

	private ConcurrentHashMap<String, Registration> registrations = new ConcurrentHashMap<>();

	@Override
	public boolean exists(String id) {
		return this.registrations.containsKey(id);
	}

	@Override
	public void delete(String id) {
		if (exists(id)) {
			Registration registration = findOne(id);
			if (registration != null) {
				this.registrations.remove(id);
			}
		}
	}

	@Override
	public void deleteAll() {
		this.registrations.clear();
	}

	@Override
	public Iterable<Registration> findAll() {
		return this.registrations.values();
	}

	@Override
	public Registration findOne(String id) {
		return this.registrations.get(id);
	}

	@Override
	public Registration save(Registration registration) {
		this.registrations.put(registration.getRegistrationKey(), registration);

		return registration;
	}
}
