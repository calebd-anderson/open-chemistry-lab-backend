# Interactive Chemistry Web Application

<img src="/src/main/resources/static/images/chemistry.png" width="auto" height="95"  alt="chem-icon"/>

<a href="https://www.flaticon.com/free-icons/chemistry" title="chemistry icons">Chemistry icons created by Freepik - Flaticon</a>
## Features
- Interactive periodic table of elements.
- Elemental experimentation.
- Real time chemical reaction.
- Create flash cards.
- Test your knowledge with a quiz.
## Angular Front End
- https://github.com/calebTree/ics499-chem-frontend

## Development
### Build system is [Maven](https://maven.apache.org/download.cgi)
A local maven wrapper is included: `./mvnw`.
```
  $ mvn clean install
  $ mvn test
  $ mvn spring-boot:run
```
### Configure Java SDK 17
- [sdkman](https://sdkman.io/install/)
    - `sdk install java 17.0.0-tem`
### Configure Local Instance of `MongoDB`
- download/install
    - [MongoDB](https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-os-x/)
### Secrets kept using `sops` and `age`
- download/install
  - [sops](https://github.com/getsops/sops?tab=readme-ov-file#22encrypting-using-age)
  - [age](https://github.com/FiloSottile/age)
#### decrypt secrets
```
$ sops -d application.enc.yml > application.yml
```
#### add recipient 
1. add `age` public key to `.sops.yaml`
2. run `updatekeys`
```
sops updatekeys src/main/resources/application.enc.yml
```
#### encrypt secrets
```
sops encrypt src/main/resources/application.yml > src/main/resources/application.enc.yml
```
## Credits
This project was built with help from:
- my peers and instructor at Metro State
- an online tutorial from [Get Arrays](https://www.getarrays.io/)
- [PubChem API](https://pubchem.ncbi.nlm.nih.gov/docs/pug-rest-tutorial)
