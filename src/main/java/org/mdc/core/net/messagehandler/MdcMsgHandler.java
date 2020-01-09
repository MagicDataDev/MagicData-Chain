package org.mdc.core.net.messagehandler;

import org.mdc.core.exception.P2pException;
import org.mdc.core.net.message.MdcMessage;
import org.mdc.core.net.peer.PeerConnection;

public interface MdcMsgHandler {

  void processMessage(PeerConnection peer, MdcMessage msg) throws P2pException;

}
