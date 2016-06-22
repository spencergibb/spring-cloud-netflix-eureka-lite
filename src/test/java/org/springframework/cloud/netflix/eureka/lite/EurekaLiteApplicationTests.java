package org.springframework.cloud.netflix.eureka.lite;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.net.URI;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.CloudEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.netflix.appinfo.InstanceInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {EurekaLiteApplicationTests.TestConfig.class, EurekaLiteApplication.class })
@WebIntegrationTest(randomPort = true, value = "eureka.lite.redis.enabled=false")
public class EurekaLiteApplicationTests {

	@Value("${local.server.port}")
	int port;

	@Test
	public void contextLoads() {
		String url = "http://localhost:" + port + "/apps";

		Application application = getApplication("myapp", "app1", 8081);
		URI location = new TestRestTemplate().postForLocation(url, application);
		assertThat("location was wrong", location, is(equalTo(URI.create("/apps/myapp/app1"))));

		Application application2 = getApplication("myapp", "app2down", 8082);
		new TestRestTemplate().postForLocation(url, application2);

		ResponseEntity<List<ApplicationStatus>> entity = new TestRestTemplate().exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<ApplicationStatus>>() {
		});
		assertThat("entity was null", entity, is(notNullValue()));
		assertThat("wrong status", entity.getStatusCode(), is(equalTo(HttpStatus.OK)));
		List<ApplicationStatus> statuses = entity.getBody();
		assertThat("statuses was wrong size", statuses, hasSize(2));
	}

	private Application getApplication(String name, String instanceId, int port) {
		return new Application(name, instanceId, "localhost", port);
	}

	@Configuration
	static class TestConfig {

		@Bean
		public Eureka eureka(InetUtils inetUtils) {
			return new TestEureka(inetUtils);
		}
	}

	static class TestEureka extends Eureka {

		private InetUtils inetUtils;

		public TestEureka(InetUtils inetUtils) {
			super(inetUtils, mock(CloudEurekaClient.class));
			this.inetUtils = inetUtils;
		}

		@Override
		public Registration register(Application application) {
			InstanceInfo instanceInfo = getInstanceInfo(application);
			InstanceInfo.InstanceStatus status = InstanceInfo.InstanceStatus.UP;
			if (application.getInstance_id().endsWith("down")) {
				status = InstanceInfo.InstanceStatus.DOWN;
			}
			ApplicationStatus applicationStatus = new ApplicationStatus(application, status);
			Registration registration = new Registration(instanceInfo, applicationStatus);
			return registration;
		}

		@Override
		public void shutdown(Registration registration) {
			// noop
		}
	}

}
