# Interactive Chemistry Web Application

<img src="/src/main/resources/static/images/chemistry.png" width="auto" height="95" />

<a href="https://www.flaticon.com/free-icons/chemistry" title="chemistry icons">Chemistry icons created by Freepik - Flaticon</a>
## Features
- Interactive periodic table of elements.
- Elemental experimentation.
- Real time chemical reaction.
- Create flash cards.
- Test your knowledge with a quiz.
## Angular Front End
- https://github.com/calebTree/ics499-chem-frontend

# Develop
## decrypt `application.enc.yml`
- download/install
  - [sops](https://github.com/getsops/sops?tab=readme-ov-file#22encrypting-using-age)
  - [age](https://github.com/FiloSottile/age)
### decrypt secrets
```
$ sops -d application.enc.yml > application.yml
```
### add recipient 
1. add `age` public key to `.sops.yaml`
2. run `updatekeys`
```
sops updatekeys src/main/resources/application.enc.yml
```
### encrypt secrets
```
sops encrypt --age <age public key> src/main/resources/application.yml
```
## Configure Local Instance of `MongoDB`
- download/install
  - [MongoDB](https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-os-x/)
## Configure Java SDK 17
 - [sdkman](https://sdkman.io/install/)
   - `sdk install java 17.0.0-tem`
## Get [Maven](https://maven.apache.org/download.cgi)
```
  $ mvn clean install
  $ mvn test
  $ mvn spring-boot:run
```
