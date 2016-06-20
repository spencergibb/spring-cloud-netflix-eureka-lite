/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.springframework.cloud.netflix.eureka.lite;

import static com.netflix.appinfo.InstanceInfo.InstanceStatus.DOWN;
import static com.netflix.appinfo.InstanceInfo.InstanceStatus.OUT_OF_SERVICE;
import static com.netflix.appinfo.InstanceInfo.InstanceStatus.UNKNOWN;
import static com.netflix.appinfo.InstanceInfo.InstanceStatus.UP;

import java.net.URI;
import java.util.Map;

import org.springframework.boot.actuate.health.Status;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.HealthCheckHandler;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;

/**
 * Eureka HealthCheckHandler that translates boot health status to
 * InstanceStatus so the proper status of the non-JVM app is sent to Eureka.
* @author Spencer Gibb
*/
class ApplicationHealthCheckHandler implements HealthCheckHandler {

    private URI uri;
    private ApplicationStatus applicationStatus;

    public ApplicationHealthCheckHandler(ApplicationStatus applicationStatus) {
        this.uri = applicationStatus.getApplication().getHealth_uri();
        this.applicationStatus = applicationStatus;
    }

    @Override
	@SuppressWarnings("unchecked")
    public InstanceStatus getStatus(InstanceStatus currentStatus) {
        InstanceStatus instanceStatus = UNKNOWN;
        if (uri == null) {
            instanceStatus = UP;
        } else {
			Map<String, Object> map = new RestTemplate().getForObject(uri, Map.class);
			Object status = map.get("status");
			if (status != null && status instanceof String) {
				instanceStatus = map(new Status(status.toString()));
			} else if (status != null && status instanceof Map) {
				Map<String, Object> statusMap = (Map<String, Object>) status;
				Object code = statusMap.get("code");
				if (code != null) {
					instanceStatus = map(new Status(code.toString()));
				}
			}
		}
		this.applicationStatus.setStatus(instanceStatus);
		return instanceStatus;
    }

    InstanceStatus map(Status status) {
        if (status.equals(Status.UP)) {
            return UP;
        } else if (status.equals(Status.OUT_OF_SERVICE)) {
            return OUT_OF_SERVICE;
        } else if (status.equals(Status.DOWN)) {
            return DOWN;
        }
        return UNKNOWN;
    }
}
