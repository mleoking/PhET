package edu.colorado.phet.ec3;

import edu.colorado.phet.ec3.model.spline.CubicSpline;

/**
 * User: Sam Reid
 * Date: Oct 18, 2006
 * Time: 3:01:57 AM
 * Copyright (c) Oct 18, 2006 by Sam Reid
 */

public class FloorSpline extends CubicSpline {

    public FloorSpline() {
        super( 4 );
        addControlPoint( -5, 0 );
        addControlPoint( 20, 0 );
//        setFrictionCoefficient( 0.03);
        setFrictionCoefficient( 0.01 );
    }

}
