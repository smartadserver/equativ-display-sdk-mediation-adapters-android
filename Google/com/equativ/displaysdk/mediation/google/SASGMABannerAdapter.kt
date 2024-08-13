package com.equativ.displaysdk.mediation.google

import android.content.Context
import android.util.Log
import android.view.View
import com.equativ.displaysdk.mediation.SASMediationBannerAdapter
import com.equativ.displaysdk.mediation.google.SASGMAUtil.getAdUnitID
import com.equativ.displaysdk.mediation.google.SASGMAUtil.initGoogleMobileAds
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Mediation adapter class for Google mobile ads banner format
 */
class SASGMABannerAdapter : SASMediationBannerAdapter {

    // Google mobile ads banner view instance
    private var adView: View? = null

    override val sdkName = "Google mobile ads"

    override val sdkVersion = MobileAds.getVersion().toString()

    override val adapterVersion = "1.0.0"

    /// SASMediationBannerAdapter implementation

    override var adapterListener: SASMediationBannerAdapter.MediationBannerAdapterListener? = null

    override fun loadAd(
        context: Context,
        serverSideParametersString: String,
        clientSideParameters: Map<String, Any>?
    ) {

        // Google Mobile Ads API need to be executed in Main
        CoroutineScope(Dispatchers.Main).launch {
            val adUnitID = getAdUnitID(serverSideParametersString)
            val gma = initGoogleMobileAds(context, serverSideParametersString)
            val adSize = getAdSize(serverSideParametersString)
            if (SASGMAUtil.GoogleMobileAds.ADMOB == gma) {
                // create google ad request
                val adRequest = AdRequest.Builder().build()

                // Create Google AdView and configure it.
                val adMobView = AdView(context)
                adMobView.adUnitId = adUnitID
                adMobView.setAdSize(adSize)
                val adListener = createAdListener(adMobView)

                // set listener on banner
                adMobView.adListener = adListener

                // perform ad request
                adMobView.loadAd(adRequest)
                adView = adMobView
            } else if (SASGMAUtil.GoogleMobileAds.AD_MANAGER == gma) {
                // create google publisher ad request
                val publisherAdRequest = AdManagerAdRequest.Builder().build()
                val adManagerView = AdManagerAdView(context)
                adManagerView.adUnitId = adUnitID
                adManagerView.setAdSizes(adSize)
                val adListener = createAdListener(adManagerView)

                // set listener on banner
                adManagerView.adListener = adListener

                // perform ad request
                adManagerView.loadAd(publisherAdRequest)
                adView = adManagerView
            }
        }
    }

    private fun createAdListener(adView: View): AdListener {
        // create Google banner listener that will intercept ad mob banner events and call appropriate SASMediationBannerAdapterListener counterpart methods
        return object : AdListener() {
            override fun onAdClosed() {
                Log.d(TAG, "Google mobile ads onAdClosed for banner")
                adapterListener?.onMediationAdCollapsed()
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.d(TAG, "Google mobile ads onAdFailedToLoad for banner (error : $loadAdError)")
                val isNoAd = loadAdError.code == AdRequest.ERROR_CODE_NO_FILL
                adapterListener?.onMediationAdFailedToLoad(
                    "Google mobile ads banner ad loading error : $loadAdError",
                    isNoAd
                )
            }

            override fun onAdClicked() {
                Log.d(TAG, "Google mobile ads onAdClicked for banner")
            }

            override fun onAdImpression() {
                Log.d(TAG, "Google mobile ads onAdImpression for banner")
            }

            override fun onAdOpened() {
                Log.d(TAG, "Google mobile ads onAdOpened for banner")
                adapterListener?.onMediationAdClicked()
            }

            override fun onAdLoaded() {
                Log.d(TAG, "Google mobile ads onAdLoaded for banner")
                adapterListener?.onMediationAdLoaded(adView, null, null)

            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "Google mobile ads onDestroy for banner")
        (adView as? AdView)?.destroy() ?: (adView as? AdManagerAdView)?.destroy()
    }

    /**
     * Utility method to get Banner Size from serverParametersString
     */
    private fun getAdSize(serverParametersString: String) =
        when (serverParametersString.split("|").getOrElse(2) { "0" }) {
            "1" -> AdSize.MEDIUM_RECTANGLE
            "2" -> AdSize.LEADERBOARD
            "3" -> AdSize.LARGE_BANNER
            else -> AdSize.BANNER
        }

    companion object {
        // tag for logging purposes
        private val TAG = SASGMABannerAdapter::class.java.simpleName
    }
}