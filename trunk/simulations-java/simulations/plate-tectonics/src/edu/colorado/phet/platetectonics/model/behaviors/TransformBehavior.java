// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.Sample;

public class TransformBehavior extends PlateBehavior {

    private final boolean towardsFront;

    public TransformBehavior( PlateMotionPlate plate, PlateMotionPlate otherPlate, boolean towardsFront ) {
        super( plate, otherPlate );
        this.towardsFront = towardsFront;
    }

    @Override public void stepInTime( float millionsOfYears ) {
        getPlate().shiftZ( 30000f / 2 * ( towardsFront ? millionsOfYears : -millionsOfYears ) );

        // add in the rift valley
        final float delta = -millionsOfYears * 100;
        getTerrain().shiftColumnElevation( getOppositeSide().getIndex( getTerrain().getNumColumns() ), delta );
        final Sample edgeSample = getCrust().getTopBoundary().getEdgeSample( getOppositeSide() );
        edgeSample.setPosition( edgeSample.getPosition().plus( ImmutableVector3F.Y_UNIT.times( delta ) ) );

        getPlate().getTerrain().elevationChanged.updateListeners();
    }
}
