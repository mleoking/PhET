// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.solublesalts.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.solublesalts.model.ion.Ion;

/**
 * IonInitilizer
 * <p/>
 * Assigns an ion a random position within the water in the vessel, and
 * a random velocity within specified bounds.
 *
 * @author Ron LeMaster
 */
public class IonInitializer {
    private static Random random = new Random( System.currentTimeMillis() );
    private static double vMax = 3;
    private static double vMin = 1;

    public static void initialize( Ion ion, SolubleSaltsModel model ) {
        ion.setPosition( genIonPosition( ion, model ) );
        ion.setVelocity( genIonVelocity() );
    }

    /**
     * Generate a random position for an ion
     *
     * @param ion
     * @return
     */
    private static Point2D genIonPosition( Ion ion, SolubleSaltsModel model ) {
        Vessel vessel = model.getVessel();
        double x = vessel.getWater().getMinX() + ion.getRadius() * 2
                   + random.nextDouble() * ( vessel.getWater().getWidth() - ion.getRadius() * 2 );
        double y = vessel.getWater().getMinY() + ion.getRadius() * 2
                   + random.nextDouble() * ( vessel.getWater().getHeight() - ion.getRadius() * 2 );
        return new Point2D.Double( x, y );
    }

    /**
     * Generate a random velocity for an aion
     *
     * @return
     */
    private static MutableVector2D genIonVelocity() {
        return new MutableVector2D( ( random.nextDouble() * ( vMax - vMin ) + vMin ) * ( random.nextBoolean() ? 1 : -1 ),
                                    ( random.nextDouble() * ( vMax - vMin ) + vMin ) * ( random.nextBoolean() ? 1 : -1 ) );
    }

}

