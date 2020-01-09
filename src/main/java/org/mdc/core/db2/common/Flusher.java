package org.mdc.core.db2.common;

import org.mdc.core.db.common.WrappedByteArray;

import java.util.Map;

public interface Flusher {

  void flush(Map<WrappedByteArray, WrappedByteArray> batch);

  void close();

  void reset();
}
