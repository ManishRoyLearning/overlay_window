package com.example.windowoverlay

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var dialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val start_service: Button = findViewById(R.id.start_service)
        val stop_service: Button = findViewById(R.id.stop_service)

        var canDraw = true

        var intent: Intent? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            canDraw = Settings.canDrawOverlays(this)
            if (!canDraw && intent != null){
                startActivity(intent)
            }
        }

        start_service.setOnClickListener {
            val service = Intent(this@MainActivity,OverlayService::class.java)
            startService(service)
        }

        stop_service.setOnClickListener {
            val service = Intent(this@MainActivity,OverlayService::class.java)
            stopService(service)
        }

    }

    private fun isServiceRunning(): Boolean{
        var manger = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        for (service in manger.getRunningServices(Int.MAX_VALUE)){

            if (OverlayService::class.java.name == service.service.className)
                return false
        }
        return false
    }

    private fun requestFloatingWindowPermission() {

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle("Screen Overlay Permission Needed")
        builder.setMessage("Enable 'Display over the App' from settings")
        builder.setPositiveButton("Open Settings", DialogInterface.OnClickListener { dialog, which ->

            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, RESULT_OK)

        })
        dialog = builder.create()
        dialog.show()
    }

    private fun checkOverlayPermission(): Boolean{
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            Settings.canDrawOverlays(this)
        }
        else return true
    }
}