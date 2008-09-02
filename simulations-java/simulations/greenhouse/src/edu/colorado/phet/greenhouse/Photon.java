/**
 * Class: Photon
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.geom.Point2D;

import edu.colorado.phet.greenhouse.coreadditions.Disk;

public class Photon extends Disk {

    private static final double speedOfLight = GreenhouseConfig.speedOfLight;
    public static final double h = GreenhouseConfig.h;
    public static final double C = GreenhouseConfig.C;

    private double wavelength;
    private double energy;
    private PhotonEmitter source;

    public Photon( double wavelength, PhotonEmitter source ) {
        super( new Point2D.Double(), GreenhouseConfig.photonRadius );
        this.wavelength = wavelength;
        this.source = source;
        this.energy = h * C / wavelength;
    }

    protected void finalize() throws Throwable {
        super.finalize();
    }

    public void setDirection( double theta ) {
        super.setVelocity( (float) ( speedOfLight * Math.cos( theta ) ),
                           (float) ( speedOfLight * Math.sin( theta ) ) );
    }

    public double getMass() {
        return 1;
    }

    public double getEnergy() {
        return energy;
    }

    public double getWavelength() {
        return wavelength;
    }

    public PhotonEmitter getSource() {
        return source;
    }

    public void leaveSystem() {
        deleteObservers();
    }
}
