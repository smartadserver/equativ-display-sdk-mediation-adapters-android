# Equativ Mediation Adapters Android

This repository centralizes all _Android Display SDK8_ mediation adapters, serving ads from third party SDKs through Equativ SDK acting as primary integration.

## Automatic Gradle installation

You can install the __Equativ Display SDK__, one or several mediation adapters and their related third party SDKs using _Gradle_.

First declare the _Equativ_ repository in the main _build.gradle_ file of your project. Also add any other _Maven_ repository that might be required by a third party SDK (see table below).

    allprojects {
      repositories {
        google()
        jcenter()

        // add the Equativ repository
        maven { url 'https://packagecloud.io/smartadserver/android/maven2' }

        // add other third party repositories if necessary (see table below)
        // …
      }
    }

Then in the _build.gradle_ of to your application module, you can now import the adapters you need. Any dependency will be automatically fetched (_Equativ Display SDK_, third party SDK, …).

For instance you can import _Google_ and _Ogury_ as follows:

    implementation('com.equativ.android.mediation:equativ-display-sdk-with-google:8.5.0.0')
    implementation('com.equativ.android.mediation:equativ-display-sdk-with-ogury:8.5.0.0')

> **Note on version numbers:**
>
> The latest version is: **8.5.0.0**
>
> If you import several third party adapters using _Gradle_, you must use the **same version number for all of them**.
>
> The version number always correspond to the underlying _Equativ Display SDK_ for its first three digits, then a technical version corresponding to the adapters.
> For instance, 8.5.0.0 would import the first technical release of the adapters with the _Equativ Display SDK_ 8.5.0.

Available adapters are:

| Package name | Supported SDK version | Comments | Maven repository |
| ------------ | --------------------- | -------- | ---------------- |
| ```equativ-display-sdk-with-googlemobileads``` | 23.5.0 | _n/a_ | _n/a_ |
| ```equativ-display-sdk-with-ogury``` | 5.6.2 | _n/a_ | ```maven {url 'https://maven.ogury.co'}``` |


## Manual installation

You can still install the adapters manually if needed:

1. First make sure you have installed the __Equativ Display SDK__. More information [here](http://documentation.smartadserver.com/DisplaySDK8/android/gettingstarted.html).

2. Copy and paste the classes of the adapter(s) you need to your project sources. Note that some adapter classes have a base class, do not forget to copy it as well. __Beware__ of the fact that the adapter classes are located in a folder structure consistent with their declared package names __and__ the class name sent in the mediation ad sent by __Equativ__ delivery. For the whole mediation flow to work properly, you __must__ leave this folder structure untouched when copying it in your project. Typically, you should copy the com/ root folder containing the classes directly in one of the source folders of your Android project, for instance src/main/Java. If that com/ folder already exists, simply merge it with the one containing the adapters. Failing to do so will prevent the SDK from properly instantiating the adapters when it receives a mediation ad, and the ad call will then fail.

3. Make sure to integrate the SDK corresponding to the chosen adapter(s).

4. If you are building your application with the ```minifiedEnable true``` option, which usually obfuscates classnames, you __must__ add a proguard rule (or equivalent) to your build pipeline to ensure that the adapter classes you imported remain __untouched__. Indeed, they are instantiated via reflection by the __Equativ Display SDK__ and obfuscating them would prevent them from being used when mediation ads are fetched.
Please refer to each dedicated README.me per SDK folder to get the proguard rule to add to your build configuration.
