package com.example.moviemviimpl.utils

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

object MapAnimationUtils {


    fun polylineAnimator(): ValueAnimator {
        return ValueAnimator.ofInt(0, 100)
            .apply {
                interpolator = LinearInterpolator()
                duration = 4000
            }

        /**
         *"Kotlinaize" way above
         * **/
//        val valueAnimator = ValueAnimator.ofInt(0, 100)
//        valueAnimator.interpolator = LinearInterpolator()
//        valueAnimator.duration = 4000
//        return valueAnimator
    }

    fun iconAnimator(): ValueAnimator {
        return ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 3000
            interpolator = LinearInterpolator()
        }
    }
}