package org.springframework.cloud.netflix.eureka.lite;

import static org.springframework.cloud.netflix.eureka.lite.Application.computeRegistrationKey;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.CloudEurekaClient;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.eureka.InstanceInfoFactory;
import org.springframework.cloud.netflix.eureka.MutableDiscoveryClientOptionalArgs;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;

/**
 * @author Spencer Gibb
 */
@RestController
public class EurekaLiteController implements Closeable {

	@Autowired
	ConfigurableApplicationContext context;

	@Autowired
	InetUtils inetUtils;

	ConcurrentHashMap<String, Registration> registrations = new ConcurrentHashMap<>();

	@RequestMapping(path = "/apps", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity register(@Valid @RequestBody Application application, HttpServletRequest request) throws Exception {

		if (registrations.containsKey(application.getRegistrationKey())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Already registered: " + application);
		}

		EurekaInstanceConfigBean instanceConfig = new EurekaInstanceConfigBean(inetUtils);
		instanceConfig.setAppname(application.getName());
		instanceConfig.setVirtualHostName(application.getName());
		instanceConfig.setInstanceId(application.getInstance_id());
		instanceConfig.setHostname(application.getHostname());
		instanceConfig.setNonSecurePort(application.getPort());

		InstanceInfo instanceInfo = new InstanceInfoFactory().create(instanceConfig);

		ApplicationInfoManager applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);

		EurekaClientConfigBean clientConfig = new EurekaClientConfigBean();
		CloudEurekaClient eurekaClient = new CloudEurekaClient(applicationInfoManager, clientConfig, new MutableDiscoveryClientOptionalArgs(), this.context);

		Registration registration = new Registration(applicationInfoManager, eurekaClient, application);

		eurekaClient.registerHealthCheck(new ApplicationHealthCheckHandler(registration.getApplicationStatus()));

		this.registrations.put(application.getRegistrationKey(), registration);

		applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);


		URI location = new URI(request.getRequestURI() + "/" + application.getName() + "/" + application.getInstance_id());
		return ResponseEntity.created(location).build();
	}

	@RequestMapping(path = "/apps/{name}/{instanceId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity unregister(@PathVariable("name") String name, @PathVariable("instanceId") String instanceId) {
		String registrationKey = computeRegistrationKey(name, instanceId);
		if (!this.registrations.containsKey(registrationKey)) {
			return ResponseEntity.notFound().build();
		}

		Registration registration = this.registrations.get(registrationKey);
		close(registration);

		return ResponseEntity.noContent().build();
	}


	@RequestMapping(path = "/apps/{name}/{instanceId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getInstance(@PathVariable("name") String name, @PathVariable("instanceId") String instanceId) {
		String registrationKey = computeRegistrationKey(name, instanceId);
		if (!this.registrations.containsKey(registrationKey)) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(this.registrations.get(registrationKey).getApplicationStatus());
	}


	@RequestMapping(path = "/apps", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Collection<ApplicationStatus> listApps() {
		ArrayList<ApplicationStatus> applications = new ArrayList<>();
		for (Registration registration : this.registrations.values()) {
			applications.add(registration.getApplicationStatus());
		}
		return applications;
	}

	@RequestMapping(path = "/apps/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Collection<ApplicationStatus> listApps(@PathVariable("name") String name) {
		ArrayList<ApplicationStatus> applications = new ArrayList<>();
		for (Registration registration : this.registrations.values()) {
			if (registration.getApplicationStatus().getApplication().getName().equals(name)) {
				applications.add(registration.getApplicationStatus());
			}
		}
		return applications;
	}

	void close(Registration registration) {
		registration.getEurekaClient().shutdown();
	}

	@Override
	public void close() throws IOException {
		for (Registration registration : this.registrations.values()) {
			close(registration);
		}
		this.registrations.clear();
	}
}
