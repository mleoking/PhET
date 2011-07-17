// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.common.util.Units;

/**
 * This class contains state information for Sodium Ions and permits matching in MicroModel for particle counting.
 *
 * @author Sam Reid
 */
public class SodiumIonParticle extends SphericalParticle {
    public static final double radius = Units.picometersToMeters( 102 ) * MicroModel.sizeScale;

    public SodiumIonParticle() {
        super( radius, new ImmutableVector2D(), Color.red );
    }
}
