/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.model;

import edu.colorado.phet.dischargelamps.model.ElectronSource;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.collision.Collidable;

import java.awt.geom.Point2D;
import java.awt.geom.Line2D;

/**
 * PhotoelectricTarget
 * <p>
 * The plate in the photoelectric model that is bombarded with light
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricTarget extends ElectronSource {
    private Line2D line;


    public PhotoelectricTarget( BaseModel model, Point2D p1, Point2D p2 ) {
        super( model, p1, p2 );
        line = new Line2D.Double( p1, p2 );
    }

    /**
     * Produces an electron of appropriate energy if the specified photon has enough energy.
     * @param photon
     */
    public void handlePhotonCollision( Photon photon ) {
        System.out.println( "Electron produced" );
    }

    /**
     * Tells if the target has been hit by a specified photon in the last time step
      * @param photon
     * @return
     */
    public boolean isHitByPhoton( Photon photon ) {
        boolean result = line.intersectsLine( photon.getPosition().getX(), photon.getPosition().getY(),
                                 photon.getPositionPrev().getX(), photon.getPositionPrev().getY() );
        return result;
    }
}
