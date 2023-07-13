/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254b.bn254b_parameters;

import com.example.DizkZKP.algebra.curves.barreto_naehrig.abstract_bn_parameters.AbstractBNGTParameters;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq12;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq2;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254b.BN254bFields.BN254bFq6;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254b.BN254bGT;

public class BN254bGTParameters
        extends AbstractBNGTParameters<BN254bFq, BN254bFq2, BN254bFq6, BN254bFq12, BN254bGT, BN254bGTParameters> {

    public static final BN254bGT ONE = new BN254bGT(BN254bFq12.ONE);

    public BN254bGT ONE() {
        return ONE;
    }
}
