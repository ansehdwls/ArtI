package com.hexa.arti.ui.profile

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.databinding.FragmentInstagramBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InstagramFragment : BaseFragment<FragmentInstagramBinding>(R.layout.fragment_instagram) {

    private val viewModel: InstagramViewModel by viewModels()

    override fun init() {

        viewModel.resultString.observe(viewLifecycleOwner) {
            makeToast(it)
            findNavController().popBackStack()
        }

        binding.webView.settings.javaScriptEnabled = true

        binding.webView.apply {
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (url != null && url.contains("callback?code")) {
                        viewModel.postInstagramUrl(url)
                        binding.webView.loadUrl("javascript:window.Android.processHTML(document.documentElement.outerHTML);")
                        return true
                    }
                    return super.shouldOverrideUrlLoading(view, url)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                }
            }
            loadUrl("http://j11d106.p.ssafy.io/instagram/redirect")
        }
    }

}