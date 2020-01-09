package org.mdc.core.net.messagehandler;

import lombok.extern.slf4j.Slf4j;
import org.mdc.core.capsule.BlockCapsule;
import org.mdc.core.capsule.BlockCapsule.BlockId;
import org.mdc.core.config.args.Args;
import org.mdc.core.exception.P2pException;
import org.mdc.core.exception.P2pException.TypeEnum;
import org.mdc.core.net.MdcNetDelegate;
import org.mdc.core.net.message.BlockMessage;
import org.mdc.core.net.message.MdcMessage;
import org.mdc.core.net.peer.Item;
import org.mdc.core.net.peer.PeerConnection;
import org.mdc.core.net.service.AdvService;
import org.mdc.core.net.service.SyncService;
import org.mdc.core.services.WitnessProductBlockService;
import org.mdc.protos.Protocol.Inventory.InventoryType;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.mdc.core.config.Parameter.ChainConstant.BLOCK_PRODUCED_INTERVAL;
import static org.mdc.core.config.Parameter.ChainConstant.BLOCK_SIZE;

@Slf4j(topic = "net")
@Component
public class BlockMsgHandler implements MdcMsgHandler {

  @Autowired
  private MdcNetDelegate MdcNetDelegate;

  @Autowired
  private AdvService advService;

  @Autowired
  private SyncService syncService;

  @Autowired
  private WitnessProductBlockService witnessProductBlockService;

  private int maxBlockSize = BLOCK_SIZE + 1000;

  private boolean fastForward = Args.getInstance().isFastForward();

  @Override
  public void processMessage(PeerConnection peer, MdcMessage msg) throws P2pException {

    BlockMessage blockMessage = (BlockMessage) msg;
    BlockId blockId = blockMessage.getBlockId();

    if (!fastForward && !peer.isFastForwardPeer()) {
      check(peer, blockMessage);
    }

    if (peer.getSyncBlockRequested().containsKey(blockId)) {
      peer.getSyncBlockRequested().remove(blockId);
      syncService.processBlock(peer, blockMessage);
    } else {
      Long time = peer.getAdvInvRequest().remove(new Item(blockId, InventoryType.BLOCK));
      long now = System.currentTimeMillis();
      long interval = blockId.getNum() - MdcNetDelegate.getHeadBlockId().getNum();
      processBlock(peer, blockMessage.getBlockCapsule());
      logger.info(
          "Receive block/interval {}/{} from {} fetch/delay {}/{}ms, txs/process {}/{}ms, witness: {}",
          blockId.getNum(),
          interval,
          peer.getInetAddress(),
          time == null ? 0 : now - time,
          now - blockMessage.getBlockCapsule().getTimeStamp(),
          ((BlockMessage) msg).getBlockCapsule().getTransactions().size(),
          System.currentTimeMillis() - now,
          Hex.toHexString(blockMessage.getBlockCapsule().getWitnessAddress().toByteArray()));
    }
  }

  private void check(PeerConnection peer, BlockMessage msg) throws P2pException {
    Item item = new Item(msg.getBlockId(), InventoryType.BLOCK);
    if (!peer.getSyncBlockRequested().containsKey(msg.getBlockId()) && !peer.getAdvInvRequest()
        .containsKey(item)) {
      throw new P2pException(TypeEnum.BAD_MESSAGE, "no request");
    }
    BlockCapsule blockCapsule = msg.getBlockCapsule();
    if (blockCapsule.getInstance().getSerializedSize() > maxBlockSize) {
      throw new P2pException(TypeEnum.BAD_MESSAGE, "block size over limit");
    }
    long gap = blockCapsule.getTimeStamp() - System.currentTimeMillis();
    if (gap >= BLOCK_PRODUCED_INTERVAL) {
      throw new P2pException(TypeEnum.BAD_MESSAGE, "block time error");
    }
  }

  private void processBlock(PeerConnection peer, BlockCapsule block) throws P2pException {
    BlockId blockId = block.getBlockId();
    if (!MdcNetDelegate.containBlock(block.getParentBlockId())) {
      logger.warn("Get unlink block {} from {}, head is {}.", blockId.getString(),
          peer.getInetAddress(), MdcNetDelegate.getHeadBlockId().getString());
      syncService.startSync(peer);
      return;
    }

    Item item = new Item(blockId, InventoryType.BLOCK);
    if (fastForward || peer.isFastForwardPeer()) {
      peer.getAdvInvReceive().put(item, System.currentTimeMillis());
      advService.addInvToCache(item);
    }

    if (fastForward) {
      if (block.getNum() < MdcNetDelegate.getHeadBlockId().getNum()) {
        logger.warn("Receive a low block {}, head {}",
            blockId.getString(), MdcNetDelegate.getHeadBlockId().getString());
        return;
      }
      if (MdcNetDelegate.validBlock(block)) {
        advService.fastForward(new BlockMessage(block));
        MdcNetDelegate.trustNode(peer);
      }
    }

    MdcNetDelegate.processBlock(block);
    witnessProductBlockService.validWitnessProductTwoBlock(block);
    MdcNetDelegate.getActivePeer().forEach(p -> {
      if (p.getAdvInvReceive().getIfPresent(blockId) != null) {
        p.setBlockBothHave(blockId);
      }
    });

    if (!fastForward) {
      advService.broadcast(new BlockMessage(block));
    }
  }

}
