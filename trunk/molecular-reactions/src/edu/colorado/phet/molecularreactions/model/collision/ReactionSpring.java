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
import edu.colorado.phet.molecularreactions.model.AbstractMolecule;
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
    double omega;
    double phi;
    double A;
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
//        cb.addBody( bodies[0] );
//        cb.addBody( bodies[1] );
        cb.addBody( bodies[0].getFullMolecule() );
        cb.addBody( bodies[1].getFullMolecule() );
        Point2D fixedPt = cb.getCM();

        // Compute the spring constants for the two component springs
        double k0 = k * cb.getMass() / bodies[1].getFullMolecule().getMass();
        double k1 = k * cb.getMass() / bodies[0].getFullMolecule().getMass();

        // Compute the resting lengths of the springs
        double rl0 = fixedPt.distance( bodies[0].getPosition() ) - bodies[0].getRadius();
        double rl1 = fixedPt.distance( bodies[1].getPosition() ) - bodies[1].getRadius();

        // Make the component springs, with the bodies attached
        springs[0] = new Spring( k0, rl0, fixedPt, bodies[0].getFullMolecule() );
        springs[1] = new Spring( k1, rl1, fixedPt, bodies[1].getFullMolecule() );
        return springs;
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        for( int i = 0; i < componentSprings.length; i++ ) {
            componentSprings[i].stepInTime( dt );
        }
        notifyObservers();
    }

    public Point2D getCM() {
        return cb.getCM();
    }

    public double getMomentOfInertia() {
        return cb.getMomentOfInertia();
    }
}
