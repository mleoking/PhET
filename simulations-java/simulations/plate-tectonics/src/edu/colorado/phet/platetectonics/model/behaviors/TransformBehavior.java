// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import edu.colorado.phet.platetectonics.model.PlateMotionPlate;

public class TransformBehavior extends PlateBehavior {

    private final boolean towardsFront;

    public TransformBehavior( PlateMotionPlate plate, PlateMotionPlate otherPlate, boolean towardsFront ) {
        super( plate, otherPlate );
        this.towardsFront = towardsFront;
    }

    @Override public void stepInTime( float millionsOfYears ) {
        getPlate().shiftZ( 30000f / 2 * ( towardsFront ? millionsOfYears : -millionsOfYears ) );
        // TODO: remove this so we can keep terrain variation
        getPlate().fullSyncTerrain();
    }
}
