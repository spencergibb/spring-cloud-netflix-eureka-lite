package org.springframework.cloud.netflix.eureka.lite;

import static org.springframework.cloud.netflix.eureka.lite.Application.computeRegistrationKey;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Spencer Gibb
 */
@RestController
public class EurekaLiteController implements Closeable {

	private Eureka eureka;

	private RegistrationRepository registrations;

	public EurekaLiteController(Eureka eureka, RegistrationRepository registrations) {
		this.eureka = eureka;
		this.registrations = registrations;
	}

	@RequestMapping(path = "/apps", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity register(@Valid @RequestBody Application application, HttpServletRequest request) throws Exception {

		if (registrations.exists(application.getRegistrationKey())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Already registered: " + application);
		}

		Registration registration = this.eureka.register(application);
		this.registrations.save(registration);

		URI location = new URI(request.getRequestURI() + "/" + application.getName() + "/" + application.getInstance_id());
		return ResponseEntity.created(location).build();
	}

	@RequestMapping(path = "/apps/{name}/{instanceId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity unregister(@PathVariable("name") String name, @PathVariable("instanceId") String instanceId) {
		String registrationKey = computeRegistrationKey(name, instanceId);
		if (!this.registrations.exists(registrationKey)) {
			return ResponseEntity.notFound().build();
		}

		Registration registration = this.registrations.findOne(registrationKey);
		this.eureka.shutdown(registration);

		this.registrations.delete(registrationKey);

		return ResponseEntity.noContent().build();
	}

	@RequestMapping(path = "/apps/{name}/{instanceId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getInstance(@PathVariable("name") String name, @PathVariable("instanceId") String instanceId) {
		String registrationKey = computeRegistrationKey(name, instanceId);
		if (!this.registrations.exists(registrationKey)) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(this.registrations.findOne(registrationKey).getApplicationStatus());
	}


	@RequestMapping(path = "/apps", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Collection<ApplicationStatus> listApps() {
		ArrayList<ApplicationStatus> applications = new ArrayList<>();
		for (Registration registration : this.registrations.finalAll()) {
			applications.add(registration.getApplicationStatus());
		}
		return applications;
	}

	@RequestMapping(path = "/apps/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Collection<ApplicationStatus> listApps(@PathVariable("name") String name) {
		ArrayList<ApplicationStatus> applications = new ArrayList<>();
		for (Registration registration : this.registrations.finalAll()) {
			if (registration.getApplicationName().equals(name)) {
				applications.add(registration.getApplicationStatus());
			}
		}
		return applications;
	}

	@Override
	public void close() throws IOException {
		for (Registration registration : this.registrations.finalAll()) {
			this.eureka.shutdown(registration);
		}
		this.registrations.deleteAll();
	}
}
