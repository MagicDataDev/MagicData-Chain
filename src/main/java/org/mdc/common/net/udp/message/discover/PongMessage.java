package org.mdc.common.net.udp.message.discover;

import com.google.protobuf.ByteString;
import org.mdc.common.net.udp.message.Message;
import org.mdc.common.overlay.discover.node.Node;
import org.mdc.common.utils.ByteArray;
import org.mdc.core.config.args.Args;
import org.mdc.protos.Discover;
import org.mdc.protos.Discover.Endpoint;

import static org.mdc.common.net.udp.message.UdpMessageTypeEnum.DISCOVER_PONG;

public class PongMessage extends Message {

  private Discover.PongMessage pongMessage;

  public PongMessage(byte[] data) throws Exception {
    super(DISCOVER_PONG, data);
    this.pongMessage = Discover.PongMessage.parseFrom(data);
  }

  public PongMessage(Node from, long sequence) {
    super(DISCOVER_PONG, null);
    Endpoint toEndpoint = Endpoint.newBuilder()
        .setAddress(ByteString.copyFrom(ByteArray.fromString(from.getHost())))
        .setPort(from.getPort())
        .setNodeId(ByteString.copyFrom(from.getId()))
        .build();
    this.pongMessage = Discover.PongMessage.newBuilder()
        .setFrom(toEndpoint)
        .setEcho(Args.getInstance().getNodeP2pVersion())
        .setTimestamp(sequence)
        .build();
    this.data = this.pongMessage.toByteArray();
  }

  public int getVersion() {
    return this.pongMessage.getEcho();
  }

  @Override
  public long getTimestamp() {
    return this.pongMessage.getTimestamp();
  }

  @Override
  public Node getFrom() {
    return Message.getNode(pongMessage.getFrom());
  }

  @Override
  public String toString() {
    return "[pongMessage: " + pongMessage;
  }
}
