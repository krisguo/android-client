package org.tokend.template.features.wallet.details

import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_details_list.*
import org.tokend.template.R
import org.tokend.template.data.model.history.BalanceChange
import org.tokend.template.view.details.adapter.DetailsItemsAdapter

open class UnknownDetailsActivity : BalanceChangeDetailsActivity() {

    override fun displayDetails(item: BalanceChange) {
        setContentView(R.layout.activity_details_list)

        details_list.layoutManager = LinearLayoutManager(this)
        val adapter = DetailsItemsAdapter()
        details_list.adapter = adapter

        displayEffect(item, adapter)
        displayBalanceChange(item, adapter)
        displayDate(item, adapter)
    }
}
