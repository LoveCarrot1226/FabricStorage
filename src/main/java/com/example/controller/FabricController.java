package com.example.controller;


import com.example.domain.Asset;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.TimeoutException;


@RestController
@RequestMapping("/asset")
@Slf4j
@AllArgsConstructor
public class FabricController {
    @Autowired
    final Gateway gateway;

    private Network getNetwork() {
        return gateway.getNetwork("mychannel");
    }

    private Contract getContract() {
        return getNetwork().getContract("basic");
    }


    @PostMapping
    public String createAsset(@RequestBody Asset asset) throws ContractException, InterruptedException, TimeoutException {
        Contract contract = getContract();
        //Network network=getNetwork();
        byte[] newAsset = contract.submitTransaction("CreateAsset", asset.getAssetID(), asset.getColor(),
                String.valueOf(asset.getSize()), asset.getOwner(), String.valueOf(asset.getAppraisedValue()));

        return "*** Transaction committed successfully, CreateAsset: " + StringUtils.newStringUtf8(newAsset);
    }
    /*byte[] invokeResult = contract.createTransaction("CreateAsset")
                    .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                    .submit("asset5","blue","5", "LaoWang", "666");*/
    @PutMapping
    public String updateAsset(@RequestBody Asset asset) throws ContractException, InterruptedException, TimeoutException {
        Contract contract = getContract();

        byte[] newAsset = contract.submitTransaction("UpdateAsset", asset.getAssetID(), asset.getColor(),
                String.valueOf(asset.getSize()), asset.getOwner(), String.valueOf(asset.getAppraisedValue()));

        return "*** Transaction committed successfully, UpdateAsset: " + StringUtils.newStringUtf8(newAsset);
    }

    @DeleteMapping("/{assetID}")
    public String deleteAsset(@PathVariable String assetID) throws ContractException, InterruptedException, TimeoutException {
        Contract contract = getContract();

        contract.submitTransaction("UpdateAsset", String.valueOf(assetID));

        return "*** Transaction committed successfully, DeleteAsset: " + assetID;
    }

    @GetMapping
    public String getAllAssets() throws GatewayException {
        //Map<String, Object> result = Maps.newConcurrentMap();
        Contract contract = getContract();
        byte[] asset = contract.evaluateTransaction("GetAllAssets");
        //result.put("result", StringUtils.newStringUtf8(car));

        return "*** Transaction committed successfully, AllAssets: " + StringUtils.newStringUtf8(asset);
    }

    @GetMapping("/{assetID}")
    public String ReadAsset(@PathVariable String assetID) throws ContractException, InterruptedException, TimeoutException {
        Contract contract = getContract();
        byte[] asset = contract.submitTransaction("ReadAsset", assetID);

        return "*** Transaction committed successfully, ReadAssets: " + StringUtils.newStringUtf8(asset);
    }

    @PostMapping("/{assetID}")
    public String TransferAsset(@PathVariable String assetID, @RequestBody String newOwner) throws ContractException, InterruptedException, TimeoutException {
        Contract contract = getContract();
        byte[] asset = contract.submitTransaction("TransferAsset", assetID, newOwner);
        
        return "*** Transaction committed successfully, Transfer "+assetID+" to " + newOwner;
    }
}
