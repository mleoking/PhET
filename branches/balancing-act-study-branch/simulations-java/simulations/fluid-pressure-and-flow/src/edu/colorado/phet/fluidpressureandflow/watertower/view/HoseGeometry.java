// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.fluidpressureandflow.watertower.model.Hose;

/**
 * Values for the shape of the hose.  Computed all at once for convenience and re-usability.
 *
 * @author Sam Reid
 */
public class HoseGeometry {

    //See docs in HoseNode constructor
    public final Vector2D startPoint;//initial location where the hose starts
    public final Vector2D nozzleInput;//point where the hose enters the nozzle
    public final Vector2D delta;//vector from the hose output point to the nozzle input point
    public final Vector2D prePoint;//point before the nozzle input point to ensure a straight path when entering the nozzle
    public final Vector2D rightOfTower; //Point just to the right of the hole in the water tower
    public final Vector2D bottomLeft;//Bottom left of the u-shape of the hose

    public HoseGeometry( Hose hose ) {
        startPoint = new Vector2D( hose.attachmentPoint.get().getX(), hose.attachmentPoint.get().getY() + hose.holeSize / 2 );
        nozzleInput = hose.getNozzleInputPoint();
        delta = hose.getNozzleInputPoint().minus( hose.outputPoint.get() );
        prePoint = hose.getNozzleInputPoint().plus( delta );

        rightOfTower = new Vector2D( startPoint.getX() + 2, startPoint.getY() );
        bottomLeft = new Vector2D( rightOfTower.getX(), prePoint.getY() );
    }

    public Vector2D getHandlePoint() {
        return bottomLeft.plus( prePoint ).times( 0.5 );
    }
}