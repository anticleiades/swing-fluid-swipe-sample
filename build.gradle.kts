plugins {
    java
    application
}

group = "eu.giulianogorgone"
version = "1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("eu.giulianogorgone:fluidswipe-core:1.1.0")
}

application {
    mainClass.set("eu.giulianogorgone.fluidswipe.samples.swipeabletabpane.SwipeableTabbedPaneSample")
}
