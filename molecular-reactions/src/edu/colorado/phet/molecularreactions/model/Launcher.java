/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;

/**
 * Launcher
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Launcher extends Body implements ModelElement, PotentialEnergySource {

    private Point2D restingTipLocation;
    private double maxPlungerDraw;
    private double springK = 0.3;
    private Body bodyToLaunch;

    public Launcher( Point2D restingTipLocation ) {
        this.restingTipLocation = restingTipLocation;
    }

    public void setBodyToLaunch( Body bodyToLaunch ) {
        this.bodyToLaunch = bodyToLaunch;
        bodyToLaunch.setVelocity( 0,0 );
    }

    public void release() {
        if( bodyToLaunch != null ) {
            double s = Math.sqrt( 2 * getPE() / bodyToLaunch.getMass() );
            Vector2D v = new Vector2D.Double( 0, -s );
            bodyToLaunch.setVelocity( v );
        }
        setTipLocation( restingTipLocation );
    }

    public double getPE() {
        double dl = getTipLocation().distance( restingTipLocation );
        double pe = springK * dl * dl / 2;
        return pe;
    }

    public Point2D getTipLocation() {
        return getPosition();
    }

    public void stepInTime( double dt ) {

    }

    public Point2D getCM() {
        return null;
    }

    public double getMomentOfInertia() {
        return 0;
    }

    public Point2D getRestingTipLocation() {
        return restingTipLocation;
    }

    public void setTipLocation( Point2D p ) {
        setTipLocation( p.getX(), p.getY() );
    }

    public void setTipLocation( double x, double y ) {
        setPosition( x, y );
    }
}
