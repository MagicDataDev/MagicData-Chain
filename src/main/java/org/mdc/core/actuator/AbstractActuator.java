package org.mdc.core.actuator;

import com.google.protobuf.Any;
import org.mdc.common.storage.Deposit;
import org.mdc.core.db.Manager;

public abstract class AbstractActuator implements Actuator {

  protected Any contract;
  protected Manager dbManager;

  public Deposit getDeposit() {
    return deposit;
  }

  public void setDeposit(Deposit deposit) {
    this.deposit = deposit;
  }

  protected Deposit deposit;

  AbstractActuator(Any contract, Manager dbManager) {
    this.contract = contract;
    this.dbManager = dbManager;
  }
}
