package org.mdc.core.exception;

public class NonCommonBlockException extends MdcException {

  public NonCommonBlockException() {
    super();
  }

  public NonCommonBlockException(String message) {
    super(message);
  }

  public NonCommonBlockException(String message, Throwable cause) {
    super(message, cause);
  }
}
