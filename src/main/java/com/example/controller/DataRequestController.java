package com.example.controller;


import cn.hutool.crypto.digest.DigestUtil;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254a.*;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG1Parameters;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG2Parameters;
import com.example.DizkZKP.configuration.Configuration;
import com.example.DizkZKP.relations.objects.Assignment;
import com.example.DizkZKP.relations.r1cs.R1CSRelation;
import com.example.DizkZKP.zk_proof_systems.zkSNARK.objects.CRS;
import com.example.DizkZKP.zk_proof_systems.zkSNARK.objects.Proof;
import com.example.domain.DataRequest;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.hyperledger.fabric.client.*;
import org.springframework.web.bind.annotation.*;
import scala.Tuple3;

import java.io.File;
import java.math.BigInteger;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/dataRequest")
@Slf4j
@AllArgsConstructor
public class DataRequestController {
    final Gateway gateway;
    final Contract dataRequestContract;

    @PostMapping
    public Map<String, Object> createDataRequest(@RequestBody DataRequest dataRequest) throws Exception{
        Map<String, Object> result = Maps.newConcurrentMap();

        byte[] newDataRequest = dataRequestContract.submitTransaction("CreateDataRequest",
                        dataRequest.getDataID(),
                        dataRequest.getApplicant(),
                        dataRequest.getUsage(),
                        dataRequest.getHostIP());
        result.put("DataRequest", StringUtils.newStringUtf8(newDataRequest));
        result.put("status", "ok");
        return result;
        // return "*** Transaction committed successfully, CreateDataRequest: " +
        // StringUtils.newStringUtf8(newDataRequest);
    }

    @PostMapping("/async")
    public Map<String, Object> createDataRequestAsync(@RequestBody DataRequest dataRequest)
            throws Exception {
        Map<String, Object> result = Maps.newConcurrentMap();
        dataRequestContract
                .newProposal("CreateDataRequest")
                .addArguments(
                        dataRequest.getDataID(),
                        dataRequest.getApplicant(),
                        dataRequest.getUsage(),
                        dataRequest.getHostIP())
                .build()
                .endorse()
                .submitAsync();

        result.put("status", "ok");

        return result;
    }

    @PutMapping
    public Map<String, Object> updateDataRequest(@RequestBody DataRequest dataRequest) throws Exception {
        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] newDataRequest = dataRequestContract.submitTransaction("UpdateDataRequest",
                dataRequest.getDataID(),
                dataRequest.getApplicant(),
                dataRequest.getUsage(),
                dataRequest.getHostIP());

        result.put("updateDataRequest", StringUtils.newStringUtf8(newDataRequest));
        result.put("status", "ok");
        return result;
        // return "*** Transaction committed successfully, UpdateDataRequest: " +
        // StringUtils.newStringUtf8(newDataRequest);
    }

    @DeleteMapping("/{dataId}")
    public Map<String, Object> deleteDataRequest(@PathVariable String dataId)
            throws EndorseException, CommitException, SubmitException, CommitStatusException {
        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] deletedDataRequest = dataRequestContract.submitTransaction("DeleteDataRequest", dataId);

        result.put("deletedDataRequest", dataId);
        result.put("status", "ok");
        return result;
        // return "*** Transaction committed successfully, DeleteDataRequest: " + DataRequestID;
    }

    @GetMapping("/{dataId}")
    public Map<String, Object> getDataRequest(@PathVariable String dataId) throws GatewayException {
        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] dataRequest = dataRequestContract.evaluateTransaction("GetDataRequest", dataId);
        result.put("result", StringUtils.newStringUtf8(dataRequest));

        return result;
        // return "*** Transaction committed successfully, AllDataRequest: " +
        // StringUtils.newStringUtf8(DataRequest);
    }

    @GetMapping
    public Map<String, Object> getAllDataRequest() throws GatewayException {
        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] dataRequest = dataRequestContract.evaluateTransaction("GetAllDataRequest");
        result.put("result", StringUtils.newStringUtf8(dataRequest));

        return result;
        // return "*** Transaction committed successfully, AllDataRequest: " +
        // StringUtils.newStringUtf8(DataRequest);
    }

    @GetMapping("/{applicant}")
    public String GetDataRequestByOwner(@PathVariable String applicant) throws GatewayException {
        byte[] dataRequest = dataRequestContract.evaluateTransaction("GetDataRequestByOwner", applicant);

        return "the DataRequest belong to " + applicant + " :" + StringUtils.newStringUtf8(dataRequest);
    }

    @GetMapping("/applicantPage")
    public String GetDataRequestPageByApplicant(String applicant, Integer pageSize, String bookmark)
            throws GatewayException {
        byte[] dataRequest =
                dataRequestContract.evaluateTransaction(
                        "GetDataRequestPageByOwner", applicant, String.valueOf(pageSize), bookmark);

        return "the DataRequest page belong to " + applicant + " :" + StringUtils.newStringUtf8(dataRequest);
    }


}
