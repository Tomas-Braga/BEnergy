package com.example.benergy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

@Suppress("DEPRECATION")
class Welcome3 : AppCompatActivity(), GestureDetector.OnGestureListener {

    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome3)
        gestureDetector = GestureDetector(this)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) {
            true
        }
        else {
            super.onTouchEvent(event)
        }
    }

    override fun onDown(p0: MotionEvent?): Boolean {
        return false
    }
    override fun onShowPress(p0: MotionEvent?) {
        return
    }
    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        return false
    }
    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return false
    }
    override fun onLongPress(p0: MotionEvent?) {
        return
    }


    override fun onFling(p0: MotionEvent, p1: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        try {
            val diffY = p1.y - p0.y
            val diffX = p1.x - p0.x
            if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > 100 && abs(velocityX) > 100) {
                    if (diffX < 0) {
                        val intent = Intent(this, Welcome4::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.left_out,R.anim.right_in)
                    }
                    else {
                        val intent = Intent(this, Welcome2::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.left_in,R.anim.right_out)
                    }
                }
            }
        }
        catch (exception: Exception) {
            exception.printStackTrace()
        }
        return true
    }
    override fun onBackPressed() {
        val intent = Intent(this, Welcome2::class.java)
        startActivity(intent)
    }
}