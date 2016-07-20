# RealTimeLocationServices
Real Time Location Services, read a JSON file from DropBox with Volley.

Create  your own JSON file and use Volley to access the file.

Gradle:

              /**
               *
               * this is necessary to run Butterknife
               * Start:
               *
               */
              
              buildscript {
                  repositories {
                      mavenCentral()
                  }
                  dependencies {
                      classpath 'com.android.tools.build:gradle:2.0.0'
                      classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
                  }
              }
              
              apply plugin: 'com.neenbedankt.android-apt'
              
              /**
               *
               * this is necessary to run Butterknife
               * End.
               *
               */
               
                  dependencies {
                  compile fileTree(dir: 'libs', include: ['*.jar'])
                  testCompile 'junit:junit:4.12'
                  compile files('libs/volley.jar')
                  //**** For Butterknife
                  apt 'com.jakewharton:butterknife-compiler:8.1.0'
                  compile 'com.jakewharton:butterknife:8.1.0'
                  compile 'com.android.support:cardview-v7:23.2.1'
                  compile 'com.android.support:appcompat-v7:23.3.0'
                  compile 'com.android.support:support-v4:23.3.0'
                  compile 'com.android.support:design:23.3.0'
                  compile 'com.google.android.gms:play-services:7.5.0'
                  compile 'com.google.maps.android:android-maps-utils:0.4'
                  compile 'com.google.code.gson:gson:2.4'
                  compile 'cn.pedant.sweetalert:library:1.3'
              }
              
![tracking1](https://cloud.githubusercontent.com/assets/1615724/16979634/617fe8b6-4e62-11e6-80ff-89c32731ca71.png)
