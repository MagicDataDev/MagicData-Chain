package org.mdc.core.actuator;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.mdc.core.capsule.TransactionResultCapsule;
import org.mdc.core.exception.ContractExeException;
import org.mdc.core.exception.ContractValidateException;

public interface Actuator {

  boolean execute(TransactionResultCapsule result) throws ContractExeException;

  boolean validate() throws ContractValidateException;

  ByteString getOwnerAddress() throws InvalidProtocolBufferException;

  long calcFee();

}
