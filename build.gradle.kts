plugins {
    java
    application
}

group = "eu.giulianogorgone"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("eu.giulianogorgone:fluidswipe-core:1.0.0")
}

application {
    mainClass.set("eu.giulianogorgone.fluidswipe.samples.swipeabletabpane.SwipeableTabbedPaneSample")
}
