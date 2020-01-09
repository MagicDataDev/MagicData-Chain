package org.mdc.core.exception;

public class AccountResourceInsufficientException extends MdcException {

  public AccountResourceInsufficientException() {
    super("Insufficient bandwidth and balance to create new account");
  }

  public AccountResourceInsufficientException(String message) {
    super(message);
  }
}

