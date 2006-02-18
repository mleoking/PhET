/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 18, 2006
 * Time: 1:04:57 PM
 * Copyright (c) Feb 18, 2006 by Sam Reid
 */

public class RadialIntensityReader implements DGIntensityReader {
    private DGModel dgModel;

    public RadialIntensityReader( DGModel dgModel ) {
        this.dgModel = dgModel;
    }

    public double getIntensity( double angle ) {
        Point center = dgModel.getCenterAtomPoint();
        return 0;
    }
}
