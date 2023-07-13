package com.example.common;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.client.Network;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class EventListenerRunner implements CommandLineRunner {//多线程异步执行
    final ChaincodeEventListener chaincodeEventListener;

    final Network network;

    public static final List<String> CHANNEL_LIST = Arrays.asList("mychannel");
    public static final List<String> CHAINCODE_LIST = Arrays.asList("basic","datasharing-contract-java-1");


    @Order(1) //第一步
    @Override
    public void run(String... args) {

        CHANNEL_LIST.forEach(c -> {
            for (String cc : CHAINCODE_LIST) {
                new Thread(() -> {
                    chaincodeEventListener.chaincodeEvents(network,cc);
                }).start(); //第二步
            }
        });
    }

}
