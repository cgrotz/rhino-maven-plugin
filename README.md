**rhino-maven-plugin** is a Maven plugin to compile Javascript to Java class file using Mozilla Rhino.
The compiled classes require Mozilla Rhino to run

Maven Distribution
==================
```xml
	<build>
	  <plugins>
	    <plugin>
	      <groupId>de.skiptag</groupId>
	      <artifactId>rhino-maven-plugin</artifactId>
	      <version>1.0.0</version>
	    </plugin>
	  </plugins>
	</build>
```
You can start the compilation be simply calling
```
mvn rhino:compile
```
or by adding the following to the plugin tag:
```xml
	<executions>
	  <execution>
	    <phase>compile</phase>
	    <goals>
	      <goal>compile</goal>
	    </goals>
	  </execution>
	</executions>
```
Mozilla Rhino as dependency
```xml
	<dependency>
	  <groupId>org.mozilla</groupId>
  	  <artifactId>rhino</artifactId>
	  <version>1.7R4</version>
	</dependency>
```
The artifact is deployed to Sonatype central and will be deployed on Maven central asap.
```xml
	<repositories>
	  <repository>
	    <id>sonatype-oss-public</id>
	    <url>https://oss.sonatype.org/content/groups/public/</url>
	    <releases>
	      <enabled>true</enabled>
	    </releases>
	    <snapshots>
	      <enabled>true</enabled>
	    </snapshots>
	  </repository>
	</repositories>
```



