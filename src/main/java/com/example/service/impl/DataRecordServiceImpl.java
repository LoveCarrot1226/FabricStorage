package com.example.service.impl;

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
import com.example.DizkZKP.zk_proof_systems.zkSNARK.objects.CRS;
import com.example.DizkZKP.zk_proof_systems.zkSNARK.objects.Proof;
import com.example.DizkZKP.zk_proof_systems.zkSNARK.objects.ProvingKey;
import com.example.common.exception.FabricException;
import com.example.domain.DataRecord;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.binary.StringUtils;
import org.hyperledger.fabric.client.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import scala.Tuple3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@AllArgsConstructor
@Service
public class DataRecordServiceImpl {
    final Gateway gateway;
    final Contract dataRecordContract;

    final Contract verifyContract;

    static final Base64.Encoder encoder = Base64.getEncoder();
    static final Base64.Decoder decoder = Base64.getDecoder();

    /**
     *
     */
    public Map<String, Object> createDataRecord(DataRecord dataRecord){
        Map<String, Object> result = Maps.newConcurrentMap();
        // 1.配置zkp输入参数
        Configuration config = new Configuration();
        config.setRuntimeFlag(false);
        config.setDebugFlag(true);
        int numInputs = 1023;
        int numConstraints = 1024;
        // 读取文件数据为sha256哈希值
        String sha256Hash = DigestUtil.sha256Hex(new File(dataRecord.getFilePath()));
        BigInteger bigInteger = new BigInteger(sha256Hash, 16);
        BN254aFields.BN254aFr fieldFactory = new BN254aFields.BN254aFr(bigInteger);
        BN254aG1 g1Factory = BN254aG1Parameters.ONE;
        BN254aG2 g2Factory = BN254aG2Parameters.ONE;
        BN254aPairing pairing = new BN254aPairing();
        // 2.生成R1CSConstruction
        Tuple3<
                R1CSRelation<BN254aFields.BN254aFr>,
                Assignment<BN254aFields.BN254aFr>,
                Assignment<BN254aFields.BN254aFr>>
                construction = generateR1CS(numInputs, numConstraints, fieldFactory, config);
        R1CSRelation<BN254aFields.BN254aFr> r1cs = construction._1();
        Assignment<BN254aFields.BN254aFr> primary = construction._2();
        Assignment<BN254aFields.BN254aFr> fullAssignment = construction._3();
        // 3.生成CRS
        CRS<BN254aFields.BN254aFr, BN254aG1, BN254aG2, BN254aGT> CRS =
                setupCRS(r1cs, fieldFactory, g1Factory, g2Factory, pairing, config);
        // 4.生成proof
        Proof<BN254aG1, BN254aG2> proof =
                generateProof(CRS.provingKey(), primary, fullAssignment, fieldFactory, config);

        // 5.调用合约将验证零知识证明的所需参数上传
        byte[] newVerifyRecord =
                new byte[0];
        try {
            newVerifyRecord = verifyContract.submitTransaction(
                    "CreateVerifyRecord",
                    dataRecord.getDataID(),
                    String.valueOf(CRS.provingKey()),
                    String.valueOf(CRS.verificationKey()),
                    String.valueOf(primary),
                    String.valueOf(proof),
                    String.valueOf(pairing));
        } catch (Exception e) {
            throw new FabricException(e.getMessage());
        }
        // 6.调用合约将数据表单上传
        byte[] newDataRecord =
                new byte[0];
        try {
            newDataRecord = dataRecordContract.submitTransaction(
                    "CreateDataRecord",
                    dataRecord.getDataID(),
                    dataRecord.getFileName(),
                    dataRecord.getType(),
                    dataRecord.getDataDescription(),
                    sha256Hash,
                    dataRecord.getOwner(),
                    String.valueOf(proof));
        } catch (Exception e) {
            throw new FabricException(e.getMessage());
        }
        result.put("DataRecord", StringUtils.newStringUtf8(newDataRecord));
        result.put("VerifyRecord", StringUtils.newStringUtf8(newVerifyRecord));
        result.put("status", "ok");
        return result;
    }

