package org.mdc.core.net.messagehandler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.mdc.core.config.args.Args;
import org.mdc.core.exception.P2pException;
import org.mdc.core.exception.P2pException.TypeEnum;
import org.mdc.core.net.MdcNetDelegate;
import org.mdc.core.net.message.TransactionMessage;
import org.mdc.core.net.message.TransactionsMessage;
import org.mdc.core.net.message.MdcMessage;
import org.mdc.core.net.peer.Item;
import org.mdc.core.net.peer.PeerConnection;
import org.mdc.core.net.service.AdvService;
import org.mdc.protos.Protocol.Inventory.InventoryType;
import org.mdc.protos.Protocol.ReasonCode;
import org.mdc.protos.Protocol.Transaction;
import org.mdc.protos.Protocol.Transaction.Contract.ContractType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Slf4j(topic = "net")
@Component
public class TransactionsMsgHandler implements MdcMsgHandler {

  @Autowired
  private MdcNetDelegate MdcNetDelegate;

  @Autowired
  private AdvService advService;

  private static int MAX_TRX_SIZE = 50_000;

  private static int MAX_SMART_CONTRACT_SUBMIT_SIZE = 100;

//  private static int TIME_OUT = 10 * 60 * 1000;

  private BlockingQueue<TrxEvent> smartContractQueue = new LinkedBlockingQueue(MAX_TRX_SIZE);

  private BlockingQueue<Runnable> queue = new LinkedBlockingQueue();

  private int threadNum = Args.getInstance().getValidateSignThreadNum();
  private ExecutorService trxHandlePool = new ThreadPoolExecutor(threadNum, threadNum, 0L,
      TimeUnit.MILLISECONDS, queue);

  private ScheduledExecutorService smartContractExecutor = Executors
      .newSingleThreadScheduledExecutor();

  class TrxEvent {

    @Getter
    private PeerConnection peer;
    @Getter
    private TransactionMessage msg;
    @Getter
    private long time;

    public TrxEvent(PeerConnection peer, TransactionMessage msg) {
      this.peer = peer;
      this.msg = msg;
      this.time = System.currentTimeMillis();
    }
  }

  public void init() {
    handleSmartContract();
  }

  public void close() {
    smartContractExecutor.shutdown();
  }

  public boolean isBusy() {
    return queue.size() + smartContractQueue.size() > MAX_TRX_SIZE;
  }

  @Override
  public void processMessage(PeerConnection peer, MdcMessage msg) throws P2pException {
    TransactionsMessage transactionsMessage = (TransactionsMessage) msg;
    check(peer, transactionsMessage);
    for (Transaction trx : transactionsMessage.getTransactions().getTransactionsList()) {
      int type = trx.getRawData().getContract(0).getType().getNumber();
      if (type == ContractType.TriggerSmartContract_VALUE
          || type == ContractType.CreateSmartContract_VALUE) {
        if (!smartContractQueue.offer(new TrxEvent(peer, new TransactionMessage(trx)))) {
          logger.warn("Add smart contract failed, queueSize {}:{}", smartContractQueue.size(),
              queue.size());
        }
      } else {
        trxHandlePool.submit(() -> handleTransaction(peer, new TransactionMessage(trx)));
      }
    }
  }

  private void check(PeerConnection peer, TransactionsMessage msg) throws P2pException {
    for (Transaction trx : msg.getTransactions().getTransactionsList()) {
      Item item = new Item(new TransactionMessage(trx).getMessageId(), InventoryType.TRX);
      if (!peer.getAdvInvRequest().containsKey(item)) {
        throw new P2pException(TypeEnum.BAD_MESSAGE,
            "trx: " + msg.getMessageId() + " without request.");
      }
      peer.getAdvInvRequest().remove(item);
    }
  }

  private void handleSmartContract() {
    smartContractExecutor.scheduleWithFixedDelay(() -> {
      try {
        while (queue.size() < MAX_SMART_CONTRACT_SUBMIT_SIZE) {
          TrxEvent event = smartContractQueue.take();
          trxHandlePool.submit(() -> handleTransaction(event.getPeer(), event.getMsg()));
        }
      } catch (Exception e) {
        logger.error("Handle smart contract exception.", e);
      }
    }, 1000, 20, TimeUnit.MILLISECONDS);
  }

  private void handleTransaction(PeerConnection peer, TransactionMessage trx) {
    if (peer.isDisconnect()) {
      logger.warn("Drop trx {} from {}, peer is disconnect.", trx.getMessageId(),
          peer.getInetAddress());
      return;
    }

    if (advService.getMessage(new Item(trx.getMessageId(), InventoryType.TRX)) != null) {
      return;
    }

    try {
      MdcNetDelegate.pushTransaction(trx.getTransactionCapsule());
      advService.broadcast(trx);
    } catch (P2pException e) {
      logger.warn("Trx {} from peer {} process failed. type: {}, reason: {}",
          trx.getMessageId(), peer.getInetAddress(), e.getType(), e.getMessage());
      if (e.getType().equals(TypeEnum.BAD_TRX)) {
        peer.disconnect(ReasonCode.BAD_TX);
      }
    } catch (Exception e) {
      logger.error("Trx {} from peer {} process failed.", trx.getMessageId(), peer.getInetAddress(),
          e);
    }
  }
}