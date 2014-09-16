MacMemo
=======

MacMemo is a simple library introducing `@memoize` macro annotation for simple function memoization. 
Annotated functions are wrapped with boilerplate code which uses **Guava CacheBuilder** to save 
returned values for given argument list. Memoization is scoped for particular class instance.  

Future versions may allow defining custom cache providers.

**MacMemo requires Scala 2.11**

Example usage:  
````scala
import com.softwaremill.macmemo.memoize

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
libraryDependencies += "com.softwaremill.macmemo" %% "macros" % "0.2"
````

You also need to add a special compiler plugin to your `buildSettings`:

````scala
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M1" cross CrossVersion.full)
````

To use the snapshot version:

````scala
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "com.softwaremill.macmemo" %% "macros" % "0.3-SNAPSHOT"
````

Testability (version 0.3+)
---------
In order to disable MacMemo for tests, add following test options to your `buildSettings`:
````scala
testOptions in Test += Tests.Setup(() => System.setProperty("macmemo.disable", "true"))
````

Debugging
---------

The print debugging information on what MacMemo does when generating code, set the
`macmemo.debug` system property. E.g. with SBT, just add a `System.setProperty("macmemo.debug", "")` line to your
build file.

Credits
-------

Special thanks to [Adam Warski](http://www.warski.org/blog/) for his clever [MacWire](https://github.com/adamw/macwire) library and all other inspirations :)
