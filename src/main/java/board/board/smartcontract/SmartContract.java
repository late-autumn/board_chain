package board.board.smartcontract;

import hera.api.model.ContractAddress;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Fee;
import hera.wallet.WalletApi;
//import io.blocko.service.AbstractService;

public class SmartContract {

  public static ContractTxReceipt contractExecute(WalletApi walletApi, String SmartContractId,
      String funcId, String[] lists) throws InterruptedException {
    // get contract interface
    ContractAddress contractAddress = new ContractAddress(SmartContractId);
    ContractInterface contractInterface =
        walletApi.queryApi().getContractInterface(contractAddress);
    System.out.println("Contract interface: " + contractInterface);

    // define contract execution
    ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function(funcId)
        .args(lists)
        .build();
    System.out.println("Contract invocation: " + execution);

    // execute
    ContractTxHash executionTxHash = walletApi.transactionApi().execute(execution, Fee.ZERO);
    System.out.println("Execution hash: " + executionTxHash);

    Thread.sleep(2200L);

    // query execution transaction receipt
    ContractTxReceipt executionReceipt = walletApi.queryApi().getReceipt(executionTxHash);
    System.out.println("Execution receipt: " + executionReceipt);

    return executionReceipt;


  }

  public static ContractResult contractQuery(WalletApi walletApi, String SmartContractId,
      String funcId, String[] args) throws InterruptedException {

    // get contract interface
    ContractAddress contractAddress = new ContractAddress(SmartContractId);
    ContractInterface contractInterface =
        walletApi.queryApi().getContractInterface(contractAddress);

    System.out.println("Contract interface: " + contractInterface);

    // build query invocation for constructor
    ContractInvocation queryForConstructor = contractInterface.newInvocationBuilder()
        .function(funcId)
        .args(args)
        .build();

    System.out.println("Query invocation : " + queryForConstructor);

    // request query invocation
    ContractResult deployResult = walletApi.queryApi().query(queryForConstructor);
    System.out.println("Query result: " + deployResult);

    return deployResult;
  }
}
