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

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.molecularreactions.model.CompositeMolecule;
import edu.colorado.phet.molecularreactions.model.PotentialEnergySource;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Spring
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Spring extends Particle implements ModelElement, PotentialEnergySource {

    double k;
    double omega;
    double phi;
    double A;
    double restingLength;
    private Point2D fixedEnd;
    private double angle;
    private Point2D freeEnd = new Point2D.Double();
    Line2D extent;
    Body attachedBody;
    double t;
    private double pe;

    /**
     * @param pe            The amount of energy stored in the spring when it's deformed by a specified length
     * @param dl            The specified length of deformation
     * @param restingLength
     * @param fixedEnd
     * @param angle
     */
    public Spring( double pe, double dl, double restingLength, Point2D fixedEnd, double angle ) {
        this( 2 * pe / ( dl * dl ), restingLength, fixedEnd, angle );
    }

    /**
     *
     * @param k
     * @param restingLength
     * @param fixedEnd
     * @param body
     * @param vRel  The velocity of the body relative to the fixed point of the spring
     */
    public Spring( double k, double restingLength, Point2D fixedEnd, Body body, Vector2D vRel ) {
        this( k, restingLength, fixedEnd );
        attachBodyAtRestingLength( body, vRel );
    }

    /**
     *
     * @param pe
     * @param dl
     * @param restingLength
     * @param fixedEnd
     * @param body
     * @param vRel  The velocity of the body relative to the fixed point of the spring
     */
    public Spring( double pe, double dl, double restingLength, Point2D fixedEnd, Body body, Vector2D vRel ) {
        this( 2 * pe / ( dl * dl ), restingLength, fixedEnd );
        attachBodyAtRestingLength( body, vRel );
    }

    /**
     *
     * @param k
     * @param restingLength
     * @param fixedEnd
     * @param angle
     */
    public Spring( double k, double restingLength, Point2D fixedEnd, double angle ) {
        this( k, restingLength, fixedEnd );
        setAngle( angle, fixedEnd, restingLength );
    }

    /**
     *
     * @param k
     * @param restingLength
     * @param fixedEnd
     */
    public Spring( double k, double restingLength, Point2D fixedEnd ) {
        this.k = k;
        this.restingLength = restingLength;
        this.fixedEnd = fixedEnd;
        extent = new Line2D.Double();
    }

    private void setAngle( double angle, Point2D fixedEnd, double restingLength ) {
        this.angle = angle;
        this.freeEnd = new Point2D.Double( fixedEnd.getX() + restingLength * Math.cos( angle ),
                                           fixedEnd.getY() + restingLength * Math.sin( angle ) );
    }

    public void setK( double k ) {
        this.k = k;
    }

    public double getK() {
        return k;
    }

    public void setPhi( double phi ) {
        this.phi = phi;
    }

    public double getA() {
        return A;
    }

    private double getElongation() {
        return fixedEnd.distance( freeEnd ) - restingLength;
    }

    /**
     * Assumes that the body is being attached to the spring, and assume the spring is at its resting
     * length. A virtual strut is assumed to exist between the body's CM and the end of the spring.
     *
     * @param body
     * @param vRel  The velocity of the body relative to the fixed point of the spring
     */
    private void attachBodyAtRestingLength( Body body, Vector2D vRel ) {
        this.attachedBody = body;

        if( body instanceof CompositeMolecule ) {
//            System.out.println( "Spring.attachBodyAtRestingLength" );
        }
        angle = new Vector2D.Double( fixedEnd, body.getPosition() ).getAngle();
        freeEnd.setLocation( fixedEnd.getX() + restingLength * Math.cos( angle ), fixedEnd.getY() + restingLength * Math.sin( angle ) );
        t = 0;
        omega = Math.sqrt( k / body.getMass() );

        if( Double.isNaN( omega ) ) {
//            System.out.println( "Spring.attachBodyAtRestingLength" );
        }

        // A and phi depend on the initial elongation of the spring and the initial
        // velocity of the attached body
        Vector2D.Double vSpring = new Vector2D.Double( fixedEnd, freeEnd );
        double v0 = MathUtil.getProjection( vRel, vSpring ).getMagnitude();
//        double v0 = MathUtil.getProjection( body.getVelocity(), vSpring ).getMagnitude();
        phi = 0;
        A = v0 / ( omega * Math.cos( phi ) );
    }

    /**
     * Assumes that the body is being attached to the spring, and assume the spring is at its resting
     * length. A virtual strut is assumed to exist between the body's CM and the end of the spring.
     *
     * @param body
     */
    public void attachBodyAtSpringLength( Body body, double length ) {
        this.attachedBody = body;

        angle = new Vector2D.Double( fixedEnd, body.getPosition() ).getAngle();
        freeEnd.setLocation( fixedEnd.getX() + length * Math.cos( angle ), fixedEnd.getY() + length * Math.sin( angle ) );
        t = 0;
        omega = Math.sqrt( k / body.getMass() );

        if( Double.isNaN( omega ) ) {
            System.out.println( "Spring.attachBodyAtRestingLength" );
        }

        // A and phi depend on the initial elongation of the spring and the initial
        // velocity of the attached body
//        Vector2D.Double vSpring = new Vector2D.Double( fixedEnd, freeEnd );

        double v0 = body.getVelocity().getMagnitude();
        if( v0 != 0 ) {
            v0 *= Math.cos( body.getVelocity().getAngle() - angle );
        }
        double dl = restingLength - length;
        double dlSq = dl * dl;
        double omSq = omega * omega;
        double g = Math.sqrt( dlSq * omSq / ( v0*v0 + dlSq * omSq ));// * -MathUtil.getSign( dl );
        phi = Math.asin( g );
        if( Math.abs( phi % Math.PI /2 ) < Math.PI / 4  ) {
            A = v0 / ( omega * Math.cos( phi ) );
        }
        else {
            A = ( restingLength - length ) / Math.sin( phi );
        }
    }

    /**
     * NOTE: THIS IS NOT WORKING PROPERLY
     *
     * @param body
     */
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
        double x0 = getElongation();
        double rad = ( v0 * v0 ) / ( ( x0 * x0 * omega * omega ) + ( v0 * v0 ) );
        phi = Math.acos( Math.sqrt( rad ) );
        A = v0 / ( omega * Math.cos( phi ) );

        A = x0 / Math.sin( phi );
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

    /**
     * Updates the length of the spring, the potential energy in the spring, and the velocity of the
     * attached body
     * @param dt
     */
    public void stepInTime( double dt ) {
        Vector2D v0 = getVelocity();
        t += dt;
        if( attachedBody != null ) {

            if( attachedBody instanceof CompositeMolecule ) {
//                System.out.println( "Spring.stepInTime" );
            }

            // Compute the length of the spring
            double dl = A * Math.sin( omega * t + phi );
            double l = restingLength - dl;

            // Compute the potential energy in the spring
            pe = 0.5 * k * dl * dl;
//            System.out.println( "pe = " + pe );

            // Compute the location of the free end
            freeEnd.setLocation( fixedEnd.getX() + l * Math.cos( angle ), fixedEnd.getY() + l * Math.sin( angle ) );

            // Update the velocity of the attached body
            Vector2D v = getFreeEndVelocity();
            attachedBody.setVelocity( v );
//            attachedBody.setVelocity( attachedBody.getVelocity().add( v.subtract( v0 ) ) );
        }
        notifyObservers();
    }

    private Vector2D getFreeEndVelocity() {
        Vector2D v = new Vector2D.Double( -omega * A * Math.cos( omega * t + phi ), 0 );
        v.rotate( angle );

//        v.add( getVelocity() );

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

    public double getLength() {
        return fixedEnd.distance( freeEnd );
    }

    public Line2D getExtent() {
        extent.setLine( fixedEnd, freeEnd );
        return extent;
    }

    public double getPotentialEnergy() {
        double dl = restingLength - (fixedEnd.distance( freeEnd ));
        pe = k * dl * dl / 2;
        return pe;
    }

    public Body getAttachedBody() {
        return attachedBody;
    }

    public double getPE() {
        return getPotentialEnergy();
    }

    public void setFixedEnd( Point2D position ) {
        fixedEnd = position;
    }
}
