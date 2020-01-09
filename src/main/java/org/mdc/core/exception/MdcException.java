package org.mdc.core.exception;

public class MdcException extends Exception {

  public MdcException() {
    super();
  }

  public MdcException(String message) {
    super(message);
  }

  public MdcException(String message, Throwable cause) {
    super(message, cause);
  }

}
