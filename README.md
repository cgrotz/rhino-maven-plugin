**rhino-maven-plugin** is a Maven plugin to compile Javascript to Java class file using Mozilla Rhino. The compiled classes require Mozilla Rhino to run. The plugin is licensed under the Mozilla Public License 2.0 due to MPL being the license for Rhino

Maven Distribution
==================
```xml
	<build>
	  <plugins>
	    <plugin>
	      <groupId>de.skiptag</groupId>
	      <artifactId>rhino-maven-plugin</artifactId>
	      <version>1.0.3</version>
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
The artifact is deployed to Maven Central so no repository configuration should be necessary.


Configuration
=============
You don't have to give any configuration at all. If you are happy with your javascript sources being in a directory /src/main/js
in your project, and the resulting classes being generated in the standard build directory of your project.

Please see below all the configuration possibilities:
<configuration>
	<!-- Location of the input files. Default Value is the /src/main/js folder in the project --> 
	<sourceDirectory>${basedir}/src/main/js</sourceDirectory>

	<!-- Location where the output files should be places. Default Value is the standard outputDirectory. -->
	<outputDirectory>${project.build.outputDirectory}</outputDirectory>
	
	<!-- Character Encoding to use -->
	<characterEncoding>UTF-8</characterEncoding>

	<!-- Specifies the language version to compile with. The string versionNumber
	 must be one of 100, 110, 120, 130, 140, 150, 160, or 170. See JavaScript
	 Language Versions(https://developer.mozilla.org/en/Rhino/Overview#
	 JavaScript_Language_Versions) for more information on language versions.
	 Default value is -1 (unspecified) -->
	<languageVersion>-1<languageVersion>

	<!-- Optimizes at level optLevel, which must be an integer between -1 and 9.
	 See Optimization (https://developer.mozilla.org/en/Rhino/Optimization)
	 for more details. If optLevel is greater than zero, debug may not be
	 specified. Default value is -1 (unspecified). -->
	<optLevel>-1</optLevel>

	<!-- Does not save the source in the class file. Functions and scripts
	 compiled this way cannot be decompiled. This option can be used to avoid
	 distributing source or simply to save space in the resulting class file.
	 Default value is false.-->
	<nosource>false</nosource>

	<!-- Specifies that debug information should be generated. May not be combined
	 with optimization at an optLevel greater than zero. Default value is true.-->
	<debug>true</debug>

	<!--
	  Not document what this parameter is doing. Default value is true.
	-->
	<observeInstructionCount>true<observeInstructionCount>

	<!-- Specify the class name used for main method implementation. The class
	 must have a method matching public static void main(Script sc, String[]
	 args). -->
	<mainMethodClass>com.company.some.MainClass<mainMethodClass>

	<!-- Specifies that a java class extending the Java class java-class-name
	 should be generated from the incoming JavaScript source file. Each global
	 function in the source file is made a method of the generated class,
	 overriding any methods in the base class by the same name. -->
	<superclass>com.company.some.Class</superclass>

	<!--
	 Specifies that a java class implementing the Java interface
	 java-intf-name should be generated from the incoming JavaScript source
	 file. Each global function in the source file is made a method of the
	 generated class, implementing any methods in the interface by the same
	 name.-->
	<interfaces>
		<interface>com.company.some.Interface</interface>
	</interfaces>
<configuration>
