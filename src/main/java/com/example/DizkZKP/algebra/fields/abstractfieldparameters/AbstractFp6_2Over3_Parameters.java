/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package com.example.DizkZKP.algebra.fields.abstractfieldparameters;

import com.example.DizkZKP.algebra.fields.Fp;
import com.example.DizkZKP.algebra.fields.Fp6_2Over3;

public abstract class AbstractFp6_2Over3_Parameters {

    public abstract AbstractFpParameters FpParameters();

    public abstract AbstractFp3Parameters Fp3Parameters();

    public abstract Fp6_2Over3 ZERO();

    public abstract Fp6_2Over3 ONE();

    public abstract Fp nonresidue();

    public abstract Fp[] FrobeniusMapCoefficientsC1();
}
