package edu.colorado.phet.bernoulli.meter;

import edu.colorado.phet.bernoulli.BernoulliModel;
import edu.colorado.phet.bernoulli.Vessel;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObservable;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;

import java.awt.geom.Point2D;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Sep 25, 2003
 * Time: 10:16:05 AM
 * To change this template use Options | File Templates.
 */
public class Barometer extends SimpleObservable implements SimpleObserver {
    double x;
    double y;
    BernoulliModel model;
    Vessel currentVessel = null;

    public Barometer( double x, double y, BernoulliModel model ) {
        this.x = x;
        this.y = y;
        this.model = model;
    }

    public double getX() {
        return x;
    }


    public void setX( double x ) {
        this.x = x;
    }


    public double getY() {
        return y;
    }

    public void setY( double y ) {
        this.y = y;
    }

    public Point2D.Double getLocation() {
        return new Point2D.Double( x, y );
    }

    public void translate( double dx, double dy ) {
        this.x += dx;
        this.y += dy;
        updateObservers();
    }

    public double getPressure() {
        Vessel v = model.vesselAt( x, y );
        if( v != currentVessel ) {
            if( currentVessel != null ) {
                currentVessel.removeObserver( this );
            }
            currentVessel = v;
            if( currentVessel != null ) {
                currentVessel.addObserver( this );
            }

        }
        if( v != null ) {
            return v.getPressure( x, y );
        }
        return 0;
    }

    public void update() {
        updateObservers();
    }

}
