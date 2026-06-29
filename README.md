# PowerLib

[![Jenkins](https://img.shields.io/jenkins/build?jobUrl=https%3A%2F%2Fci.codemc.io%2Fjob%2FAlbeMiglio%2Fjob%2FPowerLib%2F&color=bright-green)](https://ci.codemc.io/job/AlbeMiglio/job/PowerLib/)
![GitHub](https://img.shields.io/github/license/AlbeMiglio/PowerLib?color=bright-green&label=License)
![GitHub All Releases](https://img.shields.io/github/downloads/AlbeMiglio/PowerLib/total?color=brightgreen&label=Downloads)
[![Discord](https://img.shields.io/discord/618742870035398684?logo=Join%20on%20Discord&label=Discord)](https://discord.gg/UMhsCZk)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=AlbeMiglio_PowerLib&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=AlbeMiglio_PowerLib)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=AlbeMiglio_PowerLib&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=AlbeMiglio_PowerLib)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=AlbeMiglio_PowerLib&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=AlbeMiglio_PowerLib)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=AlbeMiglio_PowerLib&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=AlbeMiglio_PowerLib)

PowerLib is a server-side user interface library for Minecraft servers.

Development builds are always available at: **https://ci.codemc.io/job/AlbeMiglio/job/PowerLib/**

## Importing with Maven/Gradle

- To hook this library into your project with Maven, you just need to add to your pom.xml the repositories and
  dependencies below:

```
	<repositories>
	    <repository>
	        <id>codemc-repo</id>
	        <url>https://repo.codemc.org/repository/maven-public/</url>
	    </repository>
	</repositories>

	<dependencies>
	    <dependency>
  		<groupId>it.mycraft</groupId>
  		<artifactId>powerlib-<YOUR-PLATFORM></artifactId>
  		<version>1.3.1-SNAPSHOT</version>
		<scope>provided</scope>
	    </dependency>
	</dependencies>
```

Replace the `<YOUR-PLATFORM>` string with the platform you're going to use PowerLib for.

- Bukkit/Spigot/Paper (& forks): `bukkit`
- BungeeCord: `bungee`
- VelocityPowered: `velocity`

In case of an all-in-one application, there is an experimental option
to include all platforms in just one dependency. Use it at your own
risk, and don't do that unless you know what you're doing!

- (Experimental) ALL: `all`

If you're using Gradle, just put the repositories and dependencies below into your `build.gradle`. As said before,
replace `<YOUR-PLATFORM>` with
your platform between the ones expressed above.

```
repositories {
    maven {
        url 'https://repo.codemc.org/repository/maven-public/'
    }
    mavenCentral()
}

dependencies {
    compileOnly 'it.mycraft:powerlib-<YOUR-PLATFORM>:1.3.1-SNAPSHOT'
}
```

## Shading PowerLib onto your plugin

Since `v1.2.5` shading is finally available! Here is the Maven configuration to correctly compile it into
your plugin and rename its packages:

```
	<repositories>
	    <repository>
	        <id>codemc-repo</id>
	        <url>https://repo.codemc.org/repository/maven-public/</url>
	    </repository>
	</repositories>

	<dependencies>
	    <dependency>
  		<groupId>it.mycraft</groupId>
  		<artifactId>powerlib-<YOUR-PLATFORM></artifactId>
  		<version>1.3.1-SNAPSHOT</version>
		<scope>compile</scope>
	    </dependency>
	</dependencies>
	
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.3.0</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <createDependencyReducedPom>false</createDependencyReducedPom>
                            <relocations>
                                <relocation>
                                    <pattern>it.mycraft.powerlib</pattern>
                                    <shadedPattern>(your.fantastic.package).libs.powerlib</shadedPattern>
                                </relocation>
                            </relocations>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

## Sponsors

[![YourKit](https://www.yourkit.com/images/yklogo.png)](https://www.yourkit.com/java/profiler/)

YourKit supports open source projects with innovative and intelligent tools for monitoring and profiling Java 
and .NET applications. Their flagship products include the award-winning full-featured [YourKit Java Profiler](https://www.yourkit.com/java/profiler/) 
and the [.NET Profiler](https://www.yourkit.com/dotnet-profiler/)!
