package org.mdc.core.db;

import org.apache.commons.lang3.ArrayUtils;
import org.mdc.core.capsule.DelegatedResourceCapsule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class DelegatedResourceStore extends MdcStoreWithRevoking<DelegatedResourceCapsule> {

  @Autowired
  public DelegatedResourceStore(@Value("DelegatedResource") String dbName) {
    super(dbName);
  }

  @Override
  public DelegatedResourceCapsule get(byte[] key) {

    byte[] value = revokingDB.getUnchecked(key);
    return ArrayUtils.isEmpty(value) ? null : new DelegatedResourceCapsule(value);
  }

  @Deprecated
  public List<DelegatedResourceCapsule> getByFrom(byte[] key) {
    return revokingDB.getValuesNext(key, Long.MAX_VALUE).stream()
        .map(DelegatedResourceCapsule::new)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

}