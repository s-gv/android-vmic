package com.sagargv.vmic

import android.R.attr.port
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
        val port = 9009

        val udpSocket = DatagramSocket()
        val serverAddr: InetAddress = InetAddress.getByName("192.168.29.236")
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