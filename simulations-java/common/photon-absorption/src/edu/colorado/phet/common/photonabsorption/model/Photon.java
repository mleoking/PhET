// Copyright 2002-2011, University of Colorado

/**
 * Class: Photon
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.common.photonabsorption.model;

import java.awt.geom.Point2D;
import java.util.Observable;

/**
 * Class that represents a photon.  Primarily keeps track of wavelength,
 * position, and velocity (as odd as that may seem) and can be stepped in
 * order to make the photon move in model space.
 *
 * @author John Blanco
 */
public class Photon extends Observable {

    private final double wavelength;
    private final Point2D location;
    private double vx;
    private double vy;

    public Photon( double wavelength ) {
        location = new Point2D.Double();
        this.wavelength = wavelength;
    }

    public void setVelocity( double vx, double vy ) {
        this.vx = vx;
        this.vy = vy;
    }

    public double getWavelength() {
        return wavelength;
    }

    public Point2D getLocation() {
        return location;
    }

    public void setLocation( double x, double y) {
        this.location.setLocation( x, y );
        setChanged();
        notifyObservers();
    }

    public void stepInTime( double dt ) {
        setLocation( location.getX() + vx * dt, location.getY() + vy * dt  );
    }
}
