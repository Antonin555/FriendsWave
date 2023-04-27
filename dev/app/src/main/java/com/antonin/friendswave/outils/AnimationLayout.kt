package com.antonin.friendswave.outils

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator

class AnimationLayout {



    //https://stackoverflow.com/questions/4946295/android-expand-collapse-animation
    fun expand(view: View, duration:Int, targetHeight:Int) {


        var prevHeight  = view.getHeight()

        view.visibility = View.VISIBLE
        val valueAnimator : ValueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)

        valueAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                view.layoutParams.height = animation.getAnimatedValue() as Int
                view.requestLayout()
            }
        });
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.duration = duration.toLong()
        valueAnimator.start()

    }

    fun  collapse(view: View, duration:Int, targetHeight:Int) {
        val prevHeight: Int = view.height
        val valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.addUpdateListener { animation ->
            view.layoutParams.height = animation.animatedValue as Int
            view.requestLayout()
        }
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.duration = duration.toLong()
        valueAnimator.start()

    }
}