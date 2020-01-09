package org.mdc.core.exception;

public class TooBigTransactionResultException extends MdcException {

  public TooBigTransactionResultException() {
    super("too big transaction result");
  }

  public TooBigTransactionResultException(String message) {
    super(message);
  }
}
