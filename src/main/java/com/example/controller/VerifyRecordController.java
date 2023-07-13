package com.example.controller;


import com.example.domain.VerifyRecord;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.hyperledger.fabric.client.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/verify")
@Slf4j
@AllArgsConstructor
public class VerifyRecordController {
    final Gateway gateway;
    final Contract verifyContract;


    @GetMapping("/record/{dataId}")
    public Map<String, Object> getVerifyRecord(@PathVariable String dataId) throws GatewayException {
        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] verifyRecord = verifyContract.evaluateTransaction("GetVerifyRecord", dataId);
        result.put("result", StringUtils.newStringUtf8(verifyRecord));

        return result;
    }

    @GetMapping("/record")
    public Map<String, Object> getAllVerifyRecord() throws GatewayException {
        Map<String, Object> result = Maps.newConcurrentMap();
        byte[] verifyRecord = verifyContract.evaluateTransaction("GetAllVerifyRecord");
        result.put("allResult", StringUtils.newStringUtf8(verifyRecord));

        return result;
    }

    /**
     * 通过智能合约验证零知识证明
     * @param dataId 要验证的数据表单ID
     * @return
     * @throws EndorseException
     * @throws CommitException
     * @throws SubmitException
     * @throws CommitStatusException
     */
    @GetMapping("/{dataId}")
    public String verifyData(@PathVariable String dataId) throws EndorseException, CommitException, SubmitException, CommitStatusException {
        byte[] isValid = verifyContract.submitTransaction("Verify", dataId);
        return isValid.toString();
    }

}
