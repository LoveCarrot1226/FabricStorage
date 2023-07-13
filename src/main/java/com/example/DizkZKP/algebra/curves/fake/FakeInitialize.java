/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package com.example.DizkZKP.algebra.curves.fake;

import com.example.DizkZKP.algebra.curves.fake.fake_parameters.FakeGTParameters;

public class FakeInitialize {

    static FakeGTParameters GTParameters;

    public static void init() {
        GTParameters = new FakeGTParameters();
    }
}
