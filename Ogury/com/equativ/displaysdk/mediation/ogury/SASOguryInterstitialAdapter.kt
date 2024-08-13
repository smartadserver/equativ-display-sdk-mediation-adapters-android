package com.equativ.displaysdk.mediation.ogury

import android.content.Context
import android.util.Log
import com.equativ.displaysdk.mediation.SASMediationInterstitialAdapter
import com.equativ.displaysdk.mediation.ogury.SASOguryUtil.getAdUnitID
import com.equativ.displaysdk.mediation.ogury.SASOguryUtil.getAssetKey
import com.ogury.core.OguryError
import com.ogury.ed.OguryInterstitialAd
import com.ogury.ed.OguryInterstitialAdListener
import com.ogury.sdk.Ogury
import com.ogury.sdk.OguryConfiguration

class SASOguryInterstitialAdapter : SASMediationInterstitialAdapter, OguryInterstitialAdListener {

    // Ogury interstitial ad
    private var oguryInterstitial: OguryInterstitialAd? = null

    override val sdkName = "Ogury SDK"

    override val sdkVersion = Ogury.getSdkVersion() ?: "Unknown version"

    override val adapterVersion = "1.0.0"

    /// SASMediationInterstitialAdapter implementation

    override var adapterListener: SASMediationInterstitialAdapter.MediationInterstitialAdapterListener? = null

    override fun loadAd(
        context: Context,
        serverSideParametersString: String,
        clientSideParameters: Map<String, Any>?
    ) {
        /// CONFIGURATION START

        // Init the Ogury SDK ad each call, the API KEY can be different at each call
        Ogury.start(OguryConfiguration.Builder(context, getAssetKey(serverSideParametersString)).build())

        /// CONFIGURATION END

        // Instantiate the Presage interstitial
        oguryInterstitial = OguryInterstitialAd(context, getAdUnitID(serverSideParametersString)).apply {
            setListener(this@SASOguryInterstitialAdapter)
            load()
        }
    }

    override fun show() {
        oguryInterstitial?.run {
            if (isLoaded) {
                show()
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "Ogury onDestroy not supported for interstitial")
    }

    /// OguryInterstitialAdListener implementation

    override fun onAdLoaded() {
        Log.d(TAG, "Ogury banner listener onAdLoaded")
        adapterListener?.onMediationAdLoaded()
    }

    override fun onAdDisplayed() {
        Log.d(TAG, "Ogury listener onAdDisplayed")
        adapterListener?.onMediationAdShown()
    }

    override fun onAdClicked() {
        Log.d(TAG, "Ogury listener onAdClicked")
        adapterListener?.onMediationAdClicked()
    }

    override fun onAdClosed() {
        Log.d(TAG, "Ogury listener onAdClosed")
        adapterListener?.onMediationAdDismissed()
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

        val isNoFill = error?.errorCode == 2001 || error?.errorCode == 2008
        val errorMessage = error?.let { "Ogury SASOguryBannerAdapter failed with error: $error" }
            ?: "Ogury SASOguryBannerAdapter failed with unknown error"

        if (error?.errorCode == 2010) {
            adapterListener?.onMediationAdFailedToShow(errorMessage)
        } else {
            adapterListener?.onMediationAdFailedToLoad(errorMessage,isNoFill)
        }
    }

    companion object {
        private val TAG = SASOguryInterstitialAdapter::class.java.simpleName
    }
}