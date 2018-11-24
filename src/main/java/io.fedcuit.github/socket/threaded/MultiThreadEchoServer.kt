package io.fedcuit.github.socket.threaded

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

private class HandleMsg(val clientSocket: Socket) : Runnable {
    override fun run() {
        val begin = System.currentTimeMillis()
        clientSocket.use { clientSocket ->
            BufferedReader(InputStreamReader(clientSocket.getInputStream())).useLines { lines ->
                PrintWriter(clientSocket.getOutputStream(), true).use { printWriter ->
                    lines.forEach { printWriter.println(it) }
                    val end = System.currentTimeMillis()
                    println("Send ${end - begin}ms")
                }

            }

        }
    }
}

fun main(args: Array<String>) {
    val port = 8000
    val tp = Executors.newCachedThreadPool()
    val echoSever = ServerSocket(port)
    println("Server listens at: $port")
    while (true) {
        val clientSocket = echoSever.accept()
        println("${clientSocket.remoteSocketAddress} connect!")
        tp.submit(HandleMsg(clientSocket))
    }
}