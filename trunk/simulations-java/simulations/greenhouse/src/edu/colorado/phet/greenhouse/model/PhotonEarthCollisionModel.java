// Copyright 2002-2012, University of Colorado

/**
 * Class: PhotonEarthCollisionModel
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 10, 2003
 */
package edu.colorado.phet.greenhouse.model;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;

public class PhotonEarthCollisionModel {

    private static MutableVector2D loa = new MutableVector2D();

    public static void handle( Photon photon, Earth earth ) {

        double separation = Math.abs( photon.getLocation().distance( earth.getLocation() ) );
        if ( separation <= Earth.radius ) {
            loa.setComponents( (float) ( photon.getLocation().getX() - earth.getLocation().getX() ),
                               (float) ( photon.getLocation().getY() - earth.getLocation().getY() ) );
            earth.absorbPhoton( photon );
        }

        if ( earth.getReflectivity( photon ) >= Math.random() ) {
            photon.setVelocity( photon.getVelocity().getX(), -photon.getVelocity().getY() );
        }
    }
}
