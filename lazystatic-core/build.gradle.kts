/*
 * Copyright 2025 Karma Krafts
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import dev.karmakrafts.conventions.configureJava
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.introspekt)
}

configureJava(libs.versions.java)

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.uuid.ExperimentalUuidApi")
    }
    mingwX64()
    linuxX64()
    linuxArm64()
    macosX64()
    macosArm64()
    androidLibrary {
        namespace = "$group.${rootProject.name}"
        compileSdk = libs.versions.androidCompileSDK.get().toInt()
        minSdk = libs.versions.androidMinimalSDK.get().toInt()
    }
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX64()
    androidNativeX86()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()
    watchosArm32()
    watchosArm64()
    watchosX64()
    watchosSimulatorArm64()
    jvm()
    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        nodejs {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
    }
    @OptIn(ExperimentalWasmDsl::class) wasmJs {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        nodejs {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
    }
    applyDefaultHierarchyTemplate()
    withSourcesJar(true)
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.stately.common)
                implementation(libs.stately.concurrent.collections)
                api(libs.introspekt.runtime)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}