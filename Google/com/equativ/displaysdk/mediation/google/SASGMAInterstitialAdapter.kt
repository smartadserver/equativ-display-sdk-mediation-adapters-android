package com.equativ.displaysdk.mediation.google

import android.app.Activity
import android.content.Context
import android.util.Log
import com.equativ.displaysdk.mediation.google.SASGMAUtil.getAdUnitID
import com.equativ.displaysdk.mediation.google.SASGMAUtil.initGoogleMobileAds
import com.equativ.displaysdk.mediation.SASMediationInterstitialAdapter
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

/**
 * Mediation adapter class for Google mobile ads interstitial format
 */
class SASGMAInterstitialAdapter : SASMediationInterstitialAdapter {
    // Google mobile ads interstitial ad
    var interstitialAd: InterstitialAd? = null

    override val sdkName = "Google mobile ads"

    override val sdkVersion = MobileAds.getVersion().toString()

    override val adapterVersion = "1.0.0"

    // WeakReference on Activity at loading time for future display
    private lateinit var activityWeakReference: WeakReference<Activity>

    /// SASMediationInterstitialAdapter implementation
    override var adapterListener: SASMediationInterstitialAdapter.MediationInterstitialAdapterListener? =
        null

    override fun loadAd(
        context: Context,
        serverSideParametersString: String,
        clientSideParameters: Map<String, Any>?
    ) {

        // Google Mobile Ads API need to be executed in Main
        CoroutineScope(Dispatchers.Main).launch {
            // reset any previous leftover (?) interstitial
            interstitialAd = null
            if (context !is Activity) {
                adapterListener?.onMediationAdFailedToLoad(
                    "Google interstitial requires the Context to be an Activity for display",
                    false
                )
            } else {
                activityWeakReference = WeakReference(context)
                val gma = initGoogleMobileAds(context, serverSideParametersString)
                val adUnitID = getAdUnitID(serverSideParametersString)
                if (SASGMAUtil.GoogleMobileAds.ADMOB == gma) {
                    // create Google mobile ad request
                    val adRequest = AdRequest.Builder().build()
                    InterstitialAd.load(
                        context,
                        adUnitID,
                        adRequest,
                        createInterstitialAdLoadCallback()
                    )
                } else if (SASGMAUtil.GoogleMobileAds.AD_MANAGER == gma) {
                    // create Google mobile ad request
                    val publisherAdRequest = AdManagerAdRequest.Builder().build()

                    // create Google mobile ads interstitial ad object
                    AdManagerInterstitialAd.load(
                        context,
                        adUnitID,
                        publisherAdRequest,
                        createInterstitialAdLoadCallback()
                    )
                }
            }
        }
    }

    override fun show() {
        interstitialAd?.let { interstitialAd ->
            activityWeakReference.get()?.let { activity ->
                CoroutineScope(Dispatchers.Main).launch {
                    interstitialAd.show(activity)
                }
            } ?: throw Exception("Activity to display Google interstitial is null")
        } ?: throw Exception("No Google mobile ads interstitial ad loaded !")
    }

    private fun createInterstitialAdLoadCallback(): InterstitialAdLoadCallback {
        return object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Google mobile ads ad onAdLoaded for interstitial")
                this@SASGMAInterstitialAdapter.interstitialAd = interstitialAd

                // Create fullscreen callback
                interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        // Called when fullscreen content failed to show.
                        Log.d(
                            "TAG",
                            "Google mobile ads onAdFailedToShowFullScreenContent : " + adError.message
                        )

                        adapterListener?.onMediationAdFailedToShow(adError.message)
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "Google mobile ads onAdShowedFullScreenContent for interstitial")
                        adapterListener?.onMediationAdShown()
                    }

                    override fun onAdDismissedFullScreenContent() {
                        Log.d(
                            TAG,
                            "Google mobile ads onAdDismissedFullScreenContent for interstitial"
                        )
                        adapterListener?.onMediationAdDismissed()
                    }

                    override fun onAdImpression() {
                        Log.d(TAG, "Google mobile ads onAdImpression for interstitial")
                    }

                    override fun onAdClicked() {
                        Log.d(TAG, "Google mobile ads onAdClicked for interstitial")
                        adapterListener?.onMediationAdClicked()
                    }
                }

                // notify Smart SDK of successful interstitial loading
                adapterListener?.onMediationAdLoaded()
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.d(
                    TAG,
                    "Google mobile ads onAdFailedToLoad for interstitial (error:$loadAdError)"
                )
                val isNoAd = loadAdError.code == AdRequest.ERROR_CODE_NO_FILL
                adapterListener?.onMediationAdFailedToLoad(
                    "Google mobile ads interstitial ad loading error $loadAdError",
                    isNoAd
                )
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "Google mobile ads onDestroy not supported for interstitial")
    }

    companion object {
        // tag for logging purposes
        private val TAG = SASGMAInterstitialAdapter::class.java.simpleName
    }
}