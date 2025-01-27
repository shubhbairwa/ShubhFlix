// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

//for dagger-Hilt
    id("com.google.dagger.hilt.android") version "2.51.1" apply false


}

buildscript {
    dependencies {

        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
        
        //for Dagger Hilt need to add this
        classpath(libs.kotlin.gradle.plugin)
    }
}