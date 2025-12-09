import groovy.json.JsonOutput
import groovy.json.JsonSlurper

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.callstack.react.brownfield")
    `maven-publish`
    id("com.facebook.react")
}

react {
    autolinkLibrariesWithApp()
}

repositories {
    mavenCentral()
}

android {
    namespace = "com.rnprojectreact"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        buildConfigField("boolean", "IS_EDGE_TO_EDGE_ENABLED", properties["edgeToEdgeEnabled"].toString())
        buildConfigField("boolean", "IS_NEW_ARCHITECTURE_ENABLED", properties["newArchEnabled"].toString())
        buildConfigField("boolean", "IS_HERMES_ENABLED", properties["hermesEnabled"].toString())
    }

    publishing {
        multipleVariants {
            allVariants()
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenAar") {
            groupId = "com.rnprojectreact"
            artifactId = "rnproject"
            version = "0.0.1-local"
            afterEvaluate {
                from(components.getByName("default"))
            }

            pom {
                withXml {
                    /**
                     * As a result of `from(components.getByName("default")` all of the project
                     * dependencies are added to `pom.xml` file. We do not need the react-native
                     * third party dependencies to be a part of it as we embed those dependencies.
                     */
                    val dependenciesNode = (asNode().get("dependencies") as groovy.util.NodeList).first() as groovy.util.Node
                    dependenciesNode.children()
                        .filterIsInstance<groovy.util.Node>()
                        .filter { (it.get("groupId") as groovy.util.NodeList).text() == rootProject.name }
                        .forEach { dependenciesNode.remove(it) }
                }
            }
        }
    }

    repositories {
        mavenLocal() // Publishes to the local Maven repository (~/.m2/repository)
    }
}

dependencies {
    api("com.facebook.react:react-android:0.81.1")
    api("com.facebook.react:hermes-android:0.81.1")
}

val moduleBuildDir: Directory = layout.buildDirectory.get()

/**
 * As a result of `from(components.getByName("default")` all of the project
 * dependencies are added to `module.json` file. We do not need the react-native
 * third party dependencies to be a part of it as we embed those dependencies.
 */
tasks.register("removeDependenciesFromModuleFile") {
    doLast {
        file("$moduleBuildDir/publications/mavenAar/module.json").run {
            val json = inputStream().use { JsonSlurper().parse(it) as Map<String, Any> }
            (json["variants"] as? List<MutableMap<String, Any>>)?.forEach { variant ->
                (variant["dependencies"] as? MutableList<Map<String, Any>>)?.removeAll { it["group"] == rootProject.name }
            }
            writer().use { it.write(JsonOutput.prettyPrint(JsonOutput.toJson(json))) }
        }
    }
}

tasks.named("generateMetadataFileForMavenAarPublication") {
   finalizedBy("removeDependenciesFromModuleFile")
}

/**
 * Fix task dependency issues where library tasks try to use
 * output from app's createBundleReleaseJsAndAssets task
 */
afterEvaluate {
    val appBundleTask = project.parent?.project(":app")?.tasks?.findByName("createBundleReleaseJsAndAssets")
    if (appBundleTask != null) {
        tasks.matching {
            it.name.startsWith("generateDebugResources") ||
            it.name.startsWith("extractDeepLinksForAar") ||
            it.name.startsWith("generate") ||
            it.name.startsWith("process")
        }.configureEach {
            mustRunAfter(appBundleTask)
        }
    }
}