    public Map<String, Object> createDataRecordAsync(@RequestBody DataRecord dataRecord){
        Map<String, Object> result = Maps.newConcurrentMap();
        // 1.配置zkp输入参数
        Configuration config = new Configuration();
        config.setRuntimeFlag(false);
        config.setDebugFlag(true);
        int numInputs = 1023;
        int numConstraints = 1024;
        // 读取文件数据为sha256哈希值
        String sha256Hash = DigestUtil.sha256Hex(new File(dataRecord.getFilePath()));
        BigInteger bigInteger = new BigInteger(sha256Hash, 16);
        BN254aFields.BN254aFr fieldFactory = new BN254aFields.BN254aFr(bigInteger);
        BN254aG1 g1Factory = BN254aG1Parameters.ONE;
        BN254aG2 g2Factory = BN254aG2Parameters.ONE;
        BN254aPairing pairing = new BN254aPairing();
        // 2.生成R1CSConstruction
        Tuple3<
                R1CSRelation<BN254aFields.BN254aFr>,
                Assignment<BN254aFields.BN254aFr>,
                Assignment<BN254aFields.BN254aFr>>
                construction = generateR1CS(numInputs, numConstraints, fieldFactory, config);
        R1CSRelation<BN254aFields.BN254aFr> r1cs = construction._1();
        Assignment<BN254aFields.BN254aFr> primary = construction._2();
        Assignment<BN254aFields.BN254aFr> fullAssignment = construction._3();
        // 3.生成CRS
        CRS<BN254aFields.BN254aFr, BN254aG1, BN254aG2, BN254aGT> CRS =
                setupCRS(r1cs, fieldFactory, g1Factory, g2Factory, pairing, config);
        // 4.生成proof
        Proof<BN254aG1, BN254aG2> proof =
                generateProof(CRS.provingKey(), primary, fullAssignment, fieldFactory, config);


        //        Status status = dataRecordContract.newProposal("CreateDataRecord")
        //                .addArguments(dataRecord.getDataRecordID(), dataRecord.getColor(),
        //                        String.valueOf(dataRecord.getSize()), dataRecord.getOwner(),
        // String.valueOf(dataRecord.getAppraisedValue()))
        //                .build()
        //                .endorse()
        //                .submitAsync()
        //                .getStatus();

        try {
            verifyContract
                    .newProposal("CreateVerifyRecord")
                    .addArguments(
                            dataRecord.getDataID(),
                            String.valueOf(CRS.provingKey()),
                            String.valueOf(CRS.verificationKey()),
                            String.valueOf(primary),
                            String.valueOf(proof),
                            String.valueOf(pairing))
                    .build()
                    .endorse()
                    .submitAsync();
        } catch (Exception e) {
            throw new FabricException(e.getMessage());
        }

        try {
            dataRecordContract
                    .newProposal("CreateDataRecord")
                    .addArguments(
                            dataRecord.getDataID(),
                            dataRecord.getFileName(),
                            dataRecord.getType(),
                            dataRecord.getDataDescription(),
                            sha256Hash,
                            dataRecord.getOwner(),
                            String.valueOf(proof))
                    .build()
                    .endorse()
                    .submitAsync();
        } catch (Exception e) {
            throw new FabricException(e.getMessage());
        }

        //        if (!status.isSuccessful()) {
        //            throw new RuntimeException("Transaction " + status.getTransactionId() + " failed
        // to commit with status code: " + status.getCode());
        //        }
        result.put("status", "ok");
        return result;
    }


