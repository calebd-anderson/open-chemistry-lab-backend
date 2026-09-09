# Catalyst Engine
- BFF Spring Boot API

## Development
### Configure JDK 25 LTS
- [sdkman](https://sdkman.io/install/)
- [jabba](https://github.com/shyiko/jabba)
- [temurin](https://adoptium.net/temurin/releases/?version=25&os=any&arch=any)

>[!IMPORTANT]
>The version of Java used by the project must match the version output by `./mvnw --version`

>[!TIP]
>Set `JAVA_HOME` to the location where your Java JDK is installed

### Build system is [Maven](https://maven.apache.org/download.cgi)
- [Maven Plugin](https://docs.spring.io/spring-boot/maven-plugin/goals.html)
- A local [Maven wrapper](https://maven.apache.org/tools/wrapper/) is included: `./mvnw`.
```bash
# test
./mvnw test
# generate a .jar
./mvnw clean package
# skip tests
./mvnw clean package -DskipTests
# run
./mvnw spring-boot:run
# override default dev,local profiles
./mvnw spring-boot:run -Dapp.profiles=test
```
### Manually build container image with Docker
```
docker build -t chemlab .
docker run --rm -p 8080:8080 chemlab
```
### Configure a local `MongoDB` instance 
Download, install [MongoDB](https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-os-x/).
- MongoDB w/ Docker:
```sh
docker pull mongo:7.0.40-jammy
docker run --name mongodb -p 27017:27017 -d mongo:7.0.40-jammy
```
### Testing
Repository layer tests leverage MongoDB [Testcontainers](https://testcontainers.com/). A container runtime will need to be present before running repository tests.
### Secrets kept using `sops` and `age`
- download/install
  - [sops](https://github.com/getsops/sops?tab=readme-ov-file#encrypting-using-age)
  - [age](https://github.com/FiloSottile/age?tab=readme-ov-file#installation)
#### decrypt prod secrets
```
$ sops -d src/main/resources/application-prod.enc.yml > src/main/resources/application-prod.yml
```
#### add recipient
1. generate an age key pair: `age-keygen -o key.txt`
2. add the key pair to [a location where sops will find it](https://getsops.io/docs/usage/identities/age/)
3. add the `age` public key to `.sops.yaml`
4. run `updatekeys`:
```
sops updatekeys src/main/resources/application-prod.enc.yml
```
#### encrypt prod secrets after changes to `application-prod.yml`
```
sops encrypt src/main/resources/application-prod.yml > src/main/resources/application-prod.enc.yml
```
