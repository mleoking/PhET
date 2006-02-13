/**
 * Class: CircularPhotonEmitter
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 10, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.geom.Point2D;

public class CircularPhotonEmitter extends AbstractPhotonEmitter {

    private Point2D.Double center;
    private double radius;
    private double wavelength;
    private double alpha;
    private double beta;

    public CircularPhotonEmitter( Point2D.Double center, double radius, double wavelength ) {
        this( center, radius, wavelength, 0, Math.PI * 2 );
    }

    public CircularPhotonEmitter( Point2D.Double center, double radius, double wavelength, double alpha, double beta ) {
        this.center = center;
        this.radius = radius;
        this.wavelength = wavelength;
        this.alpha = alpha;
        this.beta = beta;
    }

    public Photon emitPhoton() {
        double theta = Math.random() * ( beta - alpha ) + alpha;
        Photon photon = new Photon( wavelength, this );
        photon.setDirection( theta );
        photon.setLocation( center.getX() + radius * Math.cos( theta ),
                            center.getY() + radius * Math.sin( theta ));
        return photon;
    }
}
