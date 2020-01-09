package org.mdc.core.db.accountstate;

import com.google.protobuf.ByteString;
import com.google.protobuf.Internal;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.mdc.common.crypto.Hash;
import org.mdc.core.capsule.BlockCapsule;
import org.mdc.core.db.Manager;
import org.mdc.core.db.accountstate.storetrie.AccountStateStoreTrie;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j(topic = "AccountState")
@Component
public class TrieService {

  @Setter
  private Manager manager;

  @Setter
  private AccountStateStoreTrie accountStateStoreTrie;

  public byte[] getFullAccountStateRootHash() {
    long latestNumber = manager.getDynamicPropertiesStore().getLatestBlockHeaderNumber();
    return getAccountStateRootHash(latestNumber);
  }

  public byte[] getSolidityAccountStateRootHash() {
    long latestSolidityNumber = manager.getDynamicPropertiesStore().getLatestSolidifiedBlockNum();
    return getAccountStateRootHash(latestSolidityNumber);
  }

  private byte[] getAccountStateRootHash(long blockNumber) {
    long latestNumber = blockNumber;
    byte[] rootHash = null;
    try {
      BlockCapsule blockCapsule = manager.getBlockByNum(latestNumber);
      ByteString value = blockCapsule.getInstance().getBlockHeader().getRawData()
          .getAccountStateRoot();
      rootHash = value == null ? null : value.toByteArray();
      if (Arrays.equals(rootHash, Internal.EMPTY_BYTE_ARRAY)) {
        rootHash = Hash.EMPTY_TRIE_HASH;
      }
    } catch (Exception e) {
      logger.error("Get the {} block error.", latestNumber, e);
    }
    return rootHash;
  }
}
