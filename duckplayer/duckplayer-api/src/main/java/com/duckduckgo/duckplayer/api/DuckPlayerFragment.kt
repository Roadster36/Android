package com.duckduckgo.duckplayer.api

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.duckduckgo.duckplayer.api.databinding.BottomSheetDuckPlayerBinding

class DuckPlayerFragment : DialogFragment() {

    private lateinit var binding: BottomSheetDuckPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetDuckPlayerBinding.inflate(inflater, container, false)
        LottieCompositionFactory.fromRawRes(context, R.raw.duckplayer)
        binding.duckPlayerAnimation.setAnimation(R.raw.duckplayer)
        binding.duckPlayerAnimation.playAnimation()
        binding.duckPlayerAnimation.repeatCount = LottieDrawable.INFINITE
        binding.dismissButton.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, com.duckduckgo.mobile.android.R.style.Widget_DuckDuckGo_DialogFullScreen)
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        dismiss()
    }
    companion object {
        fun newInstance(): DuckPlayerFragment =
            DuckPlayerFragment()
    }
}
