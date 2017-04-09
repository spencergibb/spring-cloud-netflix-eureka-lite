package org.springframework.cloud.netflix.eureka.lite;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.appinfo.InstanceInfo;

/**
 * @author Spencer Gibb
 */
@RestController
public class EurekaLiteController {

	private final Eureka eureka;
	private final EurekaLiteProperties properties;

	public EurekaLiteController(Eureka eureka, EurekaLiteProperties properties) {
		this.eureka = eureka;
		this.properties = properties;
	}

	@RequestMapping(path = "/apps", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Registration> register(@Valid @RequestBody Application application, HttpServletRequest request) throws Exception {
		InstanceInfo instanceInfo = this.eureka.register(application);

		URI location = new URI(request.getRequestURI() + "/" + application.getName() + "/" + application.getInstance_id());
		Registration registration = new Registration();
		registration.setApplication(application);
		registration.update(instanceInfo);
		return ResponseEntity.created(location).body(registration);
	}

	@RequestMapping(path = "/apps/{name}/{instanceId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity unregister(@PathVariable("name") String name, @PathVariable("instanceId") String instanceId) {
		this.eureka.cancel(name, instanceId);

		return ResponseEntity.noContent().build();
	}

	@RequestMapping(path = "/apps/{name}/{instanceId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Registration> getInstance(@PathVariable("name") String name, @PathVariable("instanceId") String instanceId) {
		Registration registration = this.eureka.getRegistration(name, instanceId);
		return ResponseEntity.ok(registration);
	}

	@RequestMapping(path = "/apps/{name}/{instanceId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity renew(@PathVariable("name") String name, @PathVariable("instanceId") String instanceId,
								@RequestBody Registration registration) {
		InstanceInfo instanceInfo = this.eureka.getInstanceInfo(registration);
		this.eureka.renew(instanceInfo);

		return ResponseEntity.ok().build();
	}


	@RequestMapping(path = "/apps", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, List<Registration>> listApps() {
		return this.eureka.getApplications();
	}

	@RequestMapping(path = "/apps/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Collection<Registration> listApps(@PathVariable("name") String name) {
		return this.eureka.getRegistrations(name);
	}
}
