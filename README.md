# Eureka Lite

Run `EurekaLiteApplication` with `--server.port=8762`

```
$ cd src/test/resources/test/client1
$ twistd -no web --path=. --port=8081

# in another terminal
$ cd src/test/resources/test/client2
$ twistd -no web --path=. --port=8082

# in another terminal
$ cd src/test/resources/test/client3
$ twistd -no web --path=. --port=8181

# in another terminal
$ http POST :8762/apps name=myapp instance_id=app1 hostname=localhost port=8081 health_uri=http://localhost:8081/health.json
$ http POST :8762/apps name=myapp instance_id=app2 hostname=localhost port=8082 health_uri=http://localhost:8082/health.json
$ http POST :8762/apps name=anotherapp instance_id=anotherapp1 hostname=localhost port=8181 health_uri=http://localhost:8181/health.json

$ http DELETE :8762/apps/myapp/app2

$ http GET :8762/apps                                                                              (10055)[16:35:57]
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Date: Mon, 20 Jun 2016 22:35:59 GMT
Server: Apache-Coyote/1.1
Transfer-Encoding: chunked
X-Application-Context: application:8762

[
    {
        "application": {
            "health_uri": "http://localhost:8082/health.json",
            "hostname": "localhost",
            "instance_id": "app2",
            "name": "myapp",
            "port": 8082
        },
        "status": "DOWN"
    },
    {
        "application": {
            "health_uri": "http://localhost:8081/health.json",
            "hostname": "localhost",
            "instance_id": "app1",
            "name": "myapp",
            "port": 8081
        },
        "status": "UP"
    },
    {
        "application": {
            "health_uri": "http://localhost:8181/health.json",
            "hostname": "localhost",
            "instance_id": "anotherapp1",
            "name": "anotherapp",
            "port": 8181
        },
        "status": "UP"
    }
]

$ http GET :8762/apps/anotherapp
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Date: Mon, 20 Jun 2016 22:41:27 GMT
Server: Apache-Coyote/1.1
Transfer-Encoding: chunked
X-Application-Context: application:8762

[
    {
        "application": {
            "health_uri": "http://localhost:8181/health.json",
            "hostname": "localhost",
            "instance_id": "anotherapp1",
            "name": "anotherapp",
            "port": 8181
        },
        "status": "UP"
    }
]

$ http GET :8762/apps/myapp/app1
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Date: Mon, 20 Jun 2016 22:44:11 GMT
Server: Apache-Coyote/1.1
Transfer-Encoding: chunked
X-Application-Context: application:8762

{
    "application": {
        "health_uri": "http://localhost:8081/health.json",
        "hostname": "localhost",
        "instance_id": "app1",
        "name": "myapp",
        "port": 8081
    },
    "status": "UP"
}
```

## Todo

- [ ] Repository Interface
