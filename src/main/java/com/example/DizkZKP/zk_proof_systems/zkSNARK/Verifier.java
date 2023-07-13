/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package com.example.DizkZKP.zk_proof_systems.zkSNARK;

import com.example.DizkZKP.algebra.fields.AbstractFieldElementExpanded;
import com.example.DizkZKP.algebra.curves.AbstractG1;
import com.example.DizkZKP.algebra.curves.AbstractG2;
import com.example.DizkZKP.algebra.curves.AbstractGT;
import com.example.DizkZKP.algebra.curves.AbstractPairing;
import com.example.DizkZKP.algebra.msm.VariableBaseMSM;
import com.example.DizkZKP.configuration.Configuration;
import com.example.DizkZKP.relations.objects.Assignment;
import com.example.DizkZKP.zk_proof_systems.zkSNARK.objects.Proof;
import com.example.DizkZKP.zk_proof_systems.zkSNARK.objects.VerificationKey;

public class Verifier {
    public static <FieldT extends AbstractFieldElementExpanded<FieldT>, G1T extends
            AbstractG1<G1T>, G2T extends AbstractG2<G2T>, GTT extends AbstractGT<GTT>, PairingT extends
            AbstractPairing<G1T, G2T, GTT>>
    boolean verify(
            final VerificationKey<G1T, G2T, GTT> verificationKey,
            final Assignment<FieldT> primaryInput,
            final Proof<G1T, G2T> proof,
            final PairingT pairing,
            final Configuration config) {
        // Assert first element == FieldT.one().
        final FieldT firstElement = primaryInput.get(0);
        assert (firstElement.equals(firstElement.one()));

        // Compute the left hand side: A * B.
        final GTT AB = pairing.reducedPairing(proof.gA(), proof.gB());

        // Get alpha, beta, gamma, and delta from the verification key.
        final GTT alphaBeta = verificationKey.alphaG1betaG2(); // alpha * beta
        final G2T gamma = verificationKey.gammaG2();
        final G2T delta = verificationKey.deltaG2();
        final GTT CDelta = pairing.reducedPairing(proof.gC(), delta); // C * delta

        // Compute the summation of primaryInput[i] * (beta*u_i(x) + alpha*v_i(x) + w_i(x)) * gamma^{-1}
        final G1T evaluationABC = VariableBaseMSM
                .serialMSM(primaryInput.elements(), verificationKey.gammaABC());

        // Compute the right hand side: alpha*beta + evaluationABC*gamma + C*delta
        final GTT C = alphaBeta.add(pairing.reducedPairing(evaluationABC, gamma)).add(CDelta);

        final boolean verifierResult = AB.equals(C);
        if (!verifierResult && config.verboseFlag()) {
            System.out.println("Verifier failed: ");
            System.out.println("\n\tA * B = " + AB + "\nC     = " + C);
        }

        return verifierResult;
    }
}
