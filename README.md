# Superhero RESTful Webservice

Method | URL | Required Parameters | Optional Parameters | Example | Description
--- | --- | --- | --- | --- | ---
GET | /superheroes | - | - | /superheroes | Get a list of all superheroes
GET | /superheroes | page | size, sort | /superheroes?page=3&size=10&sort=firstAppearance,desc | Get a paged list of superheroes
GET | /superheroes | sort | - | /superheroes?sort=publisher,desc&sort=pseudonym,asc | Get a sorted list of all superheroes
GET | /superheroes/{id} | id | - | /superheroes/1234 | Get a superhero by id
GET | /superheroes | pseudonym | - | /superheroes?pseudonym=Batman | Get a superhero by pseudonym
POST | /superheroes | - | - | /superheroes <details>
                                             <summary>Body</summary>
                                             
                                             ```json
                                             {
                                               "name": "Clark Kent",
                                               "pseudonym": "Superman",
                                               "publisher": "DC Comics",
                                               "skills": [
                                                 "Superhuman strength, speed, and durability",
                                                 "Flight",
                                                 "Heat vision",
                                                 "Freezing breath",
                                                 "X-ray vision",
                                                 "Telescopic & microscopic vision"
                                               ],
                                               "allies": [
                                                 "Supergirl",
                                                 "Superboy",
                                                 "Superdog",
                                                 "Power Girl"
                                               ],
                                               "firstAppearance": "1938-04-18"
                                             }
                                             ```
                                             
                                           </details> | Create a superhero 

## Run

```
mvn spring-boot:run
```

## Build a docker image

```
mvn install dockerfile:build
```

## Push the image

```
mvn dockerfile:push
```

## Push upon mvn install
Add the following into the dockerfile-maven-plugin configuration:

```xml
<executions>
	<execution>
		<id>default</id>
		<phase>install</phase>
		<goals>
			<goal>build</goal>
			<goal>push</goal>
		</goals>
	</execution>
</executions>
```

## Run a container

```
docker container run -p 80:8080 -t pzinsta/superhero-webservice
```