package org.mdc.core.db;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.mdc.common.storage.DbSourceInter;
import org.mdc.common.storage.leveldb.LevelDbDataSourceImpl;
import org.mdc.common.storage.leveldb.RocksDbDataSourceImpl;
import org.mdc.core.config.args.Args;
import org.mdc.core.db.api.IndexHelper;
import org.mdc.core.db2.core.IMdcChainBase;
import org.mdc.core.exception.BadItemException;
import org.mdc.core.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map.Entry;

@Slf4j(topic = "DB")
public abstract class MdcDatabase<T> implements IMdcChainBase<T> {

  protected DbSourceInter<byte[]> dbSource;
  @Getter
  private String dbName;

  @Autowired(required = false)
  protected IndexHelper indexHelper;

  protected MdcDatabase(String dbName) {
    this.dbName = dbName;

    if ("LEVELDB".equals(Args.getInstance().getStorage().getDbEngine().toUpperCase())) {
      dbSource =
          new LevelDbDataSourceImpl(Args.getInstance().getOutputDirectoryByDbName(dbName), dbName);
    } else if ("ROCKSDB".equals(Args.getInstance().getStorage().getDbEngine().toUpperCase())) {
      String parentName = Paths.get(Args.getInstance().getOutputDirectoryByDbName(dbName),
          Args.getInstance().getStorage().getDbDirectory()).toString();
      dbSource =
          new RocksDbDataSourceImpl(parentName, dbName);
    }

    dbSource.initDB();
  }

  protected MdcDatabase() {
  }

  public DbSourceInter<byte[]> getDbSource() {
    return dbSource;
  }

  /**
   * reset the database.
   */
  public void reset() {
    dbSource.resetDb();
  }

  /**
   * close the database.
   */
  @Override
  public void close() {
    dbSource.closeDB();
  }

  public abstract void put(byte[] key, T item);

  public abstract void delete(byte[] key);

  public abstract T get(byte[] key)
      throws InvalidProtocolBufferException, ItemNotFoundException, BadItemException;

  public T getUnchecked(byte[] key) {
    return null;
  }

  public abstract boolean has(byte[] key);

  public String getName() {
    return this.getClass().getSimpleName();
  }

  @Override
  public Iterator<Entry<byte[], T>> iterator() {
    throw new UnsupportedOperationException();
  }
}