    public Map<String, Object> updateDataRecord(DataRecord dataRecord){
        Map<String, Object> result = Maps.newConcurrentMap();
        /// 1.配置zkp输入参数
        Configuration config = new Configuration();
        config.setRuntimeFlag(false);
        config.setDebugFlag(true);
        int numInputs = 1023;
        int numConstraints = 1024;
        // 读取文件数据为sha256哈希值
        String sha256Hash = DigestUtil.sha256Hex(new File(dataRecord.getFilePath()));
        BigInteger bigInteger = new BigInteger(sha256Hash, 16);
        BN254aFields.BN254aFr fieldFactory = new BN254aFields.BN254aFr(bigInteger);
        BN254aG1 g1Factory = BN254aG1Parameters.ONE;
        BN254aG2 g2Factory = BN254aG2Parameters.ONE;
        BN254aPairing pairing = new BN254aPairing();
        // 2.生成R1CSConstruction
        Tuple3<
                R1CSRelation<BN254aFields.BN254aFr>,
                Assignment<BN254aFields.BN254aFr>,
                Assignment<BN254aFields.BN254aFr>>
                construction = generateR1CS(numInputs, numConstraints, fieldFactory, config);
        R1CSRelation<BN254aFields.BN254aFr> r1cs = construction._1();
        Assignment<BN254aFields.BN254aFr> primary = construction._2();
        Assignment<BN254aFields.BN254aFr> fullAssignment = construction._3();
        // 3.生成CRS
        CRS<BN254aFields.BN254aFr, BN254aG1, BN254aG2, BN254aGT> CRS =
                setupCRS(r1cs, fieldFactory, g1Factory, g2Factory, pairing, config);
        // 4.生成proof
        Proof<BN254aG1, BN254aG2> proof =
                generateProof(CRS.provingKey(), primary, fullAssignment, fieldFactory, config);
        // 5.调用合约将验证零知识证明的所需参数上传
        byte[] newVerifyRecord =
                new byte[0];
        try {
            newVerifyRecord = verifyContract.submitTransaction(
                    "UpdateVerifyRecord",
                    dataRecord.getDataID(),
                    String.valueOf(CRS.provingKey()),
                    String.valueOf(CRS.verificationKey()),
                    String.valueOf(primary),
                    String.valueOf(proof),
                    String.valueOf(pairing));
        } catch (Exception e) {
            throw new FabricException(e.getMessage());
        }
        // 6.调用合约将数据表单上传
        byte[] newDataRecord =
                new byte[0];
        try {
            newDataRecord = dataRecordContract.submitTransaction(
                    "UpdateDataRecord",
                    dataRecord.getDataID(),
                    dataRecord.getFileName(),
                    dataRecord.getType(),
                    dataRecord.getDataDescription(),
                    sha256Hash,
                    dataRecord.getOwner(),
                    String.valueOf(proof));
        } catch (Exception e) {
            throw new FabricException(e.getMessage());
        }

        result.put("updateDataRecord", StringUtils.newStringUtf8(newDataRecord));
        result.put("updateVerifyRecord", StringUtils.newStringUtf8(newVerifyRecord));
        result.put("status", "ok");
        return result;
    }
    public <
            BNFrT extends BNFields.BNFr<BNFrT>,
            BNFqT extends BNFields.BNFq<BNFqT>,
            BNFq2T extends BNFields.BNFq2<BNFqT, BNFq2T>,
            BNFq6T extends BNFields.BNFq6<BNFqT, BNFq2T, BNFq6T>,
            BNFq12T extends BNFields.BNFq12<BNFqT, BNFq2T, BNFq6T, BNFq12T>,
            BNG1T extends BNG1<BNFrT, BNFqT, BNG1T, BNG1ParametersT>,
            BNG2T extends BNG2<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>,
            BNGTT extends BNGT<BNFqT, BNFq2T, BNFq6T, BNFq12T, BNGTT, BNGTParametersT>,
            BNG1ParametersT extends AbstractBNG1Parameters<BNFrT, BNFqT, BNG1T, BNG1ParametersT>,
            BNG2ParametersT extends
                    AbstractBNG2Parameters<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>,
            BNGTParametersT extends
                    AbstractBNGTParameters<BNFqT, BNFq2T, BNFq6T, BNFq12T, BNGTT, BNGTParametersT>,
            BNPublicParametersT extends BNPublicParameters<BNFqT, BNFq2T, BNFq6T, BNFq12T>,
            BNPairingT extends
                    BNPairing<
                            BNFrT,
                            BNFqT,
                            BNFq2T,
                            BNFq6T,
                            BNFq12T,
                            BNG1T,
                            BNG2T,
                            BNGTT,
                            BNG1ParametersT,
                            BNG2ParametersT,
                            BNGTParametersT,
                            BNPublicParametersT>>
    Tuple3<R1CSRelation<BNFrT>, Assignment<BNFrT>, Assignment<BNFrT>> generateR1CS(
            final int numInputs, final int numConstraints, BNFrT fieldFactory, Configuration config) {
        return R1CSConstruction.serialConstruct(numConstraints, numInputs, fieldFactory, config);
    }

