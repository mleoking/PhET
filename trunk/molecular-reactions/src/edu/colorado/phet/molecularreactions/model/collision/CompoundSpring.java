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

import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.molecularreactions.model.CompositeBody;

import java.awt.geom.Point2D;
import java.awt.geom.Line2D;

/**
 * CompoundSpring
 * <p/>
 * A spring that has bodies attached at both ends. It is modeled as two
 * simple springs whose fixed ends are at the CM of the two bodies.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CompoundSpring extends Body implements ModelElement {

    double k;
    double omega;
    double phi;
    double A;
    double restingLength;
    private double angle;
    Line2D extent;
    Body attachedBody;
    double t;
    private Spring[] componentSprings;
    private CompositeBody cb;

    /**
     * @param pe            The amount of energy stored in the spring when it's deformed by a specified length
     * @param dl            The specified length of deformation
     * @param restingLength
     * @param bodies        An array of two Body instances
     */
    public CompoundSpring( double pe, double dl, double restingLength, Body[] bodies ) {
        this( 2 * pe / ( dl * dl ), restingLength, bodies );
    }

    public CompoundSpring( double k, double restingLength, Body[] bodies ) {
        this.k = k;
        this.restingLength = restingLength;
        angle = new Vector2D.Double( bodies[0].getPosition(), bodies[1].getPosition() ).getAngle();
        extent = new Line2D.Double();

        // Create component springs
        componentSprings = createComponentSprings( bodies );
    }

    private Spring[] createComponentSprings( Body[] bodies ) {
        Spring[] springs = new Spring[2];

        // Find the CM of the bodies. This will be the fixed point
        // of the two springs
        cb = new CompositeBody();
        cb.addBody( bodies[0] );
        cb.addBody( bodies[1] );
        Point2D fixedPt = cb.getCM();

        // Compute the spring constants for the two component springs
        double k0 = k * cb.getMass() / bodies[1].getMass();
        double k1 = k * cb.getMass() / bodies[0].getMass();

        // Compute the resting lengths of the springs
        double rl0 = restingLength * bodies[1].getMass() / cb.getMass();
        double rl1 = restingLength * bodies[0].getMass() / cb.getMass();

        // Compute the angles of the springs
        double angle0 = new Vector2D.Double( fixedPt, bodies[0].getPosition() ).getAngle();
        double angle1 = new Vector2D.Double( fixedPt, bodies[1].getPosition() ).getAngle();

        // Make the component springs
        springs[0] = new Spring( k0, rl0, fixedPt, angle0 );
        springs[1] = new Spring( k1, rl1, fixedPt, angle1 );

        // Attache the bodies to the springs
        springs[0].attachBody( bodies[0] );
        springs[1].attachBody( bodies[1] );

        return springs;
    }

    public void setK( double k ) {
        this.k = k;
    }

    public double getK() {
        return k;
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        for( int i = 0; i < componentSprings.length; i++ ) {
            componentSprings[i].stepInTime( dt );
        }
        notifyObservers();
    }

    public double getRestingLength() {
        return restingLength;
    }

    public double getPotentialEnergy() {
        double pe = 0;
        for( int i = 0; i < componentSprings.length; i++ ) {
            pe += componentSprings[i].getPotentialEnergy();
        }
        return pe;
    }

    public Point2D getCM() {
        return cb.getCM();
    }

    public double getMomentOfInertia() {
        return cb.getMomentOfInertia();
    }
}
