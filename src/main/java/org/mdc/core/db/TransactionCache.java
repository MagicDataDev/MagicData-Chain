package org.mdc.core.db;

import lombok.extern.slf4j.Slf4j;
import org.mdc.core.capsule.BytesCapsule;
import org.mdc.core.db2.common.TxCacheDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class TransactionCache extends TronStoreWithRevoking<BytesCapsule> {

  @Autowired
  public TransactionCache(@Value("trans-cache") String dbName) {
    super(dbName, TxCacheDB.class);
  }
}
