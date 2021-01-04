package com.sagargv.vmic

import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.app.JobIntentService
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


private const val SAMPLE_RATE = 8000

class MicStreamService : JobIntentService() {
    companion object {
        @JvmStatic var isStreaming: Boolean = false
    }

    override fun onHandleWork(intent: Intent) {
        isStreaming = true

        val port = intent.getIntExtra("port", 9009)
        val addr = intent.getStringExtra("addr") ?: "192.168.1.2"

        Log.d(LOG_TAG, "Start streaming -- " + addr + ":" + port)

        val udpSocket = DatagramSocket()
        val serverAddr: InetAddress = InetAddress.getByName(addr)
        val buf = "The String to Send".toByteArray()

        val minBufSizeInBytes = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT)
        val bufSizeInBytes = 2*minBufSizeInBytes
        val buffer = ByteArray(bufSizeInBytes) //ShortArray(bufSizeInBytes)
        Log.d(LOG_TAG, "min_buf_size: "+ minBufSizeInBytes)
        val mic = AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_8BIT,
                bufSizeInBytes)
        mic.startRecording();
        while (isStreaming) {
            var toRead = buffer.size
            while(toRead > 0) {
                toRead -= mic.read(buffer, buffer.size - toRead, toRead);
            }

            val packet = DatagramPacket(buffer, buffer.size, serverAddr, port)
            udpSocket.send(packet)
        }

        mic.stop();
        mic.release();

        udpSocket.disconnect()
        udpSocket.close()

        Log.d(LOG_TAG, "stopped streaming")
    }
}