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

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.molecularreactions.model.CompositeBody;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;

import java.awt.geom.Point2D;
import java.awt.geom.Line2D;

/**
 * CompoundSpring
 * <p/>
 * A spring that has SimpleMolecules attached at both ends. It is modeled as two
 * simple springs whose fixed ends are at the closest points of the two molecules.
 *
 * todo: take account of initial compression of spring
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ReactionSpring extends Body implements ModelElement {

    double k;
    double restingLength;
    Line2D extent;
    Body attachedBody;
    double t;
    private Spring[] componentSprings;
    // The composite body formed by the two molecules attached to the spring
    private CompositeBody cb;

    /**
     * @param pe            The amount of energy stored in the spring when it's deformed by a specified length
     * @param dl            The specified length of deformation
     * @param restingLength
     * @param bodies        An array of two Body instances
     */
    public ReactionSpring( double pe, double dl, double restingLength, SimpleMolecule[] bodies, double initialLength ) {
//        this( 2 * pe / ( dl * dl ), restingLength, bodies );
        // Find the CM of the bodies. This will be the fixed point
        // of the two springs
        cb = new CompositeBody();
//        cb.addBody( bodies[0] );
//        cb.addBody( bodies[1] );
        cb.addBody( bodies[0].getFullMolecule() );
        cb.addBody( bodies[1].getFullMolecule() );
        Point2D fixedPt = cb.getCM();

        double l0 = initialLength * bodies[1].getFullMass() / cb.getMass();
        double l1 = initialLength * bodies[0].getFullMass() / cb.getMass();
//        componentSprings[0].setPhi( initialLength * l0 / componentSprings[0].getA() );
//        componentSprings[1].setPhi( initialLength * l1 / componentSprings[1].getA() );
        this.k = 2 * pe / ( dl * dl );
        this.restingLength = restingLength;
        extent = new Line2D.Double();

        // Create component springs
//        componentSprings = createComponentSprings( bodies );
        Spring[] springs = new Spring[2];

        // Compute the spring constants for the two component springs
        double k0 = k * cb.getMass() / bodies[1].getFullMolecule().getMass();
        double k1 = k * cb.getMass() / bodies[0].getFullMolecule().getMass();

        // Compute the resting lengths of the springs
        double rl0 = restingLength * bodies[1].getFullMass() / cb.getMass();
        double rl1 = restingLength * bodies[0].getFullMass() / cb.getMass();
//        double rl0 = fixedPt.distance( bodies[0].getPosition() ) - bodies[0].getRadius();
//        double rl1 = fixedPt.distance( bodies[1].getPosition() ) - bodies[1].getRadius();

        // Make the component springs, with the bodies attached
//        springs[0] = new Spring( k0, rl0, fixedPt, bodies[0] );
//        springs[1] = new Spring( k1, rl1, fixedPt, bodies[1] );
        double alpha = new Vector2D.Double( bodies[0].getPosition(), bodies[1].getPosition()).getAngle();
        double d0 = Math.max( 0, fixedPt.distance( bodies[0].getPosition() ) - bodies[0].getRadius() );
        springs[0] = new Spring( k0, rl0, fixedPt, alpha );
        springs[0].attachBodyAtSpringLength( bodies[0].getFullMolecule(),d0 );
        double d1 = Math.max( 0, fixedPt.distance( bodies[1].getPosition() ) - bodies[1].getRadius() );
        springs[1] = new Spring( k1, rl1, fixedPt, alpha + Math.PI );
        springs[1].attachBodyAtSpringLength( bodies[1].getFullMolecule(), d1 );

        componentSprings = springs;
    }

    /**
     * @param pe            The amount of energy stored in the spring when it's deformed by a specified length
     * @param dl            The specified length of deformation
     * @param restingLength
     * @param bodies        An array of two Body instances
     */
    public ReactionSpring( double pe, double dl, double restingLength, SimpleMolecule[] bodies ) {
        this( 2 * pe / ( dl * dl ), restingLength, bodies );
    }

    public ReactionSpring( double k, double restingLength, SimpleMolecule[] bodies ) {
        this.k = k;
        this.restingLength = restingLength;
        extent = new Line2D.Double();

        // Create component springs
        componentSprings = createComponentSprings( bodies );
    }

    public Spring[] getComponentSprings() {
        return componentSprings;
    }

    /**
     * Creates the springs that attach to each of the molecules
     * @param bodies
     * @return two springs
     */
    private Spring[] createComponentSprings( SimpleMolecule[] bodies ) {
        Spring[] springs = new Spring[2];

        // Find the CM of the bodies. This will be the fixed point
        // of the two springs
        cb = new CompositeBody();
        cb.addBody( bodies[0].getFullMolecule() );
        cb.addBody( bodies[1].getFullMolecule() );
        Point2D fixedPt = cb.getCM();

        // Compute the spring constants for the two component springs
        double k0 = k * cb.getMass() / bodies[1].getFullMolecule().getMass();
        double k1 = k * cb.getMass() / bodies[0].getFullMolecule().getMass();

        // Compute the resting lengths of the springs
        double rl0 = restingLength * bodies[1].getFullMass() / cb.getMass();// - bodies[0].getRadius();
        double rl1 = restingLength * bodies[0].getFullMass() / cb.getMass();//- bodies[1].getRadius();
//        double rl0 = fixedPt.distance( bodies[0].getPosition() ) - bodies[0].getRadius();
//        double rl1 = fixedPt.distance( bodies[1].getPosition() ) - bodies[1].getRadius();

        // Make the component springs, with the bodies attached
        Vector2D vRel0 = new Vector2D.Double( bodies[0].getFullMolecule().getVelocity() ).subtract( cb.getVelocity() );
        springs[0] = new Spring( k0, rl0, fixedPt, bodies[0].getFullMolecule(), vRel0 );
        Vector2D vRel1 = new Vector2D.Double( bodies[1].getFullMolecule().getVelocity() ).subtract( cb.getVelocity() );
        springs[1] = new Spring( k1, rl1, fixedPt, bodies[1].getFullMolecule(), vRel1 );
        return springs;
    }

    /**
     * Incorporates code to check for energy being conserved
     * @param dt
     */
    public void stepInTime( double dt ) {

        super.stepInTime( dt );
        double pe0  = getPotentialEnergy();
        double ke0 = 0;
        double ke1 = 0;
        for( int i = 0; i < componentSprings.length; i++ ) {
            ke0 += componentSprings[i].getAttachedBody().getKineticEnergy();
            componentSprings[i].stepInTime( dt );
            ke1 += componentSprings[i].getAttachedBody().getKineticEnergy();
        }
        double pe1 = getPotentialEnergy();
        double de =  pe1 + ke1 - (pe0 + ke0 );

        notifyObservers();
    }

    public Point2D getCM() {
        return cb.getCM();
    }

    public double getMomentOfInertia() {
        return cb.getMomentOfInertia();
    }

    public double getPotentialEnergy() {
        return componentSprings[0].getPotentialEnergy() + componentSprings[1].getPotentialEnergy();
    }
}
