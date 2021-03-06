package org.mdc.core.net.message;

import org.mdc.common.utils.Sha256Hash;
import org.mdc.protos.Protocol.Inventory;
import org.mdc.protos.Protocol.Inventory.InventoryType;

import java.util.List;

public class TransactionInventoryMessage extends InventoryMessage {

  public TransactionInventoryMessage(byte[] packed) throws Exception {
    super(packed);
  }

  public TransactionInventoryMessage(Inventory inv) {
    super(inv);
  }

  public TransactionInventoryMessage(List<Sha256Hash> hashList) {
    super(hashList, InventoryType.TRX);
  }
}
