package com.example.common;

import com.example.domain.CcEvent;
import com.example.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.hyperledger.fabric.client.ChaincodeEvent;
import org.hyperledger.fabric.client.CloseableIterator;
import org.hyperledger.fabric.client.Network;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
public class ChaincodeEventListener {
    final EventService eventService;

    public ChaincodeEventListener(EventService eventService) {
        this.eventService = eventService;
    }

    public void chaincodeEvents(Network network, String chaincodeName) {
    log.info("Read events of chaincode: " + chaincodeName);
        try (CloseableIterator<ChaincodeEvent> events = network.getChaincodeEvents(chaincodeName)) {
        while (true) {
            CompletableFuture<ChaincodeEvent> receivedEvent = CompletableFuture.supplyAsync(events::next);
            if (!ObjectUtils.isEmpty(receivedEvent)) {
                ChaincodeEvent event = receivedEvent.get();
                log.info("Received event name: " + event.getEventName() +
                        ", payload: " + new String(event.getPayload(), StandardCharsets.UTF_8) +
                        ", txId: " + event.getTransactionId() +
                        ", blockNumber: " + event.getBlockNumber() +
                        ", chaincodeName: " + chaincodeName);
                //事件处理逻辑
                CcEvent ccEvent=new CcEvent(
                        event.getBlockNumber(),event.getTransactionId(),event.getChaincodeName(),
                        event.getEventName(),new String(event.getPayload(), StandardCharsets.UTF_8));
                eventService.save(ccEvent);
            }
            Thread.sleep(50);
        }
    } catch (Exception e) {
        log.error("链码事件处理失败", e);
    }
}

}
