/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package com.example.DizkZKP.algebra.curves.fake.abstract_fake_parameters;

import com.example.DizkZKP.algebra.curves.fake.FakeGT;
import com.example.DizkZKP.algebra.curves.fake.fake_parameters.FakeFqParameters;

public abstract class AbstractFakeGTParameters {

    public abstract FakeFqParameters FqParameters();

    public abstract FakeGT ONE();

}
