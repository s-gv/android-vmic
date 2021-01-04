package com.sagargv.vmic

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.JobIntentService


const val LOG_TAG = "LOG_vMic"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
private const val SHARED_PREFERENCES_FILE = "com.sagargv.vmic.shared_prefs"

class MainActivity : AppCompatActivity() {
    var addr = ""
    var port = 0

    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    var isStreaming = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE)
        addr = sharedPref.getString("addr", "192.168.29.236")!!
        port = sharedPref.getInt("port", 9009)
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
        val layout = LayoutInflater.from(this).inflate(R.layout.dialog_addr, null, false)
        layout.findViewById<EditText>(R.id.ip).setText(addr)
        layout.findViewById<EditText>(R.id.port).setText(port.toString())

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Set IP / port")
        builder.setView(layout)
        builder.apply {
            setPositiveButton(android.R.string.ok,
                    DialogInterface.OnClickListener { dialog, id ->
                        addr = layout.findViewById<EditText>(R.id.ip).text.toString()
                        port = layout.findViewById<EditText>(R.id.port).text.toString().toInt()

                        val sharedPref = getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE)
                        with (sharedPref.edit()) {
                            putString("addr", addr)
                            putInt("port", port)
                            apply()
                        }
                        //Log.i(LOG_TAG, ip + ":" + port)
                    })
            setNegativeButton(android.R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
        }

        builder.show()
    }

    fun onMicButtonClick(v: View) {
        if (!isStreaming) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
                    return
                }
            }
            startStreaming()
        }
        else {
            stopStreaming()
        }
    }

    private fun startStreaming() {
        isStreaming = true

        val serviceIntent = Intent().apply {
            putExtra("addr", addr)
            putExtra("port", port)
        }
        JobIntentService.enqueueWork(this, MicStreamService::class.java, 0, serviceIntent)

        val b = findViewById(R.id.button) as Button
        b.setText("End streaming")
        b.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))

        val statusView = findViewById<TextView>(R.id.statusView)
        statusView.setText("Streaming to " + addr + ":" + port)
    }

    private fun stopStreaming() {
        isStreaming = false

        MicStreamService.isStreaming = false

        val b = findViewById(R.id.button) as Button
        b.setText("Start mic streaming")
        b.setBackgroundColor(resources.getColor(android.R.color.holo_green_light))

        val statusView = findViewById<TextView>(R.id.statusView)
        statusView.setText("")
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
        if (permissionToRecordAccepted) {
            startStreaming()
        }
    }
}