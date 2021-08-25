MacMemo
=======
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.softwaremill.macmemo/macros_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.softwaremill.macmemo/macros_2.12)  
[![Build Status](https://travis-ci.org/kciesielski/macmemo.svg?branch=master)](https://travis-ci.org/kciesielski/macmemo)

MacMemo is a simple library introducing `@memoize` macro annotation for simple function memoization. 
Annotated functions are wrapped with boilerplate code which uses **Guava CacheBuilder** 
(or any other cache implementation - see 'Custom memo cache builders' section) to save 
returned values for given argument list. Memoization is scoped for particular class instance.    

**Available for Scala 2.11 and 2.12**

Example usage:  
````scala
import com.softwaremill.macmemo.memoize
import scala.concurrent.duration._

class GraphBuilder {

  @memoize(maxSize = 20000, expiresAfter = 2 hours)
  def creatGraph(elementCount: Int): Graph = {
    someExpensiveCode()
  }

}
````

Parameters (for more details see [Guava docs](http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/cache/CacheBuilder.html)):
* maxSize: Specifies the maximum number of entries the cache may contain.
* expiresAfter: Specifies that each entry should be automatically removed from the cache once a fixed duration has elapsed after the entry's creation, or the most recent replacement of its value.
* concurrencyLevel: Guides the allowed concurrency among update operations.

Installation, using with SBT
----------------------------

The jars are deployed to [Sonatype's OSS repository](https://oss.sonatype.org/content/repositories/releases/com/softwaremill/macmemo/).
To use MacMemo in your project, add a dependency:

````scala
libraryDependencies += "com.softwaremill.macmemo" %% "macros" % "0.4"
````

You also need to add a special compiler plugin to your `buildSettings`:

````scala
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
````

Testability
---------
In order to disable MacMemo for tests, add following test options to your `buildSettings`:
````scala
testOptions in Test += Tests.Setup(() => System.setProperty("macmemo.disable", "true"))
````
Or you can set vm argument by ```-Dmacmemo.disable=true```, Or set environment variable like this:
````shell
export macmemo_disable=true
````

Debugging
---------

The print debugging information on what MacMemo does when generating code, set the
`macmemo.debug` system property. E.g. with SBT, just add a `System.setProperty("macmemo.debug", "")` line to your
build file.

Custom memo cache builders
---------

One may want to use more sophisticated cache provider, than simple Guava Cache. 
It's possible to leverage any of existing cache providers like memcached or even NoSQL databases like Redis, 
by bringing appropriate implicit `com.softwaremill.macmemo.MemoCacheBuilder` instance into 
**memoized class scope** (the scope of the method definition, not it's usage, e.g: companion object, implicit val within class 
or an explicit imports available in scope of class definition)
 
See `MemoCacheBuilder.guavaMemoCacheBuilder` in `macmemo/macros/src/main/scala/com/softwaremill/macmemo/MemoCacheBuilder.scala`, 
`macros/src/test/scala/com/softwaremill/macmemo/TestMemoCacheBuilder.scala` and  
`macros/src/test/scala/com/softwaremill/macmemo/examples/MemoCacheBuilderResolutionSpec.scala` 
for custom builder implementation and usage examples.

Whenever custom memo builder cannot be found in class definition scope, appropriate compiler info message will be emitted:
```
[info] /path/to/sources/SomeFancyNamedFile.scala:42: Cannot find custom memo builder for method 'someMethodName' - default builder will be used
[info]   @memoize(maxSize = 2, 15 days)
[info]    ^
```
Currently there is no way to turn them off.

Installation, using with maven
----------------------------
pom.xml file :

add jitpack repository:
````xml
  <repositories>
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
    </repository>
  </repositories>
````

properties:
````xml
<properties>
        <scala.version>2.12</scala.version>
        <scala.compiler>2.12.10</scala.compiler>
</properties>
````
add dependency:
````xml
<dependency>
  <groupId>com.github.oldbig</groupId>
  <artifactId>macmemo</artifactId>
  <version>0.6-OBP-SNAPSHOT</version>
</dependency>
````

add paradise plugin:
````xml
<plugin>
  <groupId>net.alchim31.maven</groupId>
  <artifactId>scala-maven-plugin</artifactId>
  <version>4.3.1</version>
  <configuration>
    <compilerPlugins>
      <!--scala 2.12.x need the paradise plugin to support macros annotation, 2.13.x value not need this plugin,
      just add -Ymacro-annotations flag -->
      <compilerPlugin>
        <groupId>org.scalamacros</groupId>
        <artifactId>paradise_${scala.compiler}</artifactId>
        <version>2.1.1</version>
      </compilerPlugin>
    </compilerPlugins>
  </configuration>
</plugin>
````
OBPMemoize annotation usage:
----------------------------
the same as memoize annotation, except the parameter order, and add implicit variable of type MemoCacheBuilder:
````scala
import com.softwaremill.macmemo.memoize
import scala.concurrent.duration._

class GraphBuilder {
  implicit val builder: MemoCacheBuilder = ??? // supply your implementation
  val ttl = 2 hours
  
  @OBPMemoize(ttl)
  def creatGraph(elementCount: Int): Graph = {
    someExpensiveCode()
  }

}
````

Credits
-------

Special thanks to [Adam Warski](http://www.warski.org/blog/) for his clever [MacWire](https://github.com/adamw/macwire) library and all other inspirations :)
