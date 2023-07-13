/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254a;

import com.example.DizkZKP.algebra.curves.barreto_naehrig.BNG1;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFq;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254a.bn254a_parameters.BN254aG1Parameters;

public class BN254aG1 extends BNG1<BN254aFr, BN254aFq, BN254aG1, BN254aG1Parameters> {

    public static final BN254aG1Parameters G1Parameters = new BN254aG1Parameters();

    public BN254aG1(
            final BN254aFq X,
            final BN254aFq Y,
            final BN254aFq Z) {
        super(X, Y, Z, G1Parameters);
    }

    public BN254aG1 self() {
        return this;
    }

    public BN254aG1 construct(final BN254aFq X, final BN254aFq Y, final BN254aFq Z) {
        return new BN254aG1(X, Y, Z);
    }
}
