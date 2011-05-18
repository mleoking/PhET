// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.buildamolecule.BuildAMoleculeApplication;
import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Stores multiple instances of a single type of molecule. Keeps track of quantity, and has a desired capacity.
 */
public class CollectionBox {
    private final CompleteMolecule moleculeType;
    private final int capacity; // how many molecules need to be put in to be complete
    public final Property<Integer> quantity = new Property<Integer>( 0 ); // start with zero
    private Rectangle2D dropBounds; // calculated by the view

    private List<Listener> listeners = new LinkedList<Listener>();

    public CollectionBox( CompleteMolecule moleculeType, final int capacity ) {
        this.moleculeType = moleculeType;
        this.capacity = capacity;

        addListener( new Adapter() {
            @Override public void onAddedMolecule( Molecule molecule ) {
                if ( quantity.get() == capacity ) {
                    BuildAMoleculeApplication.playCollectionBoxFilledSound();
                }
            }
        } );
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
        return capacity == quantity.get();
    }

    /**
     * Whether this molecule can be dropped into this collection box (at this point in time)
     *
     * @param moleculeStructure The molecule's structure
     * @return Whether it can be dropped in
     */
    public <U extends Atom> boolean willAllowMoleculeDrop( MoleculeStructure<U> moleculeStructure ) {
        boolean equivalent = getMoleculeType().isEquivalent( moleculeStructure );

        // whether the structure is acceptable
        return equivalent && quantity.get() < capacity;
    }

    public void addMolecule( Molecule molecule ) {
        quantity.set( quantity.get() + 1 );

        // notify our listeners
        for ( Listener listener : listeners ) {
            listener.onAddedMolecule( molecule );
        }
    }

    public void removeMolecule( Molecule molecule ) {
        quantity.set( quantity.get() - 1 );

        // notify our listeners
        for ( Listener listener : listeners ) {
            listener.onRemovedMolecule( molecule );
        }
    }

    /**
     * Called when a molecule that can fit in this box is created
     *
     * @param molecule The molecule that was created
     */
    public void onAcceptedMoleculeCreation( Molecule molecule ) {
        for ( Listener listener : listeners ) {
            listener.onAcceptedMoleculeCreation( molecule );
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public static interface Listener {
        public void onAddedMolecule( Molecule molecule );

        public void onRemovedMolecule( Molecule molecule );

        public void onAcceptedMoleculeCreation( Molecule molecule );
    }

    public static class Adapter implements Listener {
        public void onAddedMolecule( Molecule molecule ) {
        }

        public void onRemovedMolecule( Molecule molecule ) {
        }

        public void onAcceptedMoleculeCreation( Molecule molecule ) {
        }
    }

}
