import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.MavenPublishBaseExtension

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.mavenPublish)
}

kotlin {
  ios()
  iosSimulatorArm64()

  sourceSets {
    all {
      languageSettings {
        optIn("androidx.paging.ExperimentalPagingApi")
      }
    }
    val commonMain by getting {
      dependencies {
        api(projects.pagingCommon)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.stately.concurrency)
        implementation(libs.stately.iso.collections)
      }
    }
    val iosMain by getting {
      kotlin.srcDir("../upstreams/androidx-main/paging/paging-runtime/src/commonMain/kotlin")
    }
    val iosSimulatorArm64Main by getting {
      dependsOn(iosMain)
    }
  }
}

configure<MavenPublishBaseExtension> {
  configure(
    KotlinMultiplatform(javadocJar = JavadocJar.Empty()),
  )
}
