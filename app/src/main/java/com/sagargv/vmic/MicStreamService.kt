package com.sagargv.vmic

import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


class MicStreamService : JobIntentService() {
    companion object {
        @JvmStatic var isStreaming: Boolean = false
    }

    override fun onHandleWork(intent: Intent) {
        isStreaming = true

        val port = intent.getIntExtra("port", 9009)
        val addr = intent.getStringExtra("addr") ?: "192.168.1.2"

        Log.e("TAG", "Start streaming -- " + addr + ":" + port)

        val udpSocket = DatagramSocket()
        val serverAddr: InetAddress = InetAddress.getByName(addr)
        val buf = "The String to Send".toByteArray()
        while (isStreaming) {
            val packet = DatagramPacket(buf, buf.size, serverAddr, port)
            udpSocket.send(packet)
        }

        udpSocket.disconnect()
        udpSocket.close()

        Log.e("TAG", "stopped streaming")
    }
}