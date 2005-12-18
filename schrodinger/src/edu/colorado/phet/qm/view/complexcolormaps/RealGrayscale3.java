/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.complexcolormaps;

import edu.colorado.phet.qm.model.Complex;

/**
 * User: Sam Reid
 * Date: Dec 17, 2005
 * Time: 5:19:02 PM
 * Copyright (c) Dec 17, 2005 by Sam Reid
 */

public class RealGrayscale3 extends ComponentGrayscale3 {
    protected double getComponent( Complex value ) {
        return value.getReal();
    }
}
