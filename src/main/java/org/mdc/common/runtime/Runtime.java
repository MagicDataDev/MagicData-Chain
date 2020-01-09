package org.mdc.common.runtime;

import org.mdc.common.runtime.vm.program.InternalTransaction.TrxType;
import org.mdc.common.runtime.vm.program.ProgramResult;
import org.mdc.core.exception.ContractExeException;
import org.mdc.core.exception.ContractValidateException;
import org.mdc.core.exception.VMIllegalException;


public interface Runtime {

  void execute() throws ContractValidateException, ContractExeException, VMIllegalException;

  void go();

  TrxType getTrxType();

  void finalization();

  ProgramResult getResult();

  String getRuntimeError();

  void setEnableEventLinstener(boolean enableEventLinstener);
}
