# Interactive Periodic Table Web Application

<img src="/src/main/resources/static/images/chemistry.png" width="auto" height="95"  alt="chem-icon"/>

## Features
- Interactive periodic table of elements.
- Experiment with chemistry.
- Create an account:
  - Create flash cards.
  - Test your knowledge with a quiz automatically generated from your discovered compounds.

## Development
### Configure Java SDK 21 LTS
- [sdkman](https://sdkman.io/install/)
- [temurin](https://adoptium.net/temurin/releases/?version=21&os=any&arch=any)

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
### Configure a local instance of `MongoDB`
- download/install
    - [MongoDB](https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-os-x/)
### Testing
Repository layer tests leverage MongoDB [Testcontainers](https://testcontainers.com/). A container runtime will need to be present before running repository tests.
### Secrets kept using `sops` and `age`
- download/install
  - [sops](https://github.com/getsops/sops?tab=readme-ov-file#encrypting-using-age)
  - [age](https://github.com/FiloSottile/age?tab=readme-ov-file#installation)
#### decrypt secrets
```
$ sops -d src/main/resources/application.enc.yml > src/main/resources/application.yml
```
#### add recipient
1. generate an age key pair: `age-keygen -o key.txt`
2. add the key pair to [a location where sops will find it](https://github.com/getsops/sops?tab=readme-ov-file#encrypting-using-age)
3. add the `age` public key to `.sops.yaml`
4. run `updatekeys`:
```
sops updatekeys src/main/resources/application.enc.yml
```
#### encrypt secrets after changes to `application.yml`
```
sops encrypt src/main/resources/application.yml > src/main/resources/application.enc.yml
```
## Frontend built with [Angular 20](https://angular.dev/)
- https://github.com/calebd-anderson/open-chemistry-lab-frontend

## Credits
This project was built with help from:
- My B.S. capstone project professor and peers at Metro State University
- An online tutorial from [Get Arrays](https://www.getarrays.io/)
- The [PubChem API](https://pubchem.ncbi.nlm.nih.gov/docs/pug-rest-tutorial)
  - public chemistry data service
- [RoboHash](https://robohash.org/)
  - temporary profile image generator
- Package organization is inspired by [onion architecture](https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/)
  - yet not true onion architecture in terms of the actual dependency graph
- <a href="https://www.flaticon.com/free-icons/chemistry" title="chemistry icons">Some chemistry icons created by Freepik - Flaticon</a>
