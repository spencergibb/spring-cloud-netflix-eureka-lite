package org.springframework.cloud.netflix.eureka.lite;

import com.netflix.appinfo.InstanceInfo;

import org.springframework.cloud.netflix.eureka.lite.RedisCrudRepository.RedisRegistration;
/**
 * @author Spencer Gibb
 */
public class RedisRegistrationRepository implements RegistrationRepository {

	private RedisCrudRepository repo;
	private Eureka eureka;

	public RedisRegistrationRepository(RedisCrudRepository repo, Eureka eureka) {
		this.repo = repo;
		this.eureka = eureka;
	}

	@Override
	public boolean exists(String id) {
		return this.repo.exists(id);
	}

	@Override
	public void delete(String id) {
		this.repo.delete(id);
	}

	@Override
	public void deleteAll() {
		this.repo.deleteAll();
	}

	@Override
	public Iterable<Registration> findAll() {
		throw new UnsupportedOperationException("findAll is not implemented");
	}

	@Override
	public Registration findOne(String id) {
		RedisRegistration r = this.repo.findOne(id);
		Application application = new Application(r.getName(), r.getInstance_id(), r.getHostname(), r.getPort());
		InstanceInfo.InstanceStatus instanceStatus = InstanceInfo.InstanceStatus.toEnum(r.getInstanceStatus());
		InstanceInfo instanceInfo = this.eureka.getInstanceInfo(application, r.getLastUpdatedTimestamp(), r.getLastDirtyTimestamp());
		Registration registration = new Registration(instanceInfo, new ApplicationStatus(application, instanceStatus));
		return registration;
	}

	@Override
	public Registration save(Registration registration) {
		RedisRegistration r = new RedisRegistration();
		r.setKey(registration.getRegistrationKey());
		Application app = registration.getApplicationStatus().getApplication();
		r.setHostname(app.getHostname());
		r.setInstance_id(app.getInstance_id());
		r.setName(app.getName());
		r.setPort(app.getPort());
		InstanceInfo.InstanceStatus status = registration.getApplicationStatus().getStatus();
		r.setInstanceStatus(status.toString());
		r.setLastDirtyTimestamp(registration.getInstanceInfo().getLastDirtyTimestamp());
		r.setLastUpdatedTimestamp(registration.getInstanceInfo().getLastUpdatedTimestamp());
		this.repo.save(r);
		return registration;
	}
}
