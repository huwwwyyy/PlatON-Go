package event;

import beforetest.ContractPrepareTest;
import network.platon.autotest.junit.annotations.DataSource;
import network.platon.autotest.junit.enums.DataSourceType;
import network.platon.contracts.EventCallContract;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.List;

/**
 * @title 事件验证测试
 * @description:
 * @author: albedo
 * @create: 2019/12/28
 */
public class EventCallContractTest extends ContractPrepareTest {
    @Test
    @DataSource(type = DataSourceType.EXCEL, file = "test.xls", sheetName = "emitEvent",
            author = "albedo", showName = "event.EventCallContractTest-event关键字声明事件")
    public void testEmitEvent() {
        try {
            prepare();
            EventCallContract eventCallContract = EventCallContract.deploy(web3j, transactionManager, provider).send();
            String contractAddress = eventCallContract.getContractAddress();
            String transactionHash = eventCallContract.getTransactionReceipt().get().getTransactionHash();
            collector.logStepPass("EventCallContract issued successfully.contractAddress:" + contractAddress + ", hash:" + transactionHash);
            TransactionReceipt receipt = eventCallContract.emitEvent().send();
            List<EventCallContract.IncrementEventResponse> emitEventData = eventCallContract.getIncrementEvents(receipt);
            String data = emitEventData.get(0).log.getData();
            collector.assertEqual(subHexData(data), subHexData(receipt.getFrom()), "checkout new contract param");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DataSource(type = DataSourceType.EXCEL, file = "test.xls", sheetName = "indexedEvent",
            author = "albedo", showName = "event.EventCallContractTest-indexed关键字定义事件索引")
    public void testIndexedEvent() {
        try {
            prepare();
            EventCallContract eventCallContract = EventCallContract.deploy(web3j, transactionManager, provider).send();
            String contractAddress = eventCallContract.getContractAddress();
            String transactionHash = eventCallContract.getTransactionReceipt().get().getTransactionHash();
            collector.logStepPass("EventCallContract issued successfully.contractAddress:" + contractAddress + ", hash:" + transactionHash);
            TransactionReceipt receipt = eventCallContract.indexedEvent().send();
            List<EventCallContract.DepositEventResponse> emitEventData = eventCallContract.getDepositEvents(receipt);
            String data = emitEventData.get(0).log.getData();
            collector.assertEqual(subHexData(emitEventData.get(0).log.getTopics().get(1)), subHexData(receipt.getFrom()), "checkout new contract param");
            collector.assertEqual(subHexData(data), subHexData("c"), "checkout new contract param");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DataSource(type = DataSourceType.EXCEL, file = "test.xls", sheetName = "anonymousEvent",
            author = "albedo", showName = "event.EventCallContractTest-anonymous关键字定义匿名事件")
    public void testAnonymousEvent() {
        try {
            prepare();
            EventCallContract eventCallContract = EventCallContract.deploy(web3j, transactionManager, provider).send();
            String contractAddress = eventCallContract.getContractAddress();
            String transactionHash = eventCallContract.getTransactionReceipt().get().getTransactionHash();
            collector.logStepPass("EventCallContract issued successfully.contractAddress:" + contractAddress + ", hash:" + transactionHash);
            TransactionReceipt receipt = eventCallContract.anonymousEvent().send();
            collector.assertEqual(subHexData(receipt.getLogs().get(0).getData()), subHexData("1"), "checkout new contract param");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String subHexData(String hexStr) {
        if (StringUtils.isBlank(hexStr)) {
            throw new IllegalArgumentException("string is blank");
        }
        if (StringUtils.startsWith(hexStr, "0x")) {
            hexStr = StringUtils.substringAfter(hexStr, "Ox");
        }
        byte[] addi = hexStr.getBytes();
        for (int i = 0; i < addi.length; i++) {
            if (addi[i] != 0) {
                hexStr = StringUtils.substring(hexStr, i - 1);
            }
        }
        return hexStr;
    }
}