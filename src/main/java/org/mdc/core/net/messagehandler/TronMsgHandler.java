package org.mdc.core.net.messagehandler;

import org.mdc.core.exception.P2pException;
import org.mdc.core.net.message.TronMessage;
import org.mdc.core.net.peer.PeerConnection;

public interface TronMsgHandler {

  void processMessage(PeerConnection peer, TronMessage msg) throws P2pException;

}
