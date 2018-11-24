package io.fedcuit.github.socket.threaded

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket

fun main(args: Array<String>) {
    val client = Socket()
    client.connect(InetSocketAddress(8000))
    client.use {
        PrintWriter(client.getOutputStream(), true).use { writer ->
            val s = "Hello"
            println("Send to Server: $s")
            writer.println(s)

            BufferedReader(InputStreamReader(client.getInputStream())).use { reader ->
                val echo = reader.readLine()
                println("Echo from server: $echo")
            }

        }
    }
}