# fortress-examples

Java code examples for how to use the Apache Fortress API.

1. Build docker image with some base data 

```
docker build . -t fortress-openldap-test-with-data
```

2. Start container 

```
docker run -it -p 10389:389 fortress-openldap-test-with-data
```

3. Run tests