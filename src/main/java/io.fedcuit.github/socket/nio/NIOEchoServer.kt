package io.fedcuit.github.socket.nio

import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.SelectionKey.OP_READ
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.nio.channels.spi.AbstractSelector
import java.nio.channels.spi.SelectorProvider
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private val selector: AbstractSelector = SelectorProvider.provider().openSelector()
private val tp: ExecutorService = Executors.newCachedThreadPool()

fun main(args: Array<String>) {
    startServer()
}

fun startServer() {
    val responseTimes = hashMapOf<Socket, Long>()

    val port = 8000
    val ssc = ServerSocketChannel.open()
    ssc.configureBlocking(false)
    ssc.socket().bind(InetSocketAddress(port))
    ssc.register(selector, SelectionKey.OP_ACCEPT)

    println("Server listens at ${InetSocketAddress(port)}")

    while (true) {
        selector.select()
        val readyKeys = selector.selectedKeys()
        val iterator = readyKeys.iterator()
        while (iterator.hasNext()) {
            val sk = iterator.next()
            iterator.remove()

            when {
                sk.isAcceptable -> doAccept(sk)
                sk.isValid && sk.isReadable -> {
                    responseTimes.putIfAbsent((sk.channel() as SocketChannel).socket(), System.currentTimeMillis())
                    doRead(sk)
                }
                sk.isValid && sk.isWritable -> {
                    doWrite(sk)
                    val startAt = responseTimes.remove((sk.channel() as SocketChannel).socket())
                    if (startAt != null) {
                        println("Spent ${System.currentTimeMillis() - startAt}ms")
                    }
                }
            }
        }
    }
}

fun disconnect(sk: SelectionKey) {
    sk.cancel()
    sk.channel().close()
}

fun doAccept(sk: SelectionKey) {
    try {
        val ssc = sk.channel() as ServerSocketChannel

        val clientChannel = ssc.accept()
        clientChannel.configureBlocking(false)

        // register this channel for reading
        val clientKey = clientChannel.register(selector, SelectionKey.OP_READ)
        // allocate a message buffer for this channel and attach it to selection sk
        clientKey.attach(ClientMsgBuffer())

        println("Accepted connection from ${clientChannel.socket().remoteSocketAddress}")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun doRead(sk: SelectionKey) {
    val clientChannel = sk.channel() as SocketChannel
    val bb = ByteBuffer.allocate(8192) // 8KB

    val len = clientChannel.read(bb)
    if (len < 0) {
        disconnect(sk)
        return
    }

    bb.flip()
    tp.execute(HandleMsg(sk, bb))
}

fun doWrite(sk: SelectionKey) {
    val socketChannel = sk.channel() as SocketChannel
    val msgBuffer = sk.attachment() as ClientMsgBuffer
    val queue = msgBuffer.queue

    val bb = queue.last
    val len = socketChannel.write(bb)
    if (len == -1) {
        disconnect(sk)
        return
    }
    if (bb.remaining() == 0) {
        queue.removeLast()
    }
    if (queue.size == 0) {
        sk.interestOps(OP_READ)
    }
}


class ClientMsgBuffer {
    val queue = LinkedList<ByteBuffer>()
}

class HandleMsg(private val sk: SelectionKey, private val bb: ByteBuffer) : Runnable {
    override fun run() {
        val msgBuffer = sk.attachment() as ClientMsgBuffer
        msgBuffer.queue.addFirst(bb)

        // now this socket channel is interested in both read readiness and write readiness
        sk.interestOps(SelectionKey.OP_READ or SelectionKey.OP_WRITE)

        selector.wakeup()
    }
}