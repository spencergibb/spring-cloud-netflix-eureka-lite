package org.springframework.cloud.netflix.eureka.lite;

import com.netflix.discovery.shared.resolver.ClosableResolver;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import com.netflix.discovery.shared.transport.EurekaHttpClientFactory;
import com.netflix.discovery.shared.transport.TransportClientFactory;
import lombok.Data;

/**
 * @author Spencer Gibb
 */
@Data
public class EurekaTransport {
	private final EurekaHttpClientFactory eurekaHttpClientFactory;
	private final EurekaHttpClient eurekaHttpClient;
	private final TransportClientFactory transportClientFactory;
	private final ClosableResolver closableResolver;

	public void shutdown() {
		eurekaHttpClientFactory.shutdown();
		eurekaHttpClient.shutdown();
		transportClientFactory.shutdown();
		closableResolver.shutdown();
	}
}
