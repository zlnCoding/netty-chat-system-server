package zln.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * 描述: 服务端
 *
 * @auth zln
 * @create 2018-01-25 9:36
 */
public class Server {
    //用于接收client端的通信
    public static NioEventLoopGroup pGroup = null;
    //实际处理的管道
    public static NioEventLoopGroup wGroup = null;
    //接收port
    public static Scanner portScanner = null;
    //用于储存连接的channel
    public static List<ChannelHandlerContext> arrayList = Collections.synchronizedList(new ArrayList<ChannelHandlerContext>());

    public static void main(String[] args) throws IOException, InterruptedException {

        try {
            //用于接收client端的通信
            pGroup = new NioEventLoopGroup();
            //实际处理的管道
            wGroup = new NioEventLoopGroup();
            //辅助类
            ServerBootstrap bootstrap = new ServerBootstrap();
            //将两个group添加到辅助类中
            bootstrap.group(pGroup, wGroup)
                    //设置管道
                    .channel(NioServerSocketChannel.class)
                    //设置处理类
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new ServerHandle(arrayList));
                        }
                    })
                    //设置管道的大小
                    .option(ChannelOption.SO_BACKLOG, 1024);

            System.out.println("请输入端口号.");
            portScanner = new Scanner(System.in);
            String port = portScanner.nextLine();
            String addr = InetAddress.getLocalHost().getHostAddress();
            //异步绑定端口号
            ChannelFuture sync = bootstrap.bind(new InetSocketAddress(addr, Integer.parseInt(port))).sync();
            System.out.println("服务端启动成功,绑定地址:" + addr + ",绑定端口" + port);
            //异步监听关闭事件
            sync.channel().closeFuture().sync();

            //关闭两个group
            pGroup.shutdownGracefully();
            wGroup.shutdownGracefully();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (pGroup != null) {
                pGroup.shutdownGracefully();
            }
            if (wGroup != null) {
                wGroup.shutdownGracefully();
            }
            if (portScanner != null) {
                portScanner.close();
            }
        }

    }

}
