/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.isotopemixture.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.model.SphericalParticle;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * This class represents an atom that can move around but is otherwise
 * immutable.  It was created due to a need to represent atoms as single
 * entities rather than as a collection of particles.
 *
 * At the time of this writing, this class is used only in the Isotopes flavor
 * of this simulation.
 *
 * @author John Blanco
 */
public class MovableAtom extends SphericalParticle {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    private final ImmutableAtom atomConfiguration;
    private final double radius;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    // Property that tracks whether this atom is currently part of the larger
    // model.  This is generally watched by the view so that any view
    // representation can be removed when this is set to false.
    private final BooleanProperty partOfModelProperty = new BooleanProperty( true );

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public MovableAtom( int numProtons, int numNeutrons, double radius, Point2D initialPosition ){
        this( numProtons, numNeutrons, radius, initialPosition, null );
    }

    public MovableAtom( int numProtons, int numNeutrons, double radius, Point2D initialPosition, ConstantDtClock clock ){
        super(radius, initialPosition.getX(), initialPosition.getY(), clock);
        atomConfiguration = new ImmutableAtom( numProtons, numNeutrons, numProtons );
        this.radius = radius;
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Get the configuration of the atom in terms of protons, neutrons, and
     * electrons.  The returned type also allows retrieval of information like
     * the chemical symbol for the atom.
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
    @Override
    public double getRadius(){
        return radius;
    }

    /**
     * Method to call when this model element is removed from the larger
     * model.
     */
    public void removeFromModel(){
        partOfModelProperty.setValue( false );
    }

    /**
     * Get the property that indicates whether this model element is included
     * as part of the larger model.  This property is generally monitored by
     * the view so that any representation in the view can be removed if this
     * transitions to false.
     *
     * @return
     */
    public BooleanProperty getPartOfModelProperty(){
        return partOfModelProperty;
    }

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}
