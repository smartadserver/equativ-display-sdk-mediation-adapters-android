# Equativ Mediation Adapters Android - Google Mobile Ads

## Build instructions

If you are building your application with the ```minifiedEnable true``` option, which usually obfuscates classnames, you __must__ add the following proguard rules (or equivalent) to your build pipeline to ensure that the adapter classes you imported remain __untouched__. Indeed, they are instantiated via reflection by the __Equativ Display SDK__ and obfuscating them would prevent them from being used when mediation ads are fetched.

```
-keep class com.equativ.displaysdk.mediation.google.SASGMABannerAdapter { public *; }
-keep class com.equativ.displaysdk.mediation.google.SASGMAInterstitialAdapter { public *; }
```

## Client side parameters

The __Equativ Display SDK__ can forward a map of publisher-defined values to the mediation adapters at ad request time. The _Google Mobile Ads_ adapters use it to set the __content URL__ of the underlying ad request, which _Google_ relies on for contextual targeting and brand safety.

Set this map on the ```SASAdPlacement``` instance __before__ loading the ad, using the ```SASGMAUtil.REQUEST_CONTENT_URL_KEY``` key (whose literal value is ```gmaRequestContentURL```). The value must be a ```String```, any other type will be ignored:

```
val adPlacement = SASAdPlacement(siteId, pageId, formatId).apply {
    mediationClientSideParameters = mapOf(
        SASGMAUtil.REQUEST_CONTENT_URL_KEY to "https://www.mywebsite.com/the-currently-displayed-article"
    )
}

bannerView.loadAd(adPlacement)
```

## Known issues

Google InterstitialAd API requires an Activity to be able to show the loaded interstitial ad. Therefore, the Equativ __SASInterstitialManager instance must be created with an Activity instance as "context" parameter in the application.__ Passing a non Activity context (typically, the ApplicationContext) will make the SASGMAInterstitialAdapter fail when requesting a Google mediated interstitial ad.


