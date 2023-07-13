package com.example.controller;

import com.example.domain.Asset;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.hyperledger.fabric.client.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Random;



@RestController
@RequestMapping("/asset")
@Slf4j
@AllArgsConstructor
public class AssetController {
    final Gateway gateway;
    final Contract basicContract;


    @PostMapping
    public Map<String, Object> createAsset(@RequestBody Asset asset) throws EndorseException, CommitException, SubmitException, CommitStatusException {
        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] newAsset = basicContract.submitTransaction("CreateAsset", asset.getAssetID(), asset.getColor(),
                String.valueOf(asset.getSize()), asset.getOwner(), String.valueOf(asset.getAppraisedValue()));
        result.put("payload", StringUtils.newStringUtf8(newAsset));
        result.put("status", "ok");
        return result;
        //return "*** Transaction committed successfully, CreateAsset: " + StringUtils.newStringUtf8(newAsset);
    }
    /*byte[] invokeResult = basicContract.createTransaction("CreateAsset")
                    .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                    .submit("asset5","blue","5", "LaoWang", "666");*/
    @PostMapping ("/async")
    public Map<String, Object> createAssetAsync(@RequestBody Asset asset) throws Exception {
        Map<String, Object> result = Maps.newConcurrentMap();

//        Status status = basicContract.newProposal("CreateAsset")
//                .addArguments(asset.getAssetID(), asset.getColor(),
//                        String.valueOf(asset.getSize()), asset.getOwner(), String.valueOf(asset.getAppraisedValue()))
//                .build()
//                .endorse()
//                .submitAsync()
//                .getStatus();

        basicContract.newProposal("CreateAsset")
                .addArguments(asset.getAssetID(), asset.getColor(),
                        String.valueOf(asset.getSize()), asset.getOwner(), String.valueOf(asset.getAppraisedValue()))
                .build()
                .endorse()
                .submitAsync();

//        if (!status.isSuccessful()) {
//            throw new RuntimeException("Transaction " + status.getTransactionId() + " failed to commit with status code: " + status.getCode());
//        }

        result.put("status", "ok");

        return result;
    }
    @PutMapping
    public Map<String, Object> updateAsset(@RequestBody Asset asset) throws EndorseException, CommitException, SubmitException, CommitStatusException {
        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] newAsset = basicContract.submitTransaction("UpdateAsset", asset.getAssetID(), asset.getColor(),
                String.valueOf(asset.getSize()), asset.getOwner(), String.valueOf(asset.getAppraisedValue()));
        result.put("payload", StringUtils.newStringUtf8(newAsset));
        result.put("status", "ok");
        return result;
        //return "*** Transaction committed successfully, UpdateAsset: " + StringUtils.newStringUtf8(newAsset);
    }

    @DeleteMapping("/{assetID}")
    public Map<String, Object> deleteAsset(@PathVariable String assetID) throws EndorseException, CommitException, SubmitException, CommitStatusException {
        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] deletedAsset = basicContract.submitTransaction("DeleteAsset", String.valueOf(assetID));
        result.put("payload", StringUtils.newStringUtf8(deletedAsset));
        result.put("status", "ok");
        return result;
        //return "*** Transaction committed successfully, DeleteAsset: " + assetID;
    }

    @GetMapping
    public Map<String, Object> getAllAssets() throws GatewayException {
        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] assets = basicContract.evaluateTransaction("GetAllAssets");
        result.put("result", StringUtils.newStringUtf8(assets));

        return result;
        //return "*** Transaction committed successfully, AllAssets: " + StringUtils.newStringUtf8(asset);
    }

    @GetMapping("/{assetID}")
    public String ReadAsset(@PathVariable String assetID) throws GatewayException {
        byte[] asset = basicContract.evaluateTransaction("ReadAsset", assetID);

        return "*** Transaction committed successfully, ReadAssets: " + StringUtils.newStringUtf8(asset);
    }

    @PostMapping("/{assetID}")
    public String TransferAsset(@PathVariable String assetID, @RequestBody String newOwner) throws EndorseException, CommitException, SubmitException, CommitStatusException {

        byte[] asset = basicContract.submitTransaction("TransferAsset", assetID, newOwner);

        return "*** Transaction committed successfully, Transfer "+assetID+" to " + newOwner;
    }
    @RequestMapping("/tps-test")
    public Map<String, Object> tpsTest() throws Exception {

        Asset asset = new Asset()
                .setAssetID(String.valueOf(new Random().nextInt()))
                .setColor("蓝色-" + System.currentTimeMillis())
                .setSize(new Random().nextInt())
                .setOwner("爱迪生-" + System.currentTimeMillis())
                .setAppraisedValue(new Random().nextInt());
        return createAssetAsync(asset);
    }
}
