package com.sagargv.vmic

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.JobIntentService

private const val LOG_TAG = "vMic"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class MainActivity : AppCompatActivity() {
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    var isStreaming = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onMicButtonClick(v: View) {
        if (!isStreaming) {
            isStreaming = true
            startStreaming()
            v.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
        }
        else {
            isStreaming = false
            stopStreaming()
            v.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
        }
    }

    private fun startStreaming() {
        val serviceIntent = Intent().apply {
            putExtra("stream_addr", "192.168.1.5")
        }
        JobIntentService.enqueueWork(this, MicStreamService::class.java, 0, serviceIntent)
    }

    private fun stopStreaming() {
        MicStreamService.isStreaming = false
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }
}