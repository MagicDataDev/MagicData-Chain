package org.mdc.core.services.interfaceOnSolidity.http;

import lombok.extern.slf4j.Slf4j;
import org.mdc.core.services.http.GetExchangeByIdServlet;
import org.mdc.core.services.interfaceOnSolidity.WalletOnSolidity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
@Slf4j(topic = "API")
public class GetExchangeByIdOnSolidityServlet
    extends GetExchangeByIdServlet {

  @Autowired
  private WalletOnSolidity walletOnSolidity;

  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    walletOnSolidity.futureGet(() -> super.doPost(request, response));
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    walletOnSolidity.futureGet(() -> super.doGet(request, response));
  }
}