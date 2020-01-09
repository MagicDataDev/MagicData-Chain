package org.mdc.common.logsfilter.capsule;

import lombok.Getter;
import lombok.Setter;
import org.mdc.common.logsfilter.EventPluginLoader;
import org.mdc.common.logsfilter.FilterQuery;
import org.mdc.common.logsfilter.trigger.ContractLogTrigger;

public class ContractLogTriggerCapsule extends TriggerCapsule {

  @Getter
  @Setter
  ContractLogTrigger contractLogTrigger;

  public ContractLogTriggerCapsule(ContractLogTrigger contractLogTrigger) {
    this.contractLogTrigger = contractLogTrigger;
  }

  public void setLatestSolidifiedBlockNumber(long latestSolidifiedBlockNumber) {
    contractLogTrigger.setLatestSolidifiedBlockNumber(latestSolidifiedBlockNumber);
  }

  @Override
  public void processTrigger() {
    if (FilterQuery.matchFilter(contractLogTrigger)) {
      EventPluginLoader.getInstance().postContractLogTrigger(contractLogTrigger);
    }
  }
}
