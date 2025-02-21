import com.diffplug.gradle.spotless.SpotlessExtension
import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsExtension
import com.github.spotbugs.snom.SpotBugsTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

logger.quiet("Java version: ${JavaVersion.current()}")
logger.quiet("Gradle version: ${gradle.gradleVersion}")

plugins {
  id("java-library")
  id("com.diffplug.gradle.spotless") version "7.0.2" apply (false)
  id("com.github.spotbugs") version "6.1.5" apply (false)
  id("com.asarkar.gradle.build-time-tracker") version "4.3.0"
}

allprojects {
  group = "com.willmolloy"
  repositories {
    mavenCentral()
  }
}

subprojects {
  apply(plugin = "java")
  configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }

  apply(plugin = "com.diffplug.spotless")
  configure<SpotlessExtension> {
    java {
      removeUnusedImports()
      googleJavaFormat()
    }
  }

  apply(plugin = "checkstyle")
  configure<CheckstyleExtension> {
    toolVersion = "10.12.0"
    configFile = rootProject.file("./checkstyle.xml")
    maxErrors = 0
    maxWarnings = 0
    isIgnoreFailures = false
  }

  apply(plugin = "com.github.spotbugs")
  configure<SpotBugsExtension> {
    effort.set(Effort.MAX)
    reportLevel.set(Confidence.LOW)
    ignoreFailures.set(false)
    excludeFilter.set(rootProject.file("./spotbugs-exclude.xml"))
  }
  tasks.withType<SpotBugsTask> {
    reports.create("html").required.set(true)
  }

  tasks.withType<Test> {
    maxParallelForks = Runtime.getRuntime().availableProcessors()
    useJUnitPlatform()
    testLogging {
      events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED)
      exceptionFormat = TestExceptionFormat.FULL
      showExceptions = true
      showCauses = true
      showStackTraces = true
      afterSuite(KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
        if (desc.parent == null) {
          println(
            "Results: ${result.resultType} " +
                "(${result.testCount} test${if (result.testCount > 1) "s" else ""}, " +
                "${result.successfulTestCount} passed, " +
                "${result.failedTestCount} failed, " +
                "${result.skippedTestCount} skipped)"
          )
        }
      }))
    }
    finalizedBy(tasks.withType<JacocoReport>())
  }

  apply(plugin = "jacoco")
  tasks.withType<JacocoReport> {
    reports {
      xml.required.set(true)
    }
  }

  dependencies {
    val log4jVersion = "2.24.3"
    val guavaVersion = "33.4.0-jre"
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("com.github.spotbugs:spotbugs-annotations:4.9.1")
    implementation("com.google.guava:guava:$guavaVersion")

    val junitVersion = "5.12.0"
    val truthVersion = "1.4.4"
    val mockitoVersion = "5.15.2"
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("com.google.truth:truth:$truthVersion")
    testImplementation("com.google.truth.extensions:truth-java8-extension:$truthVersion")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
    testImplementation("com.google.jimfs:jimfs:1.3.0")

    configurations.all {
      exclude("org.assertj")
      exclude("junit")
      resolutionStrategy {
        force("com.google.guava:guava:$guavaVersion") // exclude android version
      }
    }
  }
}