    public <
            BNFrT extends BNFields.BNFr<BNFrT>,
            BNFqT extends BNFields.BNFq<BNFqT>,
            BNFq2T extends BNFields.BNFq2<BNFqT, BNFq2T>,
            BNFq6T extends BNFields.BNFq6<BNFqT, BNFq2T, BNFq6T>,
            BNFq12T extends BNFields.BNFq12<BNFqT, BNFq2T, BNFq6T, BNFq12T>,
            BNG1T extends BNG1<BNFrT, BNFqT, BNG1T, BNG1ParametersT>,
            BNG2T extends BNG2<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>,
            BNGTT extends BNGT<BNFqT, BNFq2T, BNFq6T, BNFq12T, BNGTT, BNGTParametersT>,
            BNG1ParametersT extends AbstractBNG1Parameters<BNFrT, BNFqT, BNG1T, BNG1ParametersT>,
            BNG2ParametersT extends
                    AbstractBNG2Parameters<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>,
            BNGTParametersT extends
                    AbstractBNGTParameters<BNFqT, BNFq2T, BNFq6T, BNFq12T, BNGTT, BNGTParametersT>,
            BNPublicParametersT extends BNPublicParameters<BNFqT, BNFq2T, BNFq6T, BNFq12T>,
            BNPairingT extends
                    BNPairing<
                            BNFrT,
                            BNFqT,
                            BNFq2T,
                            BNFq6T,
                            BNFq12T,
                            BNG1T,
                            BNG2T,
                            BNGTT,
                            BNG1ParametersT,
                            BNG2ParametersT,
                            BNGTParametersT,
                            BNPublicParametersT>>
    CRS<BNFrT, BNG1T, BNG2T, BNGTT> setupCRS(
            R1CSRelation<BNFrT> r1cs,
            BNFrT fieldFactory,
            BNG1T g1Factory,
            BNG2T g2Factory,
            BNPairingT pairing,
            Configuration config) {
        return SerialSetup.generate(r1cs, fieldFactory, g1Factory, g2Factory, pairing, config);
    }

    public <
            BNFrT extends BNFields.BNFr<BNFrT>,
            BNFqT extends BNFields.BNFq<BNFqT>,
            BNFq2T extends BNFields.BNFq2<BNFqT, BNFq2T>,
            BNFq6T extends BNFields.BNFq6<BNFqT, BNFq2T, BNFq6T>,
            BNFq12T extends BNFields.BNFq12<BNFqT, BNFq2T, BNFq6T, BNFq12T>,
            BNG1T extends BNG1<BNFrT, BNFqT, BNG1T, BNG1ParametersT>,
            BNG2T extends BNG2<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>,
            BNGTT extends BNGT<BNFqT, BNFq2T, BNFq6T, BNFq12T, BNGTT, BNGTParametersT>,
            BNG1ParametersT extends AbstractBNG1Parameters<BNFrT, BNFqT, BNG1T, BNG1ParametersT>,
            BNG2ParametersT extends
                    AbstractBNG2Parameters<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>,
            BNGTParametersT extends
                    AbstractBNGTParameters<BNFqT, BNFq2T, BNFq6T, BNFq12T, BNGTT, BNGTParametersT>,
            BNPublicParametersT extends BNPublicParameters<BNFqT, BNFq2T, BNFq6T, BNFq12T>,
            BNPairingT extends
                    BNPairing<
                            BNFrT,
                            BNFqT,
                            BNFq2T,
                            BNFq6T,
                            BNFq12T,
                            BNG1T,
                            BNG2T,
                            BNGTT,
                            BNG1ParametersT,
                            BNG2ParametersT,
                            BNGTParametersT,
                            BNPublicParametersT>>
    Proof<BNG1T, BNG2T> generateProof(
            ProvingKey<BNFrT, BNG1T, BNG2T> provingKey,
            Assignment<BNFrT> primary,
            Assignment<BNFrT> fullAssignment,
            BNFrT fieldFactory,
            Configuration config) {
        return SerialProver.prove(provingKey, primary, fullAssignment, fieldFactory, config);
    }

    /**
     * 读取文件数据为字符串
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public String fileInputToString(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        // 1、第一步：文件转为byte数组
        byte[] buffer = new byte[1024];
        int size = -1;
        StringBuilder sb = new StringBuilder();
        while ((size = fis.read(buffer)) != -1) {
            // 2、第二步：byte数组转换为char数组
            char[] chars = new char[size];
            for (int i = 0; i < size; i++) {
                chars[i] = (char) buffer[i];
            }
            sb.append(chars);
        }
        fis.close();
        // 3、第三步：char数组转换为字符串dataContent
        return sb.toString();
    }

    /**
     * 文件的字符串数据加密
     *
     * @param dataContent
     * @return
     */
    public static String encode(String dataContent) {
        return encoder.encodeToString(dataContent.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 加密的文件字符串解密
     *
     * @param dataContent
     * @return
     */
    public static String decode(String dataContent) {
        return new String(decoder.decode(dataContent), StandardCharsets.UTF_8);
    }
}
