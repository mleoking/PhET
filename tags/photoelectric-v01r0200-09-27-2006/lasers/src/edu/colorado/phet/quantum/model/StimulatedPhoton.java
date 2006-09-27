/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.quantum.model;

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

    public static void setStimulationBounds( Rectangle2D stimulationBounds ) {
        StimulatedPhoton.stimulationBounds = stimulationBounds;
    }

    static public Photon createStimulated( Photon stimulatingPhoton, Point2D location, Atom atom ) {
        Photon newPhoton = create( stimulatingPhoton.getWavelength(), location,
                                   stimulatingPhoton.getVelocity() );
        int idx = 1;
        int yOffset = ( 1 + idx / 2 ) * 4;
        int sign = idx % 2 == 0 ? 1 : -1;
        double dy = yOffset * sign * ( stimulatingPhoton.getVelocity().getX() / stimulatingPhoton.getVelocity().getMagnitude() );
        double dx = yOffset * -sign * ( stimulatingPhoton.getVelocity().getY() / stimulatingPhoton.getVelocity().getMagnitude() );
        double newY = stimulatingPhoton.getPosition().getY() + dy;
        double newX = stimulatingPhoton.getPosition().getX() + dx;

        // Keep the photon inside the cavity.
        // todo: if we get the photon graphic positioned better, this may change.

        double minY = stimulationBounds.getMinY() + Photon.RADIUS;
        double maxY = stimulationBounds.getMaxY();
        if( newY < minY || newY > maxY ) {
            newY = atom.getPosition().getY();
            newX = atom.getPosition().getX() - 10;
        }
        newPhoton.setPosition( newX, newY );

        return newPhoton;
    }
}
