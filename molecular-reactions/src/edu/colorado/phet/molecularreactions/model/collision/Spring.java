/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Spring
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Spring extends SimpleObservable implements ModelElement {

    double k;
    double omega;
    double phi;
    double A;
    double restingLength;
    private Point2D fixedEnd;
    private double angle;
    private Point2D freeEnd;
    Line2D extent;
    Body attachedBody;
    double t;

    public Spring( double k, double restingLength, Point2D fixedEnd, double angle ) {
        this.k = k;
        this.restingLength = restingLength;
        this.fixedEnd = fixedEnd;
        this.angle = angle;
        this.freeEnd = new Point2D.Double( fixedEnd.getX() + restingLength * Math.cos( angle ),
                                           fixedEnd.getY() + restingLength * Math.sin( angle ) );
        extent = new Line2D.Double();
    }

    public void setK( double k ) {
        this.k = k;
    }

    public double getK() {
        return k;
    }

    private double getElongation() {
        return fixedEnd.distance( freeEnd ) - restingLength;
    }

    public void attachBody( Body body ) {
        this.attachedBody = body;
        freeEnd.setLocation( body.getCM() );
        angle = new Vector2D.Double( fixedEnd, freeEnd ).getAngle();

        t = 0;
        omega = Math.sqrt( k / body.getMass() );

        // A and phi depend on the initial elongation of the spring and the initial
        // velocity of the attached body
        Vector2D.Double vSpring = new Vector2D.Double( fixedEnd, freeEnd );
        double v0 = MathUtil.getProjection( body.getVelocity(), vSpring ).getMagnitude();
        double c = getElongation() * omega / v0;
        double rad = Math.sqrt( 1 / ( c * c + 1));
        phi = Math.acos( rad * MathUtil.getSign( body.getVelocity().dot( vSpring) ) );
        A = getElongation() / Math.sin(phi);

//        A = getElongation() + v0 / omega;
//        phi = Math.asin( getElongation() / A );
    }

    /**
     * Gets the magnitude of the force exerted by the spring.
     *
     * @return the force exerted by the spring
     */
    public Vector2D getForce() {
        double fMag = -getElongation() * k;
        Vector2D f = new Vector2D.Double( fMag, 0 );
        f.rotate( angle );
        return f;
    }

    public Vector2D getAcceleration() {
        double aMag = -( omega * omega ) * A * Math.sin( omega * t + phi );
        Vector2D a = new Vector2D.Double( aMag, 0 );
        a.rotate( angle );
        return a;
    }

    public void stepInTime( double dt ) {
        t += dt;
        if( attachedBody != null ) {
            freeEnd.setLocation( attachedBody.getCM() );
            Vector2D v = getVelocity();
            attachedBody.setVelocity( v );
        }
        notifyObservers();
    }

    private Vector2D getVelocity() {
        Vector2D v = new Vector2D.Double( omega * A * Math.cos( omega * t + phi ), 0 );
        v.rotate( angle );
        return v;
    }

    public double getRestingLength() {
        return restingLength;
    }

    public Point2D getFixedEnd() {
        return fixedEnd;
    }

    public Point2D getFreeEnd() {
        return freeEnd;
    }

    public Line2D getExtent() {
        extent.setLine( fixedEnd, freeEnd );
        return extent;
    }

    public double getPotentialEnergy() {
        double dl = restingLength - freeEnd.distance( fixedEnd );
        return 0.5 * k * dl * dl;
    }
}
