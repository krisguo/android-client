package org.tokend.template.data.model

import com.google.gson.annotations.SerializedName
import okhttp3.HttpUrl
import java.io.Serializable

data class UrlConfig(
        @SerializedName("api")
        private val mApi: String,
        @SerializedName("storage")
        private val mStorage: String,
        @SerializedName("kyc")
        private val mKyc: String,
        @SerializedName("terms")
        private val mTerms: String,
        @SerializedName("host")
        private val mAppHost: String
) : Serializable {
    val api: String
        get() = mApi
                .addTrailSlashIfNeeded()
                .addProtocolIfNeeded()

    val storage: String
        get() = mStorage
                .addTrailSlashIfNeeded()
                .addProtocolIfNeeded()

    val kyc: String
        get() = mKyc
                .addTrailSlashIfNeeded()
                .addProtocolIfNeeded()

    val terms: String
        get() = mTerms
                .addTrailSlashIfNeeded()
                .addProtocolIfNeeded()

    val apiDomain: String
        get() = HttpUrl.parse(api)?.host() ?: api

    val appHost: String
        get() = mAppHost
                .addTrailSlashIfNeeded()
                .addProtocolIfNeeded()

    private fun String.addTrailSlashIfNeeded(): String {
        return if (this.endsWith('/')) this else this + "/"
    }

    private fun String.addProtocolIfNeeded(): String {
        return if (!contains("^.+//".toRegex()))
            "https://" + this
        else if (startsWith("//"))
            "https:" + this
        else
            this
    }
}