// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Stores multiple instances of a single type of molecule. Keeps track of quantity, and has a desired capacity.
 */
public class CollectionBox {
    private final CompleteMolecule moleculeType;
    private int capacity; // how many molecules need to be put in to be complete
    public final Property<Integer> quantity = new Property<Integer>( 0 ); // start with zero
    private Rectangle2D dropBounds; // calculated by the view

    private List<Listener> listeners = new LinkedList<Listener>();

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

    public boolean isFull() {
        return capacity == quantity.getValue();
    }

    /**
     * Whether this molecule can be dropped into this collection box (at this point in time)
     *
     * @param moleculeStructure The molecule's structure
     * @return Whether it can be dropped in
     */
    public boolean willAllowMoleculeDrop( MoleculeStructure moleculeStructure ) {
        return getMoleculeType().getMoleculeStructure().isEquivalent( moleculeStructure ) && quantity.getValue() < capacity;
    }

    public void addMolecule( MoleculeStructure molecule ) {
        quantity.setValue( quantity.getValue() + 1 );

        // notify our listeners
        for ( Listener listener : listeners ) {
            listener.onAddedMolecule( molecule );
        }
    }

    public void removeMolecule( MoleculeStructure molecule ) {
        quantity.setValue( quantity.getValue() - 1 );

        // notify our listeners
        for ( Listener listener : listeners ) {
            listener.onRemovedMolecule( molecule );
        }
    }

    /**
     * Called when a molecule that can fit in this box is created
     *
     * @param moleculeStructure The molecule structure that was created
     */
    public void onAcceptedMoleculeCreation( MoleculeStructure moleculeStructure ) {
        for ( Listener listener : listeners ) {
            listener.onAcceptedMoleculeCreation( moleculeStructure );
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public static interface Listener {
        public void onAddedMolecule( MoleculeStructure moleculeStructure );

        public void onRemovedMolecule( MoleculeStructure moleculeStructure );

        public void onAcceptedMoleculeCreation( MoleculeStructure moleculeStructure );
    }

    public static class Adapter implements Listener {
        public void onAddedMolecule( MoleculeStructure moleculeStructure ) {
        }

        public void onRemovedMolecule( MoleculeStructure moleculeStructure ) {
        }

        public void onAcceptedMoleculeCreation( MoleculeStructure moleculeStructure ) {
        }
    }

}
