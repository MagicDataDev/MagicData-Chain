package org.mdc.core.services.http;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.mdc.core.Wallet;
import org.mdc.core.capsule.TransactionCapsule;
import org.mdc.protos.Protocol.Transaction;
import org.mdc.protos.Protocol.TransactionSign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;


@Component
@Slf4j(topic = "API")
public class AddTransactionSignServlet extends HttpServlet {

  @Autowired
  private Wallet wallet;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) {

  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    try {
      String contract = request.getReader().lines()
          .collect(Collectors.joining(System.lineSeparator()));
      Util.checkBodySize(contract);
      JSONObject input = JSONObject.parseObject(contract);
      boolean visible =  Util.getVisibleOnlyForSign(input);
      String strTransaction = input.getJSONObject("transaction").toJSONString();
      Transaction transaction = Util.packTransaction(strTransaction, visible);
      JSONObject jsonTransaction = JSONObject.parseObject(JsonFormat.printToString(transaction,
          visible));
      input.put("transaction", jsonTransaction);
      TransactionSign.Builder build = TransactionSign.newBuilder();
      JsonFormat.merge(input.toJSONString(), build, visible);
      TransactionCapsule reply = wallet.addSign(build.build());
      if (reply != null) {
        response.getWriter().println(Util.printCreateTransaction(reply.getInstance(), visible));
      } else {
        response.getWriter().println("{}");
      }
    } catch (Exception e) {
      logger.debug("Exception: {}", e.getMessage());
      try {
        response.getWriter().println(Util.printErrorMsg(e));
      } catch (IOException ioe) {
        logger.debug("IOException: {}", ioe.getMessage());
      }
    }
  }
}
