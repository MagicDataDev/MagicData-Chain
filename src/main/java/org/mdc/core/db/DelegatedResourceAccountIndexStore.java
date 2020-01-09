package org.mdc.core.db;

import org.apache.commons.lang3.ArrayUtils;
import org.mdc.core.capsule.DelegatedResourceAccountIndexCapsule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DelegatedResourceAccountIndexStore extends
        TronStoreWithRevoking<DelegatedResourceAccountIndexCapsule> {

  @Autowired
  public DelegatedResourceAccountIndexStore(@Value("DelegatedResourceAccountIndex") String dbName) {
    super(dbName);
  }

  @Override
  public DelegatedResourceAccountIndexCapsule get(byte[] key) {

    byte[] value = revokingDB.getUnchecked(key);
    return ArrayUtils.isEmpty(value) ? null : new DelegatedResourceAccountIndexCapsule(value);
  }

}