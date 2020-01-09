package org.mdc.core.capsule;

public interface ProtoCapsule<T> {

  byte[] getData();

  T getInstance();
}
