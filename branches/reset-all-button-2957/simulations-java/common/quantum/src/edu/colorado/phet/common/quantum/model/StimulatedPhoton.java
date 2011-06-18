// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.quantum.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * LaserPhoton
 * <p/>
 * Extends photon in that it knows how to produce photons due to stimulated
 * emission.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class StimulatedPhoton extends Photon {

    // The bounds within which a stimulated photon must be created. This keeps them inside the
    // laser cavity
    static private Rectangle2D stimulationBounds;

    //    private static double SEPARATION=Photon.RADIUS;
    private static double SEPARATION = 9;

    public static void setStimulationBounds( Rectangle2D stimulationBounds ) {
        StimulatedPhoton.stimulationBounds = stimulationBounds;
    }

    static public Photon createStimulated( Photon stimulatingPhoton, Point2D location, Atom atom ) {
        Photon newPhoton = new Photon( stimulatingPhoton.getWavelength(), location,
                                       stimulatingPhoton.getVelocity() );
        int idx = 1;
//        double yOffset = (double)(( 1 + idx / 2 ) * 4);
        double yOffset = SEPARATION;
        int sign = idx % 2 == 0 ? 1 : -1;
        double dy = yOffset * sign * ( stimulatingPhoton.getVelocity().getX() / stimulatingPhoton.getVelocity().getMagnitude() );
        double dx = yOffset * -sign * ( stimulatingPhoton.getVelocity().getY() / stimulatingPhoton.getVelocity().getMagnitude() );
        double newY = stimulatingPhoton.getPosition().getY() + dy;
        double newX = stimulatingPhoton.getPosition().getX() + dx;

        // Keep the photon inside the cavity.
        // todo: if we get the photon graphic positioned better, this may change.

        double minY = stimulationBounds.getMinY() + Photon.RADIUS;
        double maxY = stimulationBounds.getMaxY();
        if ( newY < minY || newY > maxY ) {
            newY = atom.getPosition().getY();
            newX = atom.getPosition().getX() - Photon.RADIUS;
        }
        newPhoton.setPosition( newX, newY );

        return newPhoton;
    }

    public static double getSeparation() {
        return SEPARATION;
    }

    public static void setSeparation( double sep ) {
        StimulatedPhoton.SEPARATION = sep;
    }
}
