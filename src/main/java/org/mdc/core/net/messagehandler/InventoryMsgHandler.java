package org.mdc.core.net.messagehandler;

import lombok.extern.slf4j.Slf4j;
import org.mdc.common.utils.Sha256Hash;
import org.mdc.core.net.MdcNetDelegate;
import org.mdc.core.net.message.InventoryMessage;
import org.mdc.core.net.message.MdcMessage;
import org.mdc.core.net.peer.Item;
import org.mdc.core.net.peer.PeerConnection;
import org.mdc.core.net.service.AdvService;
import org.mdc.protos.Protocol.Inventory.InventoryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j(topic = "net")
@Component
public class InventoryMsgHandler implements MdcMsgHandler {

  @Autowired
  private MdcNetDelegate MdcNetDelegate;

  @Autowired
  private AdvService advService;

  @Autowired
  private TransactionsMsgHandler transactionsMsgHandler;

  private int maxCountIn10s = 10_000;

  @Override
  public void processMessage(PeerConnection peer, MdcMessage msg) {
    InventoryMessage inventoryMessage = (InventoryMessage) msg;
    InventoryType type = inventoryMessage.getInventoryType();

    if (!check(peer, inventoryMessage)) {
      return;
    }

    for (Sha256Hash id : inventoryMessage.getHashList()) {
      Item item = new Item(id, type);
      peer.getAdvInvReceive().put(item, System.currentTimeMillis());
      advService.addInv(item);
    }
  }

  private boolean check(PeerConnection peer, InventoryMessage inventoryMessage) {
    InventoryType type = inventoryMessage.getInventoryType();
    int size = inventoryMessage.getHashList().size();

    if (peer.isNeedSyncFromPeer() || peer.isNeedSyncFromUs()) {
      logger.warn("Drop inv: {} size: {} from Peer {}, syncFromUs: {}, syncFromPeer: {}.",
          type, size, peer.getInetAddress(), peer.isNeedSyncFromUs(), peer.isNeedSyncFromPeer());
      return false;
    }

    if (type.equals(InventoryType.TRX)) {
      int count = peer.getNodeStatistics().messageStatistics.mdcInTrxInventoryElement.getCount(10);
      if (count > maxCountIn10s) {
        logger.warn("Drop inv: {} size: {} from Peer {}, Inv count: {} is overload.",
            type, size, peer.getInetAddress(), count);
        return false;
      }

      if (transactionsMsgHandler.isBusy()) {
        logger.warn("Drop inv: {} size: {} from Peer {}, transactionsMsgHandler is busy.",
            type, size, peer.getInetAddress());
        return false;
      }
    }

    return true;
  }
}
