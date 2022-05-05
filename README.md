# Interactive Chemistry Web Application

<img src="/src/main/resources/static/images/chemistry.png" width="auto" height="95" />

<a href="https://www.flaticon.com/free-icons/chemistry" title="chemistry icons">Chemistry icons created by Freepik - Flaticon</a>

### Development Docker email server
```
docker build -t fedora:mail bin/smtp-container/
docker run --name mailserver -e APP_DOMAIN=chemlab.edu --rm -it -p 80:80 -p 25:25 -p 143:143 -p 110:110 fedora:mail
```

### Use
```
  $ mvn test
  $ mvn clean install
  $ mvn spring-boot:run
```
### Features
- Interactive periodic table of elements.
- Elemental experimentation.
- Real time chemical reaction.
- Create flash cards.
- Test your knowledge with a quiz.

### Angular Front End
- https://github.com/calebTree/ics499-chem-frontend
