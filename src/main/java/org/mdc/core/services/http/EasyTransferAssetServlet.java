package org.mdc.core.services.http;

import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.mdc.api.GrpcAPI;
import org.mdc.api.GrpcAPI.EasyTransferAssetMessage;
import org.mdc.api.GrpcAPI.EasyTransferResponse;
import org.mdc.api.GrpcAPI.Return.response_code;
import org.mdc.common.crypto.ECKey;
import org.mdc.core.Wallet;
import org.mdc.core.capsule.TransactionCapsule;
import org.mdc.core.exception.ContractValidateException;
import org.mdc.core.services.http.JsonFormat.ParseException;
import org.mdc.protos.Contract.TransferAssetContract;
import org.mdc.protos.Protocol.Transaction.Contract.ContractType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;


@Component
@Slf4j
public class EasyTransferAssetServlet extends HttpServlet {

  @Autowired
  private Wallet wallet;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) {

  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    GrpcAPI.Return.Builder returnBuilder = GrpcAPI.Return.newBuilder();
    EasyTransferResponse.Builder responseBuild = EasyTransferResponse.newBuilder();
    boolean visible = false;
    try {
      String input = request.getReader().lines()
          .collect(Collectors.joining(System.lineSeparator()));
      visible = Util.getVisiblePost(input);
      EasyTransferAssetMessage.Builder build = EasyTransferAssetMessage.newBuilder();
      JsonFormat.merge(input, build, visible);
      byte[] privateKey = wallet.pass2Key(build.getPassPhrase().toByteArray());
      ECKey ecKey = ECKey.fromPrivate(privateKey);
      byte[] owner = ecKey.getAddress();
      TransferAssetContract.Builder builder = TransferAssetContract.newBuilder();
      builder.setOwnerAddress(ByteString.copyFrom(owner));
      builder.setToAddress(build.getToAddress());
      builder.setAssetName(ByteString.copyFrom(build.getAssetId().getBytes()));
      builder.setAmount(build.getAmount());

      TransactionCapsule transactionCapsule;
      transactionCapsule = wallet
          .createTransactionCapsule(builder.build(), ContractType.TransferAssetContract);
      transactionCapsule.sign(privateKey);
      GrpcAPI.Return retur = wallet.broadcastTransaction(transactionCapsule.getInstance());
      responseBuild.setTransaction(transactionCapsule.getInstance());
      responseBuild.setResult(retur);
      response.getWriter().println(Util.printEasyTransferResponse(responseBuild.build(), visible));
    } catch (ParseException e) {
      logger.debug("ParseException: {}", e.getMessage());
      returnBuilder.setResult(false).setCode(response_code.OTHER_ERROR)
          .setMessage(ByteString.copyFromUtf8(e.getMessage()));
      responseBuild.setResult(returnBuilder.build());
      try {
        response.getWriter().println(JsonFormat.printToString(responseBuild.build(), visible));
      } catch (IOException ioe) {
        logger.debug("IOException: {}", ioe.getMessage());
      }
      return;
    } catch (IOException e) {
      logger.debug("IOException: {}", e.getMessage());
      returnBuilder.setResult(false).setCode(response_code.OTHER_ERROR)
          .setMessage(ByteString.copyFromUtf8(e.getMessage()));
      responseBuild.setResult(returnBuilder.build());
      try {
        response.getWriter().println(JsonFormat.printToString(responseBuild.build(), visible));
      } catch (IOException ioe) {
        logger.debug("IOException: {}", ioe.getMessage());
      }
      return;
    } catch (ContractValidateException e) {
      returnBuilder.setResult(false).setCode(response_code.CONTRACT_VALIDATE_ERROR)
          .setMessage(ByteString.copyFromUtf8(e.getMessage()));
      responseBuild.setResult(returnBuilder.build());
      try {
        response.getWriter().println(JsonFormat.printToString(responseBuild.build(), visible));
      } catch (IOException ioe) {
        logger.debug("IOException: {}", ioe.getMessage());
      }
      return;
    }
  }
}