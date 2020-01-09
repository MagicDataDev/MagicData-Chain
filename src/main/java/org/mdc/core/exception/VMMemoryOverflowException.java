package org.mdc.core.exception;

public class VMMemoryOverflowException extends MdcException {

  public VMMemoryOverflowException() {
    super("VM memory overflow");
  }

  public VMMemoryOverflowException(String message) {
    super(message);
  }

}
