# lazyStatic

[![](https://git.karmakrafts.dev/kk/lazystatic/badges/master/pipeline.svg)](https://git.karmakrafts.dev/kk/lazystatic/-/pipelines)
[![](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.maven.apache.org%2Fmaven2%2Fdev%2Fkarmakrafts%2Flazystatic%2Flazystatic-core%2Fmaven-metadata.xml
)](https://git.karmakrafts.dev/kk/lazystatic/-/packages)
[![](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fdev%2Fkarmakrafts%2Flazystatic%2Flazystatic-core%2Fmaven-metadata.xml
)](https://git.karmakrafts.dev/kk/lazystatic/-/packages)

lazyStatic brings C++-like static locals to Kotlin Multiplatform.

### How to use it

First, add the official Maven Central repository to your `settings.gradle.kts`:

```kotlin
pluginManagement {
    repositories {
        maven("https://central.sonatype.com/repository/maven-snapshots")
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://central.sonatype.com/repository/maven-snapshots")
        mavenCentral()
    }
}
```

Then add a dependency on the library and the [Introspekt](https://git.karmakrafts.dev/kk/introspekt) Gradle plugin in
your root buildscript:

```kotlin
plugins {
    id("dev.karmakrafts.introspekt.introspekt-gradle-plugin") version "<version>"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("dev.karmakrafts.lazystatic:lazystatic-core:<version>")
            }
        }
    }
}
```

### Usage example

```kotlin
import dev.karmakrafts.lazystatic.lazyStatic
import dev.karmakrafts.lazystatic.LazyStaticStorage
import dev.karmakrafts.introspekt.util.SourceLocation

fun myFunction() {
    // myValue will be the same object for every call of myFunction
    val myValue = lazyStatic { heavyComputation() }

    // Every calling thread of myFunction will have its own myTlValue
    val myTlValue = lazyStatic(storage = LazyStaticStorage.THREAD_LOCAL) {
        heavyComputationWithSeed(Uuid.random())
    }
}

fun myOtherFunction(location: SourceLocation = SourceLocation.here()) {
    // The location passed in through the caller is used as a key
    val myValue = lazyStatic(location) { heavyComputation2() }
}
```