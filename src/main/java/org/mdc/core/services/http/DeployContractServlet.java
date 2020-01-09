package org.mdc.core.services.http;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.mdc.common.utils.ByteArray;
import org.mdc.core.Wallet;
import org.mdc.protos.Contract.CreateSmartContract;
import org.mdc.protos.Protocol.SmartContract;
import org.mdc.protos.Protocol.SmartContract.ABI;
import org.mdc.protos.Protocol.Transaction;
import org.mdc.protos.Protocol.Transaction.Contract.ContractType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

import static org.mdc.core.services.http.Util.*;


@Component
@Slf4j(topic = "API")
public class DeployContractServlet extends HttpServlet {

  @Autowired
  private Wallet wallet;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    try {
      String contract = request.getReader().lines()
          .collect(Collectors.joining(System.lineSeparator()));
      Util.checkBodySize(contract);
      boolean visible = getVisiblePost(contract);
      CreateSmartContract.Builder build = CreateSmartContract.newBuilder();
      JSONObject jsonObject = JSONObject.parseObject(contract);
      String owner_address = jsonObject.getString("owner_address");
      if (visible) {
        owner_address = getHexAddress(owner_address);
      }
      byte[] ownerAddress = ByteArray.fromHexString(owner_address);
      build.setOwnerAddress(ByteString.copyFrom(ownerAddress));
      build.setCallTokenValue(Util.getJsonLongValue(jsonObject, "call_token_value"))
          .setTokenId(Util.getJsonLongValue(jsonObject, "token_id"));

      ABI.Builder abiBuilder = ABI.newBuilder();
      if (jsonObject.containsKey("abi")) {
        String abi = jsonObject.getString("abi");
        StringBuffer abiSB = new StringBuffer("{");
        abiSB.append("\"entrys\":");
        abiSB.append(abi);
        abiSB.append("}");
        JsonFormat.merge(abiSB.toString(), abiBuilder, visible);
      }

      SmartContract.Builder smartBuilder = SmartContract.newBuilder();
      smartBuilder
          .setAbi(abiBuilder)
          .setCallValue(Util.getJsonLongValue(jsonObject, "call_value"))
          .setConsumeUserResourcePercent(Util.getJsonLongValue(jsonObject,
              "consume_user_resource_percent"))
          .setOriginEnergyLimit(Util.getJsonLongValue(jsonObject, "origin_energy_limit"));
      if (!ArrayUtils.isEmpty(ownerAddress)) {
        smartBuilder.setOriginAddress(ByteString.copyFrom(ownerAddress));
      }

      String jsonByteCode = jsonObject.getString("bytecode");
      if (jsonObject.containsKey("parameter")) {
        jsonByteCode += jsonObject.getString("parameter");
      }
      byte[] byteCode = ByteArray.fromHexString(jsonByteCode);
      if (!ArrayUtils.isEmpty(byteCode)) {
        smartBuilder.setBytecode(ByteString.copyFrom(byteCode));
      }
      String name = jsonObject.getString("name");
      if (!Strings.isNullOrEmpty(name)) {
        smartBuilder.setName(name);
      }

      long feeLimit = Util.getJsonLongValue(jsonObject, "fee_limit");
      build.setNewContract(smartBuilder);
      Transaction tx = wallet
          .createTransactionCapsule(build.build(), ContractType.CreateSmartContract).getInstance();
      Transaction.Builder txBuilder = tx.toBuilder();
      Transaction.raw.Builder rawBuilder = tx.getRawData().toBuilder();
      rawBuilder.setFeeLimit(feeLimit);
      txBuilder.setRawData(rawBuilder);
      tx = setTransactionPermissionId(jsonObject, txBuilder.build());
      response.getWriter().println(Util.printCreateTransaction(tx, visible));
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