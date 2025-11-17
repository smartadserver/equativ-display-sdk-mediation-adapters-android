# Equativ Mediation Adapters Android - Google Mobile Ads

## Build instructions

If you are building your application with the ```minifiedEnable true``` option, which usually obfuscates classnames, you __must__ add the following proguard rules (or equivalent) to your build pipeline to ensure that the adapter classes you imported remain __untouched__. Indeed, they are instantiated via reflection by the __Equativ Display SDK__ and obfuscating them would prevent them from being used when mediation ads are fetched.

```
-keep class com.equativ.displaysdk.mediation.google.SASGMABannerAdapter { public *; }
-keep class com.equativ.displaysdk.mediation.google.SASGMAInterstitialAdapter { public *; }
```

## Known issues

Google InterstitialAd API requires an Activity to be able to show the loaded interstitial ad. Therefore, the Equativ __SASInterstitialManager instance must be created with an Activity instance as "context" parameter in the application.__ Passing a non Activity context (typically, the ApplicationContext) will make the SASGMAInterstitialAdapter fail when requesting a Google mediated interstitial ad.


