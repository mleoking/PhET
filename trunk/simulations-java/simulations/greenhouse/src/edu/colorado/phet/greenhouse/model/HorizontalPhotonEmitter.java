// Copyright 2002-2011, University of Colorado

/**
 * Class: HorizontalPhotonEmitter
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 10, 2003
 */
package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.photonabsorption.model.Photon;


public class HorizontalPhotonEmitter extends AbstractPhotonEmitter {

    private Rectangle2D.Double bounds;
    private double wavelength;

    public HorizontalPhotonEmitter( Rectangle2D.Double bounds, double wavelength ) {
        this.bounds = bounds;
        this.wavelength = wavelength;
    }

    public Photon emitPhoton() {
        Photon photon = new Photon( wavelength, this );
        photon.setDirection( 3 * Math.PI / 2 );

        double x = bounds.getX() + Math.random() * bounds.getWidth();
        double y = bounds.getY() + Math.random() * bounds.getHeight();
        photon.setLocation( x, y );
        return photon;
    }

}
