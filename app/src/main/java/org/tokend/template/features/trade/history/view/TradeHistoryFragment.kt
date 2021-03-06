package org.tokend.template.features.trade.history.view

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_trade_history.*
import kotlinx.android.synthetic.main.include_error_empty_view.*
import org.tokend.template.R
import org.tokend.template.data.model.AssetPairRecord
import org.tokend.template.data.repository.tradehistory.TradeHistoryRepository
import org.tokend.template.features.trade.history.view.adapter.TradeHistoryAdapter
import org.tokend.template.fragments.BaseFragment
import org.tokend.template.util.ObservableTransformers
import org.tokend.template.view.util.LoadingIndicatorManager

class TradeHistoryFragment : BaseFragment() {

    private lateinit var assetPair: AssetPairRecord


    private val tradeHistoryRepository: TradeHistoryRepository
        get() = repositoryProvider.tradeHistory(assetPair.base, assetPair.quote)

    private val loadingIndicator = LoadingIndicatorManager(
            showLoading = { swipe_refresh.isRefreshing = true },
            hideLoading = { swipe_refresh.isRefreshing = false }
    )

    private lateinit var adapter: TradeHistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trade_history, container, false)
    }

    override fun onInitAllowed() {
        assetPair = arguments?.getSerializable(EXTRA_ASSET_PAIR) as? AssetPairRecord
                ?: return

        initViews()
        subscribeToTradeHistory()
        update()
    }

    private fun initViews() {
        initSwipeRefresh()
        initFields()
        initList()
    }

    private fun initSwipeRefresh() {
        swipe_refresh.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.accent))
        swipe_refresh.setOnRefreshListener { update(true) }
    }

    private fun initFields() {
        price_hint.text = getString(R.string.template_price_hint, assetPair.quote)
        amount_hint.text = getString(R.string.template_amount_hint, assetPair.base)
    }

    private fun initList() {
        trade_history_list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        adapter = TradeHistoryAdapter(amountFormatter)
        trade_history_list.adapter = adapter
        trade_history_list.listenBottomReach({ adapter.getDataItemCount() }) {
            tradeHistoryRepository.loadMore() || tradeHistoryRepository.noMoreItems
        }

        error_empty_view.observeAdapter(adapter, R.string.no_transaction_history)
        error_empty_view.setEmptyViewDenial { tradeHistoryRepository.isNeverUpdated }
    }

    private fun subscribeToTradeHistory() {
        tradeHistoryRepository
                .itemsSubject
                .compose(ObservableTransformers.defaultSchedulers())
                .subscribe {
                    adapter.setData(it)
                }
                .addTo(compositeDisposable)

        tradeHistoryRepository
                .loadingSubject
                .compose(ObservableTransformers.defaultSchedulers())
                .subscribe { loading ->
                    if (loading) {
                        if (tradeHistoryRepository.isOnFirstPage) {
                            loadingIndicator.setLoading(true)
                        } else {
                            adapter.showLoadingFooter()
                        }
                    } else {
                        loadingIndicator.setLoading(false)
                        adapter.hideLoadingFooter()
                    }
                }
                .addTo(compositeDisposable)

        tradeHistoryRepository
                .errorsSubject
                .compose(ObservableTransformers.defaultSchedulers())
                .subscribe { error ->
                    if (!adapter.hasData) {
                        error_empty_view.showError(error, errorHandlerFactory.getDefault()) {
                            update(true)
                        }
                    } else {
                        errorHandlerFactory.getDefault().handle(error)
                    }
                }
                .addTo(compositeDisposable)
    }

    private fun update(force: Boolean = false) {
        if (force) {
            tradeHistoryRepository.update()
        } else {
            tradeHistoryRepository.updateIfNotFresh()
        }
    }

    companion object {
        private const val EXTRA_ASSET_PAIR = "asset_pair"

        @JvmStatic
        fun newInstance(pair: AssetPairRecord): TradeHistoryFragment {
            val fragment = TradeHistoryFragment()
            fragment.arguments = Bundle().apply {
                putSerializable(EXTRA_ASSET_PAIR, pair)
            }
            return fragment
        }
    }
}