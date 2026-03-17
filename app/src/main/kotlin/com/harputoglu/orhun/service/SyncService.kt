package com.harputoglu.orhun.service

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class SyncService(context: Context, private val onCommandReceived: (String) -> Unit) {

    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val commandProcessor = com.harputoglu.orhun.util.CommandProcessor(context)
    private val serviceType = "_orhun._tcp."
    private val serviceName = "Orhun_${android.os.Build.MODEL}"
    private var serverSocket: ServerSocket? = null
    private var localPort: Int = 0

    private val registrationListener = object : NsdManager.RegistrationListener {
        override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {
            Log.d("SyncService", "Service registered: ${NsdServiceInfo.serviceName}")
        }
        override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
        override fun onServiceUnregistered(arg0: NsdServiceInfo) {}
        override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
    }

    init {
        startServer()
    }

    private fun startServer() {
        thread {
            try {
                serverSocket = ServerSocket(0)
                localPort = serverSocket!!.localPort
                registerService(localPort)

                while (true) {
                    val client = serverSocket?.accept() ?: break
                    handleClient(client)
                }
            } catch (e: Exception) {
                Log.e("SyncService", "Server error", e)
            }
        }
    }

    private fun handleClient(socket: Socket) {
        thread {
            try {
                val reader = BufferedReader(InputStreamReader(socket.inputStream))
                val command = reader.readLine()
                if (command != null) {
                    commandProcessor.processPayload(command)
                    onCommandReceived(command)
                }
                socket.close()
            } catch (e: Exception) {
                Log.e("SyncService", "Client error", e)
            }
        }
    }

    private fun registerService(port: Int) {
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = this@SyncService.serviceName
            serviceType = this@SyncService.serviceType
            setPort(port)
        }
        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
    }

    fun sendCommand(targetIp: String, targetPort: Int, command: String) {
        thread {
            try {
                val socket = Socket(targetIp, targetPort)
                val writer = PrintWriter(socket.outputStream, true)
                writer.println(command)
                socket.close()
            } catch (e: Exception) {
                Log.e("SyncService", "Send error", e)
            }
        }
    }

    fun stop() {
        try {
            nsdManager.unregisterService(registrationListener)
            serverSocket?.close()
        } catch (e: Exception) {}
    }
}
