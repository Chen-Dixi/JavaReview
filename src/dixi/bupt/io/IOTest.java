package dixi.bupt.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class IOTest {
    private FileInputStream fileInputStream;

    public static void main(String[] args) throws IOException {

        String src = "src/dixi/bupt/io/in";
        String dist = "src/dixi/bupt/io/dist.txt";
        FileInputStream fileInputStream = new FileInputStream(src);
        FileOutputStream fileOutputStream = new FileOutputStream(dist);

        byte[] buffer = new byte[20 * 1024];
        int cnt;

        // 读取会返回读取的字节数目
        while( (cnt = fileInputStream.read(buffer, 0, buffer.length))!=-1 ){
            fileOutputStream.write(buffer, 0, cnt);
        }

        fileInputStream.close();
        fileOutputStream.close();

    }

    private static void selectorTest() throws IOException{
        // 选择器
        Selector selector = Selector.open();
        // 通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        ssChannel.configureBlocking(false); //非阻塞
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);

        int interestSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE;

        int num = selector.select();//使用 select() 来监听到达的事件，它会一直阻塞直到有至少一个事件到达。

        // 获取到达的事件
        while(true) {
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = keys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    //
                } else if (key.isReadable()) {
                    //
                }
                keyIterator.remove();
            }
        }
    }
}
