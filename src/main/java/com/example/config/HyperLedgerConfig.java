package com.example.config;


import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.client.CallOption;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;

import org.hyperledger.fabric.client.Network;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Signers;
import org.hyperledger.fabric.client.identity.X509Identity;
import com.example.common.ChaincodeEventListener_old;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.identity.IdemixEnrollment;
import org.hyperledger.fabric.sdk.identity.X509Enrollment;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

@Configuration
@AllArgsConstructor
@Slf4j
public class HyperLedgerConfig {
    final HyperLedgerFabricProperties hyperLedgerFabricProperties;

    //final HFCAClient hfcaClient;

    @Bean
    public Gateway gateway() throws Exception {

        BufferedReader certificateReader = Files.newBufferedReader(Paths.get(hyperLedgerFabricProperties.getCertificatePath()), StandardCharsets.UTF_8);

        X509Certificate certificate = Identities.readX509Certificate(certificateReader);

        BufferedReader privateKeyReader = Files.newBufferedReader(Paths.get(hyperLedgerFabricProperties.getPrivateKeyPath()), StandardCharsets.UTF_8);

        PrivateKey privateKey = Identities.readPrivateKey(privateKeyReader);

        Gateway gateway = Gateway.newInstance()
                .identity(new X509Identity(hyperLedgerFabricProperties.getMspId(), certificate))
                .signer(Signers.newPrivateKeySigner(privateKey))
                .connection(newGrpcConnection())
                .evaluateOptions(CallOption.deadlineAfter(2, TimeUnit.MINUTES))
                .endorseOptions(CallOption.deadlineAfter(3, TimeUnit.MINUTES))
                .submitOptions(CallOption.deadlineAfter(3, TimeUnit.MINUTES))
                .commitStatusOptions(CallOption.deadlineAfter(3, TimeUnit.MINUTES))
                .connect();

        log.info("=========================================== connected fabric gateway {} ", gateway);

        return gateway;
    }

    private ManagedChannel newGrpcConnection() throws IOException, CertificateException {
        Reader tlsCertReader = Files.newBufferedReader(Paths.get(hyperLedgerFabricProperties.getTlsCertPath()));
        X509Certificate tlsCert = Identities.readX509Certificate(tlsCertReader);

        return NettyChannelBuilder.forTarget("172.52.0.244:30077")
                .sslContext(GrpcSslContexts.forClient().trustManager(tlsCert).build())
                .overrideAuthority("peer0.org1.example.com")
                .build();
    }

    @Bean
    public Network network(Gateway gateway) {
        return gateway.getNetwork(hyperLedgerFabricProperties.getChannel());
    }

    @Bean
    public Contract basicContract(Network network) {
        return network.getContract("basic","basic");
    }
    @Bean
    public Contract dataRecordContract(Network network) {
        return network.getContract("datasharing-contract-java-1","DataRecord");
    }
    @Bean
    public Contract verifyContract(Network network) {
        return network.getContract("datasharing-contract-java-1","verify");
    }
    @Bean
    public Contract dataRequestContract(Network network) {
        return network.getContract("datasharing-contract-java-1","DataRequest");
    }

    /*@Bean
    public IdemixEnrollment getIdemixEnrollment() throws Exception {
        BufferedReader privateKeyReader = Files.newBufferedReader(Paths.get(hyperLedgerFabricProperties.getPrivateKeyPath()), StandardCharsets.UTF_8);
        PrivateKey privateKey = Identities.readPrivateKey(privateKeyReader);

        Enrollment x509enrollment=new X509Enrollment(privateKey,"F:\\JavaCode\\FabricJDK\\src\\main\\resources\\crypto-config\\peerOrganizations\\org1.example.com\\ca\\ca.org1.example.com-cert.pem");

        IdemixEnrollment idemixEnrollment = (IdemixEnrollment)hfcaClient.idemixEnroll(x509enrollment, "idemixMSPID1");
        return idemixEnrollment;

    }*/
}
