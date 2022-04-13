package dixi.bupt.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 创建一个服务器端，TCP server 通信程序的流程
 *
 * * 创建一个ServerSocket
 * * 从ServerSocket接受客户连接请求
 * * 创建一个服务线程处理新的连接
 * * 在服务线程中，从socket获得I/O流
 * * 对 I/O流进行读写操作
 * * 关闭I/O流
 * * 关闭Socket
 */
public class NIOServer {

    public static void main(String[] args) throws IOException {
            Selector selector = Selector.open();

            // Channel 是双向的，既可以读，又可以写
            ServerSocketChannel ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(false);
            // If the selector detects that the corresponding server-socket channel is ready to accept another
            // connection, or has an error pending, then it will add OP_ACCEPT to the key's ready set and add the key to
            // its selected-key set.

            // 将通道注册在选择器上，同时指明具体的事件
            ssChannel.register(selector, SelectionKey.OP_ACCEPT);

            // 1 创建一个ServerSocket
            ServerSocket serverSocket = ssChannel.socket();
            InetSocketAddress address = new InetSocketAddress("127.0.0.1",8888);
            serverSocket.bind(address);

            //事件循环，因为一次 select() 调用不能处理完所有的事件，并且服务器端有可能需要一直监听事件，因此服务器端处理事件的代码一般会
            // 放在一个死循环内。
            while(true){
                //This method performs a blocking selection operation. It returns only after at least one channel
                // is selected, this selector's wakeup method is invoked, or the current thread is interrupted,
                // whichever comes first.
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = keys.iterator();

                while(keyIterator.hasNext()){
                    SelectionKey key = keyIterator.next();
                    if(key.isAcceptable()){
                        ServerSocketChannel ssChannel1 = (ServerSocketChannel) key.channel();

                        // 给每一个链接创建一个channel
                        SocketChannel sChannel = ssChannel1.accept();
                        sChannel.configureBlocking(false);

                        //这个新链接从客户端读取数据。
                        sChannel.register(selector,SelectionKey.OP_READ);

                    }else if(key.isReadable()){
                        SocketChannel sChannel = (SocketChannel) key.channel();
                        System.out.println(readDataFromSocketChannel(sChannel));
                        sChannel.close();
                    }
                    keyIterator.remove();
                }
            }



    }
    // 从 SocketChannel里面读数据，用ByteBuffer
    private static String readDataFromSocketChannel(SocketChannel sChannel) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        StringBuilder data = new StringBuilder();

        while (true) {

            buffer.clear();
            int n = sChannel.read(buffer);
            if (n == -1) {
                break;
            }
            buffer.flip();
            int limit = buffer.limit();
            char[] dst = new char[limit];
            for (int i = 0; i < limit; i++) {
                dst[i] = (char) buffer.get(i);
            }
            data.append(dst);
            buffer.clear();
        }
        return data.toString();
    }
}

