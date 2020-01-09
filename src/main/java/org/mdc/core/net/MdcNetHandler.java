package org.mdc.core.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.mdc.common.overlay.server.Channel;
import org.mdc.common.overlay.server.MessageQueue;
import org.mdc.core.net.message.MdcMessage;
import org.mdc.core.net.peer.PeerConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MdcNetHandler extends SimpleChannelInboundHandler<MdcMessage> {

  protected PeerConnection peer;

  private MessageQueue msgQueue;

  @Autowired
  private MdcNetService MdcNetService;

//  @Autowired
//  private MdcNetHandler (final ApplicationContext ctx){
//    MdcNetService = ctx.getBean(MdcNetService.class);
//  }

  @Override
  public void channelRead0(final ChannelHandlerContext ctx, MdcMessage msg) throws Exception {
    msgQueue.receivedMessage(msg);
    MdcNetService.onMessage(peer, msg);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    peer.processException(cause);
  }

  public void setMsgQueue(MessageQueue msgQueue) {
    this.msgQueue = msgQueue;
  }

  public void setChannel(Channel channel) {
    this.peer = (PeerConnection) channel;
  }

}