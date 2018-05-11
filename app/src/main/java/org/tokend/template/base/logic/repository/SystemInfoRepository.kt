package org.tokend.template.base.logic.repository

import io.reactivex.Observable
import io.reactivex.Single
import org.tokend.sdk.api.ApiService
import org.tokend.sdk.api.models.SystemInfo
import org.tokend.template.base.logic.repository.base.SimpleSingleItemRepository
import org.tokend.template.extensions.toSingle
import org.tokend.wallet.NetworkParams

class SystemInfoRepository(
        private val api: ApiService
) : SimpleSingleItemRepository<SystemInfo>() {
    override fun getItem(): Observable<SystemInfo> {
        return api.getSystemInfo().toSingle().toObservable()
    }

    fun getNetworkParams(): Single<NetworkParams> {
        return updateIfNotFreshDeferred()
                .toSingle {
                    item?.passphrase?.let { NetworkParams(it) }
                            ?: throw IllegalStateException("Missing network passphrase")
                }
    }
}