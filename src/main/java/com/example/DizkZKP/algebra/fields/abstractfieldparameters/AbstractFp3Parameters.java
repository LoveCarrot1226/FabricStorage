/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package com.example.DizkZKP.algebra.fields.abstractfieldparameters;

import com.example.DizkZKP.algebra.fields.Fp;
import com.example.DizkZKP.algebra.fields.Fp3;

public abstract class AbstractFp3Parameters {

    public abstract AbstractFpParameters FpParameters();

    public abstract Fp3 ZERO();

    public abstract Fp3 ONE();

    public abstract Fp nonresidue();

    public abstract Fp[] FrobeniusMapCoefficientsC1();

    public abstract Fp[] FrobeniusMapCoefficientsC2();
}
