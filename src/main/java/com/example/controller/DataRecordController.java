package com.example.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.*;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNG1Parameters;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNG2Parameters;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNGTParameters;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254a.*;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG1Parameters;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG2Parameters;
import com.example.DizkZKP.configuration.Configuration;
import com.example.DizkZKP.profiler.generation.R1CSConstruction;
import com.example.DizkZKP.relations.objects.Assignment;
import com.example.DizkZKP.relations.r1cs.R1CSRelation;
import com.example.DizkZKP.zk_proof_systems.zkSNARK.SerialProver;
import com.example.DizkZKP.zk_proof_systems.zkSNARK.SerialSetup;
import com.example.DizkZKP.zk_proof_systems.zkSNARK.Verifier;
import com.example.DizkZKP.zk_proof_systems.zkSNARK.objects.CRS;
import com.example.DizkZKP.zk_proof_systems.zkSNARK.objects.Proof;
import com.example.DizkZKP.zk_proof_systems.zkSNARK.objects.ProvingKey;
import com.example.DizkZKP.zk_proof_systems.zkSNARK.objects.VerificationKey;
import com.example.domain.DataRecord;
import com.example.service.impl.DataRecordServiceImpl;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.hyperledger.fabric.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import scala.Tuple3;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/dataRecord")
@Slf4j
@AllArgsConstructor
public class DataRecordController {
    final Gateway gateway;
    final Contract dataRecordContract;
    final Contract verifyContract;
    @Autowired
    DataRecordServiceImpl dataRecordService;

    @PostMapping
    public Map<String, Object> createDataRecord(@RequestBody DataRecord dataRecord) {
        Map<String, Object> result = dataRecordService.createDataRecord(dataRecord);
        return result;
        // return "*** Transaction committed successfully, CreateDataRecord: " +
        // StringUtils.newStringUtf8(newDataRecord);
    }

    @PostMapping("/async")
    public Map<String, Object> createDataRecordAsync(@RequestBody DataRecord dataRecord)
            throws Exception {
        Map<String, Object> result = dataRecordService.createDataRecordAsync(dataRecord);
        return result;
    }

    @PutMapping
    public Map<String, Object> updateDataRecord(@RequestBody DataRecord dataRecord) throws Exception {
        Map<String, Object> result = dataRecordService.updateDataRecord(dataRecord);
        return result;
        // return "*** Transaction committed successfully, UpdateDataRecord: " +
        // StringUtils.newStringUtf8(newDataRecord);
    }

    @DeleteMapping("/{dataId}")
    public Map<String, Object> deleteDataRecord(@PathVariable String dataId)
            throws EndorseException, CommitException, SubmitException, CommitStatusException {
        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] deletedDataRecord = dataRecordContract.submitTransaction("DeleteDataRecord", dataId);
        byte[] deletedVerifyRecord = verifyContract.submitTransaction("DeleteVerifyRecord", dataId);
        result.put("deletedDataRecord", dataId);
        result.put("deletedVerifyRecord", dataId);
        result.put("status", "ok");
        return result;
        // return "*** Transaction committed successfully, DeleteDataRecord: " + dataRecordID;
    }

    @GetMapping("/{dataId}")
    public Map<String, Object> getDataRecord(@PathVariable String dataId) throws GatewayException {
        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] dataRecord = dataRecordContract.evaluateTransaction("GetDataRecord", dataId);
        result.put("result", StringUtils.newStringUtf8(dataRecord));

        return result;
        // return "*** Transaction committed successfully, AllDataRecord: " +
        // StringUtils.newStringUtf8(dataRecord);
    }

    @GetMapping
    public Map<String, Object> getAllDataRecord() throws GatewayException {
        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] dataRecord = dataRecordContract.evaluateTransaction("GetAllDataRecord");
        result.put("result", StringUtils.newStringUtf8(dataRecord));

        return result;
        // return "*** Transaction committed successfully, AllDataRecord: " +
        // StringUtils.newStringUtf8(dataRecord);
    }

    @GetMapping("/{owner}")
    public String GetDataRecordByOwner(@PathVariable String owner) throws GatewayException {
        byte[] dataRecord = dataRecordContract.evaluateTransaction("GetDataRecordByOwner", owner);

        return "*** Transaction committed successfully,  the dataRecord belong to "
                + owner
                + " :"
                + StringUtils.newStringUtf8(dataRecord);
    }

    @GetMapping("/ownerPage")
    public String GetDataRecordPageByOwner(String owner, Integer pageSize, String bookmark)
            throws GatewayException {
        byte[] dataRecord =
                dataRecordContract.evaluateTransaction(
                        "GetDataRecordPageByOwner", owner, String.valueOf(pageSize), bookmark);

        return "*** Transaction committed successfully,  the dataRecord page belong to "
                + owner
                + " :"
                + StringUtils.newStringUtf8(dataRecord);
    }

    @PostMapping("/{dataId}")
    public String TransferDataRecord(@PathVariable String dataId, @RequestBody String newOwner)
            throws EndorseException, CommitException, SubmitException, CommitStatusException {

        byte[] dataRecord =
                dataRecordContract.submitTransaction("TransferDataRecord", dataId, newOwner);

        return "*** Transaction committed successfully, Transfer " + dataId + " to " + newOwner;
    }

    @RequestMapping("/tps-test")
    public Map<String, Object> tpsTest() throws Exception {

        DataRecord dataRecord =
                new DataRecord()
                        .setDataID(String.valueOf(new Random().nextInt()))
                        .setFileName("文件-" + System.currentTimeMillis())
                        .setType(".txt")
                        .setOwner("中国科技-" + System.currentTimeMillis())
                        .setFilePath("a.txt");
        return createDataRecordAsync(dataRecord);
    }

}
