# ServiceProvider
Final project for MAD subject. This project develop a mobile application for Community User and Service Provider.

# Introduction
This mobile application act as a platform for community user to search for the service nearby. This mobile application also is a platform of directory for service which store the information of services provided by Service Provider.

This project is developed using Android Studio which using JAVA programming language. This project integrate with the Google Maps API as mapping and Google Firebase API as storage.

# Module / Functionalities of the mobile apps.
Community User
-	Login account as community user.
-	Search service by type-in and category option.
-	View service provided by service provider.
-	View information of service.
-	Rate service
-	Book service
-	Message service provider


Service Provider
-	Login account as Service Provider.
-	Add service that their own organization provide.
-	Update service information.
-	Message community user that booked their service.
-	Update booking status.


# Library/external API used
- Google Maps API.
- Google Firebase API.
- Google Cloud Messaging.
- HelloCharts Library
- SlidingUp Panel Library

# Setup the development enviroment
First install the JAVA SDK and JRE enviroment.

#### Java JRE
https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html

#### Java SDK
https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

#### Install Android Studio
https://developer.android.com/studio/

#### Create a new project, add the coding and the library.
Add the following the library into the app gradle build file
All of this library required to implement into the mobile applicaiton. 
```
  implementation 'com.google.android.gms:play-services-maps:16.1.0'
  implementation 'com.google.android.gms:play-services-location:16.0.0'
  implementation 'com.google.firebase:firebase-core:16.0.8'
  implementation 'com.google.firebase:firebase-auth:16.2.1'
  implementation 'com.google.firebase:firebase-database:16.1.0'
  implementation 'com.google.firebase:firebase-iid:17.1.2'
  implementation 'com.google.firebase:firebase-messaging:17.6.0'
  implementation 'com.sothree.slidinguppanel:library:3.4.0'
  implementation 'com.firebaseui:firebase-ui-database:3.1.3'
  implementation 'com.github.lecho:hellocharts-library:1.5.8@aar'
```
