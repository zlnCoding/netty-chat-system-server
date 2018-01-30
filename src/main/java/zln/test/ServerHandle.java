package zln.test;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

/**
 * 描述:
 * 服务端处理类
 *
 * @auth zln
 * @create 2018-01-26 9:41
 */
public class ServerHandle extends ChannelHandlerAdapter {

    private List<ChannelHandlerContext> arrayList = null;

    public ServerHandle(List<ChannelHandlerContext> arrayList) {
        this.arrayList = arrayList;
    }

    /**
     * 异常处理方法
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //将连接的channel添加到数组中
        arrayList.add(ctx);
    }

    /**
     * 读事件方法
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            for (ChannelHandlerContext channelHandlerContext : arrayList) {
                if (channelHandlerContext != ctx) {
                    channelHandlerContext.channel().writeAndFlush(Unpooled.copiedBuffer(msg.toString().getBytes()));
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }
}
