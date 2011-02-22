/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.isotopemixture.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;

/**
 * This class represents an atom that can move around but is otherwise
 * immutable.  It was created due to a need to represent atoms as particles,
 * i.e. a sphere, that moves around in model space.  At the time of its
 * creation, it is only used in the Isotopes flavor of this simulation.
 *
 * @author John Blanco
 */
public class MobileAtom {

    private final ImmutableAtom atomConfiguration;
    private final double radius;
    private final Point2D position = new Point2D.Double();

    public MobileAtom( int numProtons, int numNeutrons, double radius, Point2D initialPosition ){
        atomConfiguration = new ImmutableAtom( numProtons, numNeutrons, numProtons );
        this.radius = radius;
        position.setLocation( initialPosition );
    }

    /**
     * Get the configuration of the atom in terms of protons, neturons,
     * and electrons.  The returned type also allows retrieval of information
     * like the chemical symbol for the atom.
     *
     * @return
     */
    public ImmutableAtom getAtomConfiguration(){
        return atomConfiguration;
    }

    /**
     * Get the radius of this atom.
     * @return
     */
    public double getRadius(){
        return radius;
    }

    public Point2D getPosition(){
        return new Point2D.Double( position.getX(), position.getY() );
    }
}
