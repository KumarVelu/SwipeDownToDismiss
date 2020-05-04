package com.velu.android.swipedowntodismiss

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_image.*


class ImageActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ImageActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val swipeImageTouchListener = SwipeImageTouchListener(swipe_image)
        swipe_parent.setOnTouchListener(swipeImageTouchListener)

        swipeImageTouchListener.setSwipeListener(object : SwipeImageTouchListener.SwipeListener {
            override fun onDragStart() {
                Log.i(TAG, "onDragStart: ")
            }

            override fun onDragStop() {
                Log.i(TAG, "onDragStop: ")
            }

            override fun onDismissed() {
                Log.i(TAG, "onDismissed: ")
                finish()
            }

        })
    }
}

class SwipeImageTouchListener(private val swipeView: View) : View.OnTouchListener{

    interface SwipeListener{
        fun onDragStart()
        fun onDragStop()
        fun onDismissed()
    }

    companion object {
        private const val TAG = "SwipeImageTouchListener"
    }

    // Allows us to know if we should use MotionEvent.ACTION_MOVE
    private var tracking = false
    // The Position where our touch event started
    private var startY: Float = 0.0f
    private var swipeListener: SwipeListener? = null
    private var isDragStarted = false

    fun setSwipeListener(swipeListener: SwipeListener){
        this.swipeListener = swipeListener
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        event?.let {
            when(it.action){
                MotionEvent.ACTION_DOWN -> {
                    val hitRect = Rect()
                    swipeView.getHitRect(hitRect)
                    if(hitRect.contains(event.x.toInt(), event.y.toInt()))
                        tracking = true
                    startY = it.y
                    return true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    tracking = false
                    animateSwipeView(v!!.height)
                    return true
                }

                MotionEvent.ACTION_MOVE -> {
                    if(tracking){
                        swipeView.translationY = it.y - startY
                        if(!isDragStarted){
                            isDragStarted = true
                            swipeListener?.onDragStart()
                        }

                    }
                    return true
                }

                else -> {
                    false
                }
            }
        }

        return false
    }

    /**
     * Using the current translation of swipeView, decide if it has moved
     * to the point where we want to remove it.
     */
    private fun animateSwipeView(parentHeight: Int){
        Log.i(TAG, "animateSwipeView: parentHeight : $parentHeight")
        val halfHeight = parentHeight / 2
        Log.i(TAG, "animateSwipeView :: octalHeight : $halfHeight")
        val currentPosition = swipeView.translationY

        Log.i(TAG, "animateSwipeView: currentPosition : $currentPosition")

        var animateTo = 0.0f
        if (currentPosition < -halfHeight) {
            animateTo = (-parentHeight).toFloat()
        } else if (currentPosition > halfHeight) {
            animateTo = parentHeight.toFloat()
        }

        if(animateTo == 0.0f){
            swipeListener?.onDragStop()
            isDragStarted = false
        }else{
            swipeListener?.onDismissed()
        }

        ObjectAnimator.ofFloat(swipeView, "translationY", currentPosition, animateTo)
            .setDuration(200)
            .start()
    }

}
