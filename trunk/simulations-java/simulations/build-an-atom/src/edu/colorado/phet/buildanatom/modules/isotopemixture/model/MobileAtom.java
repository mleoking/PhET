/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.isotopemixture.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;

/**
 * This class represents an atom that can move around but is otherwise
 * immutable.  It was created due to a need to represent atoms as particles
 * (generally a sphere) that moves around in model space.  At the time of its
 * creation, this class is only used in the Isotopes flavor of this
 * simulation.
 *
 * @author John Blanco
 */
public class MobileAtom {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    private final ImmutableAtom atomConfiguration;
    private final double radius;
    private final Point2D position = new Point2D.Double();

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

    public MobileAtom( int numProtons, int numNeutrons, double radius, Point2D initialPosition ){
        atomConfiguration = new ImmutableAtom( numProtons, numNeutrons, numProtons );
        this.radius = radius;
        position.setLocation( initialPosition );
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
    public double getRadius(){
        return radius;
    }

    public Point2D getPosition(){
        return new Point2D.Double( position.getX(), position.getY() );
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
