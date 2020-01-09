package org.mdc.common.overlay.discover.node.statistics;

import lombok.extern.slf4j.Slf4j;
import org.mdc.common.net.udp.message.UdpMessageTypeEnum;
import org.mdc.common.overlay.message.Message;
import org.mdc.core.net.message.FetchInvDataMessage;
import org.mdc.core.net.message.InventoryMessage;
import org.mdc.core.net.message.MessageTypes;
import org.mdc.core.net.message.TransactionsMessage;

@Slf4j
public class MessageStatistics {

  //udp discovery
  public final MessageCount discoverInPing = new MessageCount();
  public final MessageCount discoverOutPing = new MessageCount();
  public final MessageCount discoverInPong = new MessageCount();
  public final MessageCount discoverOutPong = new MessageCount();
  public final MessageCount discoverInFindNode = new MessageCount();
  public final MessageCount discoverOutFindNode = new MessageCount();
  public final MessageCount discoverInNeighbours = new MessageCount();
  public final MessageCount discoverOutNeighbours = new MessageCount();

  //tcp p2p
  public final MessageCount p2pInHello = new MessageCount();
  public final MessageCount p2pOutHello = new MessageCount();
  public final MessageCount p2pInPing = new MessageCount();
  public final MessageCount p2pOutPing = new MessageCount();
  public final MessageCount p2pInPong = new MessageCount();
  public final MessageCount p2pOutPong = new MessageCount();
  public final MessageCount p2pInDisconnect = new MessageCount();
  public final MessageCount p2pOutDisconnect = new MessageCount();

  //tcp mdc
  public final MessageCount mdcInMessage = new MessageCount();
  public final MessageCount mdcOutMessage = new MessageCount();

  public final MessageCount mdcInSyncBlockChain = new MessageCount();
  public final MessageCount mdcOutSyncBlockChain = new MessageCount();
  public final MessageCount mdcInBlockChainInventory = new MessageCount();
  public final MessageCount mdcOutBlockChainInventory = new MessageCount();

  public final MessageCount mdcInTrxInventory = new MessageCount();
  public final MessageCount mdcOutTrxInventory = new MessageCount();
  public final MessageCount mdcInTrxInventoryElement = new MessageCount();
  public final MessageCount mdcOutTrxInventoryElement = new MessageCount();

  public final MessageCount mdcInBlockInventory = new MessageCount();
  public final MessageCount mdcOutBlockInventory = new MessageCount();
  public final MessageCount mdcInBlockInventoryElement = new MessageCount();
  public final MessageCount mdcOutBlockInventoryElement = new MessageCount();

  public final MessageCount mdcInTrxFetchInvData = new MessageCount();
  public final MessageCount mdcOutTrxFetchInvData = new MessageCount();
  public final MessageCount mdcInTrxFetchInvDataElement = new MessageCount();
  public final MessageCount mdcOutTrxFetchInvDataElement = new MessageCount();

  public final MessageCount mdcInBlockFetchInvData = new MessageCount();
  public final MessageCount mdcOutBlockFetchInvData = new MessageCount();
  public final MessageCount mdcInBlockFetchInvDataElement = new MessageCount();
  public final MessageCount mdcOutBlockFetchInvDataElement = new MessageCount();


  public final MessageCount mdcInTrx = new MessageCount();
  public final MessageCount mdcOutTrx = new MessageCount();
  public final MessageCount mdcInTrxs = new MessageCount();
  public final MessageCount mdcOutTrxs = new MessageCount();
  public final MessageCount mdcInBlock = new MessageCount();
  public final MessageCount mdcOutBlock = new MessageCount();
  public final MessageCount mdcOutAdvBlock = new MessageCount();

  public void addUdpInMessage(UdpMessageTypeEnum type) {
    addUdpMessage(type, true);
  }

  public void addUdpOutMessage(UdpMessageTypeEnum type) {
    addUdpMessage(type, false);
  }

  public void addTcpInMessage(Message msg) {
    addTcpMessage(msg, true);
  }

  public void addTcpOutMessage(Message msg) {
    addTcpMessage(msg, false);
  }

