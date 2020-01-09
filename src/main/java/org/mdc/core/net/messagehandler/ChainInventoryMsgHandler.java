package org.mdc.core.net.messagehandler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.mdc.core.capsule.BlockCapsule.BlockId;
import org.mdc.core.config.Parameter.ChainConstant;
import org.mdc.core.config.Parameter.NodeConstant;
import org.mdc.core.exception.P2pException;
import org.mdc.core.exception.P2pException.TypeEnum;
import org.mdc.core.net.MdcNetDelegate;
import org.mdc.core.net.message.ChainInventoryMessage;
import org.mdc.core.net.message.MdcMessage;
import org.mdc.core.net.peer.PeerConnection;
import org.mdc.core.net.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import static org.mdc.core.config.Parameter.ChainConstant.BLOCK_PRODUCED_INTERVAL;

@Slf4j(topic = "net")
@Component
public class ChainInventoryMsgHandler implements MdcMsgHandler {

  @Autowired
  private MdcNetDelegate MdcNetDelegate;

  @Autowired
  private SyncService syncService;

  @Override
  public void processMessage(PeerConnection peer, MdcMessage msg) throws P2pException {

    ChainInventoryMessage chainInventoryMessage = (ChainInventoryMessage) msg;

    check(peer, chainInventoryMessage);

    peer.setNeedSyncFromPeer(true);

    peer.setSyncChainRequested(null); //todo thread sec

    Deque<BlockId> blockIdWeGet = new LinkedList<>(chainInventoryMessage.getBlockIds());

    if (blockIdWeGet.size() == 1 && MdcNetDelegate.containBlock(blockIdWeGet.peek())) {
      peer.setNeedSyncFromPeer(false);
      return;
    }

    while (!peer.getSyncBlockToFetch().isEmpty()) {
      if (peer.getSyncBlockToFetch().peekLast().equals(blockIdWeGet.peekFirst())) {
        break;
      }
      peer.getSyncBlockToFetch().pollLast();
    }

    blockIdWeGet.poll();

    peer.setRemainNum(chainInventoryMessage.getRemainNum());
    peer.getSyncBlockToFetch().addAll(blockIdWeGet);

    synchronized (MdcNetDelegate.getBlockLock()) {
      while (!peer.getSyncBlockToFetch().isEmpty() && MdcNetDelegate
          .containBlock(peer.getSyncBlockToFetch().peek())) {
        BlockId blockId = peer.getSyncBlockToFetch().pop();
        peer.setBlockBothHave(blockId);
        logger.info("Block {} from {} is processed", blockId.getString(), peer.getNode().getHost());
      }
    }

    //if (chainInventoryMessage.getRemainNum() == 0 && peer.getSyncBlockToFetch().isEmpty()) {
    //  peer.setNeedSyncFromPeer(false);
    //}

    if ((chainInventoryMessage.getRemainNum() == 0 && !peer.getSyncBlockToFetch().isEmpty()) ||
        (chainInventoryMessage.getRemainNum() != 0
            && peer.getSyncBlockToFetch().size() > NodeConstant.SYNC_FETCH_BATCH_NUM)) {
      syncService.setFetchFlag(true);
    } else {
      syncService.syncNext(peer);
    }
  }

  private void check(PeerConnection peer, ChainInventoryMessage msg) throws P2pException {
    if (peer.getSyncChainRequested() == null) {
      throw new P2pException(TypeEnum.BAD_MESSAGE, "not send syncBlockChainMsg");
    }

    List<BlockId> blockIds = msg.getBlockIds();
    if (CollectionUtils.isEmpty(blockIds)) {
      throw new P2pException(TypeEnum.BAD_MESSAGE, "blockIds is empty");
    }

    if (blockIds.size() > NodeConstant.SYNC_FETCH_BATCH_NUM + 1) {
      throw new P2pException(TypeEnum.BAD_MESSAGE, "big blockIds size: " + blockIds.size());
    }

    if (msg.getRemainNum() != 0 && blockIds.size() < NodeConstant.SYNC_FETCH_BATCH_NUM) {
      throw new P2pException(TypeEnum.BAD_MESSAGE,
          "remain: " + msg.getRemainNum() + ", blockIds size: " + blockIds.size());
    }

    long num = blockIds.get(0).getNum();
    for (BlockId id : msg.getBlockIds()) {
      if (id.getNum() != num++) {
        throw new P2pException(TypeEnum.BAD_MESSAGE, "not continuous block");
      }
    }

    if (!peer.getSyncChainRequested().getKey().contains(blockIds.get(0))) {
      throw new P2pException(TypeEnum.BAD_MESSAGE, "unlinked block, my head: "
          + peer.getSyncChainRequested().getKey().getLast().getString()
          + ", peer: " + blockIds.get(0).getString());
    }

    if (MdcNetDelegate.getHeadBlockId().getNum() > 0) {
      long maxRemainTime =
          ChainConstant.CLOCK_MAX_DELAY + System.currentTimeMillis() - MdcNetDelegate
              .getBlockTime(MdcNetDelegate.getSolidBlockId());
      long maxFutureNum =
          maxRemainTime / BLOCK_PRODUCED_INTERVAL + MdcNetDelegate.getSolidBlockId().getNum();
      long lastNum = blockIds.get(blockIds.size() - 1).getNum();
      if (lastNum + msg.getRemainNum() > maxFutureNum) {
        throw new P2pException(TypeEnum.BAD_MESSAGE, "lastNum: " + lastNum + " + remainNum: "
            + msg.getRemainNum() + " > futureMaxNum: " + maxFutureNum);
      }
    }
  }

}
