package org.mdc.core.exception;

public class MdcRuntimeException extends RuntimeException {

  public MdcRuntimeException() {
    super();
  }

  public MdcRuntimeException(String message) {
    super(message);
  }

  public MdcRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public MdcRuntimeException(Throwable cause) {
    super(cause);
  }

  protected MdcRuntimeException(String message, Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }


}
