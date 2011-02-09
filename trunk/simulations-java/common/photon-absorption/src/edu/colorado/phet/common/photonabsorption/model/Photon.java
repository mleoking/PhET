// Copyright 2002-2011, University of Colorado

/**
 * Class: Photon
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.common.photonabsorption.model;

import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.photonabsorption.view.PhotonNode;
import edu.colorado.phet.greenhouse.model.PhotonEmitter;

public class Photon extends Observable {

    private static final double speedOfLight = 2.99798458E8;

    private double wavelength;
    private PhotonEmitter source;
    private Point2D.Double location;
    private double radius;
    private float vx;
    private float vy;

    public Photon( double wavelength, PhotonEmitter source ) {
        location = new Point2D.Double();
        radius = 0.1;
        this.wavelength = wavelength;
        this.source = source;
    }

    public void setDirection( double theta ) {
        setVelocity( (float) ( speedOfLight * Math.cos( theta ) ),
                     (float) ( speedOfLight * Math.sin( theta ) ) );
    }

    public void setVelocity( float vx, float vy ) {
        this.vx = vx;
        this.vy = vy;
    }

    public double getWavelength() {
        return wavelength;
    }

    public PhotonEmitter getSource() {
        return source;
    }

//    public void leaveSystem() {
//        deleteObservers();
//    }

    public Point2D.Double getLocation() {
        return location;
    }

    public void setLocation( double x,double y) {
        this.location.setLocation( x,y);
        setChanged();
        notifyObservers();
    }

    public void stepInTime( double dt ) {
        setLocation( location.x + vx * dt, location.y + vy * dt  );
    }
}
