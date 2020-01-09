package org.mdc.core.services.interfaceOnSolidity.http.solidity;

import lombok.extern.slf4j.Slf4j;
import org.mdc.core.services.http.GetTransactionByIdServlet;
import org.mdc.core.services.interfaceOnSolidity.WalletOnSolidity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j(topic = "API")
public class GetTransactionByIdOnSolidityServlet
    extends GetTransactionByIdServlet {

  @Autowired
  private WalletOnSolidity walletOnSolidity;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    walletOnSolidity.futureGet(() -> super.doGet(request, response));
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    walletOnSolidity.futureGet(() -> super.doPost(request, response));
  }
}