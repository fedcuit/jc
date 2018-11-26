import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel
import java.nio.channels.spi.AbstractSelector
import java.nio.channels.spi.SelectorProvider

val selector: AbstractSelector = SelectorProvider.provider().openSelector()

fun main(args: Array<String>) {
    val channel = SocketChannel.open()
    channel.configureBlocking(false)
    channel.connect(InetSocketAddress(8000))

    channel.register(selector, SelectionKey.OP_CONNECT)

    startComm()
}

private fun startComm() {
    while (true) {
        if (!selector.isOpen) {
            break
        }
        selector.select()
        val iterator = selector.selectedKeys().iterator()
        while (iterator.hasNext()) {
            val sk = iterator.next()
            iterator.remove()
            when {
                sk.isConnectable -> connect(sk)
                sk.isReadable -> read(sk)
            }
        }
    }
}

private fun read(sk: SelectionKey) {
    val socketChannel = sk.channel() as SocketChannel
    val byteBuffer = ByteBuffer.allocate(100)

    socketChannel.read(byteBuffer)
    val data = byteBuffer.array()
    val message = String(data).trim()

    println("Received from server: $message")

    socketChannel.close()
    selector.close()
}

private fun connect(sk: SelectionKey) {
    val socketChannel = sk.channel() as SocketChannel
    if (socketChannel.isConnectionPending) {
        socketChannel.finishConnect()
    }

    socketChannel.configureBlocking(false)
    socketChannel.write(ByteBuffer.wrap("Hello${System.lineSeparator()}".toByteArray()))

    socketChannel.register(selector, SelectionKey.OP_READ)
}