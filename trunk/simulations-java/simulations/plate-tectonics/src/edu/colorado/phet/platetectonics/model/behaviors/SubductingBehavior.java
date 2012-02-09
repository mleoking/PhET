// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.Sample;

public class SubductingBehavior extends PlateBehavior {

    public SubductingBehavior( PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        super( plate, otherPlate );
    }

    @Override public void stepInTime( float millionsOfYears ) {
        for ( Sample sample : plate.getCrust().getSamples() ) {
            final ImmutableVector3F offsetVector = new ImmutableVector3F( millionsOfYears * 10000 * ( getPlate().isLeftPlate() ? 1 : -1 ), 0, 0 );
            sample.setPosition( sample.getPosition().plus( offsetVector ) );
        }
    }
}
