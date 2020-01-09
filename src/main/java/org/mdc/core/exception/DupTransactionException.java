package org.mdc.core.exception;

public class DupTransactionException extends MdcException {

  public DupTransactionException() {
    super();
  }

  public DupTransactionException(String message) {
    super(message);
  }
}
