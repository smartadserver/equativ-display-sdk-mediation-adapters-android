package com.equativ.displaysdk.mediation.ogury

import android.content.Context
import android.util.Log
import com.equativ.displaysdk.mediation.SASMediationBannerAdapter
import com.equativ.displaysdk.mediation.ogury.SASOguryUtil.getAdUnitID
import com.equativ.displaysdk.mediation.ogury.SASOguryUtil.getAssetKey
import com.ogury.core.OguryError
import com.ogury.ed.OguryBannerAdListener
import com.ogury.ed.OguryBannerAdSize
import com.ogury.ed.OguryBannerAdView
import com.ogury.sdk.Ogury
import com.ogury.sdk.OguryConfiguration

class SASOguryBannerAdapter : SASMediationBannerAdapter, OguryBannerAdListener {

    // Ogury banner ad view
    private var bannerAdView: OguryBannerAdView? = null

    override val sdkName = "Ogury SDK"

    override val sdkVersion = Ogury.getSdkVersion() ?: "Unknown version"

    override val adapterVersion = "1.0.0"

    /// SASMediationBannerAdapter implementation

    override var adapterListener: SASMediationBannerAdapter.MediationBannerAdapterListener? = null

    override fun loadAd(context: Context, serverSideParametersString: String, clientSideParameters: Map<String, Any>?) {
        /// CONFIGURATION START

        // Init the Ogury SDK ad each call, the API KEY can be different at each call
        Ogury.start(OguryConfiguration.Builder(context, getAssetKey(serverSideParametersString)).build())

        /// CONFIGURATION END

        /// BANNER LOADING
        val bannerAdView = OguryBannerAdView(context)
        bannerAdView.setListener(this)
        bannerAdView.setAdUnit(getAdUnitID(serverSideParametersString))
        bannerAdView.setAdSize(getBannerAdSize(serverSideParametersString))

        bannerAdView.loadAd()

        this.bannerAdView = bannerAdView
    }

    override fun onDestroy() {
        this.bannerAdView?.destroy()
    }

    /**
     * Utility method to return a Ogury banner ad size from serverParametersString
     */
    private fun getBannerAdSize(serverParametersString: String) =
        when (serverParametersString.split("|").getOrElse(2){"0"}) {
            "1" -> OguryBannerAdSize.MPU_300x250
            else -> OguryBannerAdSize.SMALL_BANNER_320x50
        }

    /// OguryBannerAdListener implementation
    override fun onAdLoaded() {
        Log.d(TAG, "Ogury banner listener onAdLoaded")

        bannerAdView?.let {
            adapterListener?.onMediationAdLoaded(it, null, null)
        }
    }

    override fun onAdDisplayed() {
        Log.d(TAG, "Ogury listener onAdDisplayed")
    }

    override fun onAdClicked() {
        Log.d(TAG, "Ogury listener onAdClicked")
        adapterListener?.onMediationAdClicked()
    }

    override fun onAdClosed() {
        Log.d(TAG, "Ogury listener onAdClosed")
        adapterListener?.onMediationAdCollapsed()
    }

    override fun onAdError(error: OguryError?) {
        Log.d(TAG, "Ogury listener onAdError $error")

        /**
         * From Ogury documentation :
         *
         * NO_INTERNET_CONNECTION = 0;
         * LOAD_FAILED = 2000;
         * AD_DISABLED = 2001;
         * PROFIG_NOT_SYNCED = 2002;
         * AD_EXPIRED = 2003;
         * SDK_INIT_NOT_CALLED = 2004;
         * ANOTHER_AD_ALREADY_DISPLAYED = 2005;
         * SDK_INIT_FAILED = 2006;
         * ACTIVITY_IN_BACKGROUND = 2007;
         * AD_NOT_AVAILABLE = 2008;
         * AD_NOT_LOADED = 2009;
         * SHOW_FAILED = 2010;
         **/

        error?.let {
            val isNoFill = it.errorCode == 2001 || it.errorCode == 2008
            adapterListener?.onMediationAdFailedToLoad(
                "Ogury SASOguryBannerAdapter failed with error: $error",
                isNoFill
            )
        } ?: run {
            adapterListener?.onMediationAdFailedToLoad(
                "Ogury SASOguryBannerAdapter failed with unknown error",
                false
            )
        }
    }

    companion object {
        private val TAG = SASOguryBannerAdapter::class.java.simpleName
    }
}
