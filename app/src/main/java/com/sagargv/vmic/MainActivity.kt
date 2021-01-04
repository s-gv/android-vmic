package com.sagargv.vmic

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.JobIntentService


private const val LOG_TAG = "vMic"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class MainActivity : AppCompatActivity() {
    var ip = "192.168.29.236"
    var port = 9009

    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    var isStreaming = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.new_game -> {
                showIPDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showIPDialog() {
        val layout = LayoutInflater.from(this).inflate(R.layout.dialog_addr, null, false))
        val builder = AlertDialog.Builder(this)
        builder.setMessage("MSG")
        builder.setTitle("Title")
        builder.setView(layout)
        builder.apply {
            setPositiveButton(android.R.string.ok,
                    DialogInterface.OnClickListener { dialog, id ->
                        ip = layout.findViewById<EditText>(R.id.ip).toString()
                        port = layout.findViewById<EditText>(R.id.port).toString().toInt()

                        Log.e("TAG", ip + ":" + port)
                    })
            setNegativeButton(android.R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
        }

        builder.show()
    }

    fun onMicButtonClick(v: View) {
        val b = v as Button
        if (!isStreaming) {
            isStreaming = true
            startStreaming()
            b.setText("End streaming")
            b.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
        }
        else {
            isStreaming = false
            stopStreaming()
            b.setText("Start mic streaming")
            b.setBackgroundColor(resources.getColor(android.R.color.holo_green_light))
        }
    }

    private fun startStreaming() {
        val serviceIntent = Intent().apply {
            putExtra("stream_addr", "192.168.29.236")
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