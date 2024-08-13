# Equativ Mediation Adapters Android - Google Mobile Ads

## Build instructions

If you are building your application with the ```minifiedEnable true``` option, which usually obfuscates classnames, you __must__ add the following proguard rules (or equivalent) to your build pipeline to ensure that the adapter classes you imported remain __untouched__. Indeed, they are instantiated via reflection by the __Equativ Display SDK__ and obfuscating them would prevent them from being used when mediation ads are fetched.

```
-keep class com.equativ.displaysdk.mediation.google.SASGMABannerAdapter { public *; }
-keep class com.equativ.displaysdk.mediation.google.SASGMAInterstitialAdapter { public *; }
```

## Known issues


