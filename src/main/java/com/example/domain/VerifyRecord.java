package com.example.domain;

import com.example.DizkZKP.algebra.curves.barreto_naehrig.*;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNG1Parameters;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNG2Parameters;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNGTParameters;
import com.example.DizkZKP.configuration.Configuration;
import com.example.DizkZKP.relations.objects.Assignment;
import com.example.DizkZKP.zk_proof_systems.zkSNARK.objects.Proof;
import com.example.DizkZKP.zk_proof_systems.zkSNARK.objects.ProvingKey;
import com.example.DizkZKP.zk_proof_systems.zkSNARK.objects.VerificationKey;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class VerifyRecord<
    BNFrT extends BNFields.BNFr<BNFrT>,
    BNFqT extends BNFields.BNFq<BNFqT>,
    BNFq2T extends BNFields.BNFq2<BNFqT, BNFq2T>,
    BNFq6T extends BNFields.BNFq6<BNFqT, BNFq2T, BNFq6T>,
    BNFq12T extends BNFields.BNFq12<BNFqT, BNFq2T, BNFq6T, BNFq12T>,
    BNG1T extends BNG1<BNFrT, BNFqT, BNG1T, BNG1ParametersT>,
    BNG2T extends BNG2<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>,
    BNGTT extends BNGT<BNFqT, BNFq2T, BNFq6T, BNFq12T, BNGTT, BNGTParametersT>,
    BNG1ParametersT extends AbstractBNG1Parameters<BNFrT, BNFqT, BNG1T, BNG1ParametersT>,
    BNG2ParametersT extends AbstractBNG2Parameters<BNFrT, BNFqT, BNFq2T, BNG2T, BNG2ParametersT>,
    BNGTParametersT extends AbstractBNGTParameters<BNFqT, BNFq2T, BNFq6T, BNFq12T, BNGTT, BNGTParametersT>,
    BNPublicParametersT extends BNPublicParameters<BNFqT, BNFq2T, BNFq6T, BNFq12T>,
    BNPairingT extends BNPairing<
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
                BNPublicParametersT>> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uuid;
    private String dataId;
    private ProvingKey provingKey;
    private VerificationKey verificationKey;
    private Assignment<BNFrT> primary;
    private Proof<BNG1T, BNG2T> proof;
    private BNPairingT pairing;
    private Configuration config;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;



}
