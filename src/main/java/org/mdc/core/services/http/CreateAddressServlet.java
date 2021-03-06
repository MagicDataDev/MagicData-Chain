package org.mdc.core.services.http;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.mdc.api.GrpcAPI.BytesMessage;
import org.mdc.common.crypto.ECKey;
import org.mdc.common.utils.ByteArray;
import org.mdc.common.utils.Utils;
import org.mdc.core.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;


@Component
@Slf4j(topic = "API")
public class CreateAddressServlet extends HttpServlet {

  @Autowired
  private Wallet wallet;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    try {
      boolean visible = Util.getVisible(request);
      String input = request.getParameter("value");
      if (visible) {
        input = Util.getHexString(input);
      }
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("value", input);
      BytesMessage.Builder build = BytesMessage.newBuilder();
      JsonFormat.merge(jsonObject.toJSONString(), build, visible);
      byte[] address = wallet.createAdresss(build.getValue().toByteArray());
      String base58check = Wallet.encode58Check(address);
      String hexString = ByteArray.toHexString(address);
      JSONObject jsonAddress = new JSONObject();
      jsonAddress.put("base58checkAddress", base58check);
      jsonAddress.put("value", hexString);
      response.getWriter().println(jsonAddress.toJSONString());
    } catch (Exception e) {
      logger.debug("Exception: {}", e.getMessage());
      try {
        response.getWriter().println(Util.printErrorMsg(e));
      } catch (IOException ioe) {
        logger.debug("IOException: {}", ioe.getMessage());
      }
    }
  }

  private String covertStringToHex(String input) {
    JSONObject jsonObject = JSONObject.parseObject(input);
    String value = jsonObject.getString("value");
    jsonObject.put("value", Util.getHexString(value));
    return jsonObject.toJSONString();
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    try {
      String input = request.getReader().lines()
          .collect(Collectors.joining(System.lineSeparator()));
      Util.checkBodySize(input);
      boolean visible = Util.getVisiblePost(input);
      if (visible) {
        input = covertStringToHex(input);
      }
      BytesMessage.Builder build = BytesMessage.newBuilder();
      JsonFormat.merge(input, build, visible);
      byte[] address = wallet.createAdresss(build.getValue().toByteArray());
      String base58check = Wallet.encode58Check(address);
      String hexString = ByteArray.toHexString(address);
      JSONObject jsonAddress = new JSONObject();
      jsonAddress.put("base58checkAddress", base58check);
      jsonAddress.put("value", hexString);
      response.getWriter().println(jsonAddress.toJSONString());
    } catch (Exception e) {
      logger.debug("Exception: {}", e.getMessage());
      try {
        response.getWriter().println(Util.printErrorMsg(e));
      } catch (IOException ioe) {
        logger.debug("IOException: {}", ioe.getMessage());
      }
    }
  }

  public static void main(String args[]){
    String amount = "250000000000000000";
    Long a = Long.parseLong(amount);
    for (int i=0; i<100; ++i){
      ECKey ecKey = new ECKey(Utils.getRandom());
      byte[] priKey = ecKey.getPrivKeyBytes();
      byte[] address = ecKey.getAddress();
      String priKeyStr = Hex.encodeHexString(priKey);
      String base58check = Wallet.encode58Check(address);
      String hexString = ByteArray.toHexString(address);
      JSONObject jsonAddress = new JSONObject();
      jsonAddress.put("address", base58check);
      jsonAddress.put("hexAddress", hexString);
      jsonAddress.put("privateKey", priKeyStr);
      if (!base58check.startsWith("M")){
        System.err.println(i + ":" + base58check);
        break;
      }
      System.out.println(i + ":" + base58check + "    " + priKeyStr + "  " + hexString);
    }
  }
}
