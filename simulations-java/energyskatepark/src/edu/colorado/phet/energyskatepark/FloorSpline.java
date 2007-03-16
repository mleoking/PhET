package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.energyskatepark.model.spline.CubicSpline;

/**
 * User: Sam Reid
 * Date: Oct 18, 2006
 * Time: 3:01:57 AM
 * Copyright (c) Oct 18, 2006 by Sam Reid
 */

public class FloorSpline extends CubicSpline {

    public FloorSpline() {
        super( 4 );
        addControlPoint( -100, 0 );
        addControlPoint( 100, 0 );
        setFrictionCoefficient( 0.03 );
//        setFrictionCoefficient( 0.01 );
    }

}
