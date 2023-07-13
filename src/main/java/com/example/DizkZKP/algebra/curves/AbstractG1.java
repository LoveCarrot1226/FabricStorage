/* @file
 *****************************************************************************
 * @author     This file is part of zkspark, developed by SCIPR Lab
 *             and contributors (see AUTHORS).
 * @copyright  MIT license (see LICENSE file)
 *****************************************************************************/

package com.example.DizkZKP.algebra.curves;

import com.example.DizkZKP.algebra.groups.AbstractGroup;

public abstract class AbstractG1<G1T extends AbstractG1<G1T>> extends AbstractGroup<G1T> {

    public abstract boolean isSpecial();

    public abstract int bitSize();

}