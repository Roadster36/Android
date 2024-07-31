package com.duckduckgo.duckplayer.api

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.duckduckgo.duckplayer.api.databinding.BottomSheetDuckPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DuckPlayerBottomSheet : BottomSheetDialogFragment() {

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

    companion object {
        fun newInstance(): DuckPlayerBottomSheet =
            DuckPlayerBottomSheet()
    }
}
