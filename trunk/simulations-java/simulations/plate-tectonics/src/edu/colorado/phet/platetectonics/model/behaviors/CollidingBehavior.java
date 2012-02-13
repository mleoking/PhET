// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.Sample;

public class CollidingBehavior extends PlateBehavior {

    public CollidingBehavior( PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        super( plate, otherPlate );
    }

    @Override public void stepInTime( float millionsOfYears ) {
        float sign = plate.isLeftPlate() ? 1 : -1;
        for ( Sample crustSample : getPlate().getCrust().getSamples() ) {
            final ImmutableVector3F currentPosition = crustSample.getPosition();
            final float currentX = currentPosition.x;
            if ( currentX != 0 ) {
                final int exponentialFactor = 25;
                float newX = (float) ( currentX * Math.exp( -millionsOfYears / exponentialFactor ) );
                final float maxXDelta = 30000f / 2 * millionsOfYears;
                if ( Math.abs( newX - currentX ) > maxXDelta ) {
                    newX = currentX + sign * maxXDelta;
                }
                crustSample.setPosition( currentPosition.plus( new ImmutableVector3F( newX - currentX, 0, 0 ) ) );
            }
        }
    }
}
