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
	public ResponseEntity<RegistrationDTO> register(@Valid @RequestBody Application application, HttpServletRequest request) throws Exception {
		Registration registration = this.eureka.register(application);

		URI location = new URI(request.getRequestURI() + "/" + application.getName() + "/" + application.getInstance_id());
		RegistrationDTO dto = new RegistrationDTO(registration);
		return ResponseEntity.created(location).body(dto);
	}

	@RequestMapping(path = "/apps/{name}/{instanceId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity unregister(@PathVariable("name") String name, @PathVariable("instanceId") String instanceId) {
		this.eureka.cancel(name, instanceId);

		return ResponseEntity.noContent().build();
	}

	@RequestMapping(path = "/apps/{name}/{instanceId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RegistrationDTO> getInstance(@PathVariable("name") String name, @PathVariable("instanceId") String instanceId) {
		RegistrationDTO dto = this.eureka.getInstance(name, instanceId);
		return ResponseEntity.ok(dto);
	}

	@RequestMapping(path = "/apps/{name}/{instanceId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity renew(@PathVariable("name") String name, @PathVariable("instanceId") String instanceId,
								@RequestBody RegistrationDTO dto) {
		InstanceInfo instanceInfo = this.eureka.getInstanceInfo(dto.getApplication(),
				dto.getLastUpdatedTimestamp(), dto.getLastDirtyTimestamp());
		Registration registration = new Registration(instanceInfo, new ApplicationStatus(dto.getApplication(), dto.getInstanceStatus()));
		this.eureka.renew(registration);

		return ResponseEntity.ok().build();
	}


	@RequestMapping(path = "/apps", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, List<RegistrationDTO>> listApps() {
		return this.eureka.getApplications();
	}

	@RequestMapping(path = "/apps/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Collection<RegistrationDTO> listApps(@PathVariable("name") String name) {
		return this.eureka.getInstances(name);
	}
}
