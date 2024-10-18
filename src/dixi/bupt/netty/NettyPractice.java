package dixi.bupt.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author chendixi
 * Created on 2024-03-31
 */
public class NettyPractice {
    /**
     * NIO 的问题：
     * NIO的类库和API繁杂，学习成本高，你需要熟练掌握Selector、ServerSocketChannel、SocketChannel、ByteBuffer等。
     * 需要熟悉Java多线程编程。这是因为NIO编程涉及到Reactor模式，你必须对多线程和网络编程非常熟悉，才能写出高质量的NIO程序。
     * 臭名昭著的epoll bug。它会导致Selector空轮询，最终导致CPU 100%。直到JDK1.7版本依然没得到根本性的解决。
     * ======
     * Netty 优点很多
     * API使用简单，学习成本低。
     * 功能强大，内置了多种解码编码器，支持多种协议。
     * 性能高，对比其他主流的NIO框架，Netty的性能最优。
     * 社区活跃，发现BUG会及时修复，迭代版本周期短，不断加入新的功能。
     * Dubbo、Elasticsearch都采用了Netty，质量得到验证。
     * ======
     * Netty 代码层面API 库的核心组件
     * Channel ----Socket
     * EventLoop ----控制流，多线程处理，并发；
     * ChannelHandler和ChannelPipeline
     * Bootstrap 和 ServerBootstrap
     * ======
     * Netty 面试题：https://bbs.huaweicloud.com/blogs/273799
     */

    @SuppressWarnings("checkstyle:MagicNumber")
    public static void main(String[] args) {
        //创建两个线程组 boosGroup、workerGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建服务端的启动对象，设置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            //设置两个线程组boosGroup和workerGroup
            bootstrap.group(bossGroup, workerGroup)
                    //设置服务端通道实现类型
                    .channel(NioServerSocketChannel.class)
                    //设置线程队列得到连接个数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //使用匿名内部类的形式初始化通道对象
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //给pipeline管道设置处理器
                            socketChannel.pipeline().addLast(new MyServerHandler());
                        }
                    }); //给workerGroup的EventLoop对应的管道设置处理器
            System.out.println("java技术爱好者的服务端已经准备就绪...");
            //绑定端口号，启动服务端
            ChannelFuture channelFuture = bootstrap.bind(6666).sync();
            //对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            //
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