  private void addUdpMessage(UdpMessageTypeEnum type, boolean flag) {
    switch (type) {
      case DISCOVER_PING:
        if (flag) {
          discoverInPing.add();
        } else {
          discoverOutPing.add();
        }
        break;
      case DISCOVER_PONG:
        if (flag) {
          discoverInPong.add();
        } else {
          discoverOutPong.add();
        }
        break;
      case DISCOVER_FIND_NODE:
        if (flag) {
          discoverInFindNode.add();
        } else {
          discoverOutFindNode.add();
        }
        break;
      case DISCOVER_NEIGHBORS:
        if (flag) {
          discoverInNeighbours.add();
        } else {
          discoverOutNeighbours.add();
        }
        break;
      default:
        break;
    }
  }

  private void addTcpMessage(Message msg, boolean flag) {

    if (flag) {
      mdcInMessage.add();
    } else {
      mdcOutMessage.add();
    }

    switch (msg.getType()) {
      case P2P_HELLO:
        if (flag) {
          p2pInHello.add();
        } else {
          p2pOutHello.add();
        }
        break;
      case P2P_PING:
        if (flag) {
          p2pInPing.add();
        } else {
          p2pOutPing.add();
        }
        break;
      case P2P_PONG:
        if (flag) {
          p2pInPong.add();
        } else {
          p2pOutPong.add();
        }
        break;
      case P2P_DISCONNECT:
        if (flag) {
          p2pInDisconnect.add();
        } else {
          p2pOutDisconnect.add();
        }
        break;
      case SYNC_BLOCK_CHAIN:
        if (flag) {
          mdcInSyncBlockChain.add();
        } else {
          mdcOutSyncBlockChain.add();
        }
        break;
      case BLOCK_CHAIN_INVENTORY:
        if (flag) {
          mdcInBlockChainInventory.add();
        } else {
          mdcOutBlockChainInventory.add();
        }
        break;
      case INVENTORY:
        InventoryMessage inventoryMessage = (InventoryMessage) msg;
        int inventorySize = inventoryMessage.getInventory().getIdsCount();
        if (flag) {
          if (inventoryMessage.getInvMessageType() == MessageTypes.TRX) {
            mdcInTrxInventory.add();
            mdcInTrxInventoryElement.add(inventorySize);
          } else {
            mdcInBlockInventory.add();
            mdcInBlockInventoryElement.add(inventorySize);
          }
        } else {
          if (inventoryMessage.getInvMessageType() == MessageTypes.TRX) {
            mdcOutTrxInventory.add();
            mdcOutTrxInventoryElement.add(inventorySize);
          } else {
            mdcOutBlockInventory.add();
            mdcOutBlockInventoryElement.add(inventorySize);
          }
        }
        break;
      case FETCH_INV_DATA:
        FetchInvDataMessage fetchInvDataMessage = (FetchInvDataMessage) msg;
        int fetchSize = fetchInvDataMessage.getInventory().getIdsCount();
        if (flag) {
          if (fetchInvDataMessage.getInvMessageType() == MessageTypes.TRX) {
            mdcInTrxFetchInvData.add();
            mdcInTrxFetchInvDataElement.add(fetchSize);
          } else {
            mdcInBlockFetchInvData.add();
            mdcInBlockFetchInvDataElement.add(fetchSize);
          }
        } else {
          if (fetchInvDataMessage.getInvMessageType() == MessageTypes.TRX) {
            mdcOutTrxFetchInvData.add();
            mdcOutTrxFetchInvDataElement.add(fetchSize);
          } else {
            mdcOutBlockFetchInvData.add();
            mdcOutBlockFetchInvDataElement.add(fetchSize);
          }
        }
        break;
      case TRXS:
        TransactionsMessage transactionsMessage = (TransactionsMessage) msg;
        if (flag) {
          mdcInTrxs.add();
          mdcInTrx.add(transactionsMessage.getTransactions().getTransactionsCount());
        } else {
          mdcOutTrxs.add();
          mdcOutTrx.add(transactionsMessage.getTransactions().getTransactionsCount());
        }
        break;
      case TRX:
        if (flag) {
          mdcInMessage.add();
        } else {
          mdcOutMessage.add();
        }
        break;
      case BLOCK:
        if (flag) {
          mdcInBlock.add();
        }
        mdcOutBlock.add();
        break;
      default:
        break;
    }
  }

}
