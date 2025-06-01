# lazyStatic

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