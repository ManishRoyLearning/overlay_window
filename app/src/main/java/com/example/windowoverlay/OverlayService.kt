package com.example.windowoverlay

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.Toast

class OverlayService : Service(),OnTouchListener, OnClickListener{
    private var moving = false
    private var initialtouchY = 0.0f
    private var initialTouchX = 0.0f
    private var initialY = 0
    private var initialX = 0
    private lateinit var params: WindowManager.LayoutParams
    private lateinit var overLayButton: ImageButton
    private lateinit var windowManager: WindowManager

    override fun onCreate() {
        super.onCreate()

        Toast.makeText(this, "Service Created",Toast.LENGTH_SHORT).show()

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager



        overLayButton = ImageButton(this)
        overLayButton.setImageResource(R.drawable.circle_launcher)
        overLayButton.background = null
        overLayButton.setOnTouchListener(this)
        overLayButton.setOnClickListener(this)

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }else{
            WindowManager.LayoutParams.TYPE_PHONE
        }

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP or Gravity.START

        params.x = 0
        params.y = 100

        windowManager.addView(overLayButton,params)
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(overLayButton)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        view!!.performClick()

        when(event!!.action){
            MotionEvent.ACTION_DOWN ->{
                initialX = params.x
                initialY = params.y
                initialTouchX = event.rawX
                initialtouchY = event.rawY
                moving = true
            }
            MotionEvent.ACTION_MOVE ->{
                params.x = initialX + (event.rawX - initialTouchX).toInt()
                params.y = initialY + (event.rawY - initialtouchY).toInt()
                windowManager.updateViewLayout(overLayButton,params)
            }
        }
        return true
    }

    override fun onClick(p0: View?) {
        if (!moving)Toast.makeText(this,"Button Touched",Toast.LENGTH_SHORT).show()
    }

}