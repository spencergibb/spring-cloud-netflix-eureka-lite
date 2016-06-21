# Eureka Lite

Run `EurekaLiteApplication` with `--server.port=8762`

```
$ http POST :8762/apps name=myapp instance_id=app1 hostname=localhost port=8081
$ http POST :8762/apps name=myapp instance_id=app2 hostname=localhost port=8082
$ http POST :8762/apps name=anotherapp instance_id=anotherapp1 hostname=localhost port=8181

# in another terminal
$ watch -n30 http PUT :8762/apps/myapp/app1

# eventually anotherapp/anotherapp1 will auto unregister from eureka because it isn't sending heartbeats

$ http DELETE :8762/apps/myapp/app2

$ http GET :8762/apps
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Date: Mon, 20 Jun 2016 22:35:59 GMT
Server: Apache-Coyote/1.1
Transfer-Encoding: chunked
X-Application-Context: application:8762

[
    {
        "application": {
            "hostname": "localhost",
            "instance_id": "app2",
            "name": "myapp",
            "port": 8082
        },
        "status": "DOWN"
    },
    {
        "application": {
            "hostname": "localhost",
            "instance_id": "app1",
            "name": "myapp",
            "port": 8081
        },
        "status": "UP"
    },
    {
        "application": {
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
        "hostname": "localhost",
        "instance_id": "app1",
        "name": "myapp",
        "port": 8081
    },
    "status": "UP"
}

```

## Todo

- [X] Repository Interface
- [X] Distributed Heartbeat
- [ ] @EnableEurekaLite
