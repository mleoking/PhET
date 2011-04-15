// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Stores multiple instances of a single type of molecule. Keeps track of quantity, and has a desired capacity.
 */
public class CollectionBox {
    private final CompleteMolecule moleculeType;
    private int capacity; // how many molecules need to be put in to be complete
    public final Property<Integer> quantity = new Property<Integer>( 0 ); // start with zero
    private Rectangle2D dropBounds; // calculated by the view

    public CollectionBox( CompleteMolecule moleculeType, int capacity ) {
        this.moleculeType = moleculeType;
        this.capacity = capacity;
    }

    public CompleteMolecule getMoleculeType() {
        return moleculeType;
    }

    public int getCapacity() {
        return capacity;
    }

    public Property<Integer> getQuantity() {
        return quantity;
    }

    public void setDropBounds( Rectangle2D modelDropBounds ) {
        this.dropBounds = modelDropBounds;
    }

    public Rectangle2D getDropBounds() {
        return dropBounds;
    }

    public void addMolecule( MoleculeStructure molecule ) {
        quantity.setValue( quantity.getValue() + 1 );
    }
}
