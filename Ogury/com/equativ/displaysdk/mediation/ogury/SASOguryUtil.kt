package com.equativ.displaysdk.mediation.ogury

/**
 * Utility class regrouping utility methods for all Ogury adapters
 */
object SASOguryUtil {

    /**
     * Utility method to get Ogury Asset Key from serverParametersString
     */
    fun getAssetKey(serverParametersString: String) = serverParametersString.split("|")[0]

    /**
     * Utility method to get Ogury AdUnit ID from serverParametersString
     */
    fun getAdUnitID(serverParametersString: String) = serverParametersString.split("|")
        .getOrElse(1) { "" }


}