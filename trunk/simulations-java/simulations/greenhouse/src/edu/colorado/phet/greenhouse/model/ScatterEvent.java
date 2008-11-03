/**
 * Class: ScatterEvent
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 17, 2003
 */
package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;
import java.util.Observable;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.greenhouse.BaseGreenhouseModule;
import edu.colorado.phet.greenhouse.GreenhouseConfig;

public class ScatterEvent extends Observable implements ModelElement {
    Point2D.Double location;
    private double radius;
    private BaseGreenhouseModule module;

    public ScatterEvent( Photon photon, BaseGreenhouseModule module ) {
        this.module = module;
        this.location = new Point2D.Double( photon.getLocation().x, photon.getLocation().y );
        this.radius = GreenhouseConfig.photonRadius * 1.5;
    }

    public Point2D.Double getLocation() {
        return location;
    }

    public double getRadius() {
        return radius;
    }

    public void stepInTime( double dt ) {
        this.radius -= .04;//t*radiusShrinkSpeed;
        if ( radius <= 0 ) {
            module.removeScatterEvent( this );
        }
        setChanged();
        notifyObservers();
    }
}
