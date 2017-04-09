package org.springframework.cloud.netflix.eureka.lite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.CloudEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;

import static org.mockito.Mockito.mock;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EurekaLiteApplicationTests.TestConfig.class, EurekaLiteApplication.class },
		webEnvironment = RANDOM_PORT)
public class EurekaLiteApplicationTests {

	@Value("${local.server.port}")
	int port;

	@Test
	public void contextLoads() {
		String url = "http://localhost:" + port + "/apps";

		/*Application application = getApplication("myapp", "app1", 8081);
		URI location = new TestRestTemplate().postForLocation(url, application);
		assertThat("location was wrong", location, is(equalTo(URI.create("/apps/myapp/app1"))));

		Application application2 = getApplication("myapp", "app2down", 8082);
		new TestRestTemplate().postForLocation(url, application2);

		ResponseEntity<List<ApplicationStatus>> entity = new TestRestTemplate().exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<ApplicationStatus>>() {
		});
		assertThat("entity was null", entity, is(notNullValue()));
		assertThat("wrong status", entity.getStatusCode(), is(equalTo(HttpStatus.OK)));
		List<ApplicationStatus> statuses = entity.getBody();
		assertThat("statuses was wrong size", statuses, hasSize(2));*/
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
		public InstanceInfo register(Application application) {
			InstanceInfo instanceInfo = getInstanceInfo(application);
			InstanceStatus status = InstanceStatus.UP;
			if (application.getInstance_id().endsWith("down")) {
				status = InstanceStatus.DOWN;
			}
			return instanceInfo;
		}

		@Override
		public void cancel(String appName, String instanceId) {
			// noop
		}
	}

}
