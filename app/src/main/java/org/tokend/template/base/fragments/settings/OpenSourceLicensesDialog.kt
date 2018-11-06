package org.tokend.template.base.fragments.settings

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.StyleRes
import android.support.v7.app.AlertDialog
import android.webkit.WebSettings
import android.webkit.WebView
import org.tokend.template.R

@SuppressLint("RestrictedApi")
class OpenSourceLicensesDialog(
        context: Context,
        @StyleRes
        style: Int? = null
) {
    private val dialog: AlertDialog
    private val webView: WebView = WebView(context)

    init {
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE

        dialog =
                (if (style != null)
                    AlertDialog.Builder(context, style)
                else
                    AlertDialog.Builder(context)
                        )
                        .setView(
                                webView,
                                context.resources.getDimensionPixelSize(R.dimen.half_standard_padding),
                                context.resources.getDimensionPixelSize(R.dimen.half_standard_padding),
                                context.resources.getDimensionPixelSize(R.dimen.standard_padding),
                                context.resources.getDimensionPixelSize(R.dimen.half_standard_padding)
                        )
                        .setPositiveButton(R.string.ok, null)
                        .create()
    }

    fun show() {
        dialog.show()
        webView.post {
            if (dialog.isShowing) {
                webView.loadUrl("file:///android_asset/$REPORT_FILE")
            }
        }
    }

    companion object {
        private const val REPORT_FILE = "open_source_licenses.html"
    }
}