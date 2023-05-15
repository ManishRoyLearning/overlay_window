package com.example.windowoverlay

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.*
import android.view.View.OnClickListener
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout

class OverlayService : Service(), OnClickListener{
    private lateinit var crossButton: ImageButton
    private lateinit var tikButton: ImageButton
    private lateinit var hiddenView: ConstraintLayout
    private lateinit var cardView: CardView
    private lateinit var icon: ImageView
    private lateinit var processingIcon: TextView
    private var moving = false
    private var initialtouchY = 0.0f
    private var initialTouchX = 0.0f
    private var initialY = 0
    private var initialX = 0
    private lateinit var floatView: ViewGroup
    private lateinit var params: WindowManager.LayoutParams
//    private lateinit var overLayButton: ImageButton
    private lateinit var windowManager: WindowManager

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        Toast.makeText(this, "Service Created",Toast.LENGTH_SHORT).show()

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val inflater = baseContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        floatView = inflater.inflate(R.layout.floating_window,null) as ViewGroup

        cardView = floatView.findViewById(R.id.base_cardview)
        crossButton = floatView.findViewById(R.id.cross_button)
        tikButton = floatView.findViewById(R.id.tik_button)
        hiddenView = floatView.findViewById(R.id.hidden_view)
        icon = floatView.findViewById(R.id.icon)
        processingIcon = floatView.findViewById(R.id.heading)

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
        )

        params.gravity = Gravity.TOP or Gravity.START

        params.x = 0
        params.y = 100

        windowManager.addView(floatView,params)

        icon.setOnClickListener {

            if (hiddenView.visibility == View.VISIBLE) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                }
                hiddenView.visibility = View.GONE
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                }
                hiddenView.visibility = View.VISIBLE
                processingIcon.visibility = View.VISIBLE
            }
        }

        crossButton.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(cardView, AutoTransition())
            }
            hiddenView.visibility = View.GONE
            processingIcon.visibility = View.GONE
        }

        tikButton.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(cardView, AutoTransition())
            }
            hiddenView.visibility = View.GONE
            processingIcon.visibility = View.GONE
        }

        floatView.setOnTouchListener(object : View.OnTouchListener {

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
                        windowManager.updateViewLayout(floatView,params)
                    }
                }
                return true
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(floatView)
    }

    override fun onClick(p0: View?) {
        if (!moving)Toast.makeText(this,"Button Touched",Toast.LENGTH_SHORT).show()
    }

}