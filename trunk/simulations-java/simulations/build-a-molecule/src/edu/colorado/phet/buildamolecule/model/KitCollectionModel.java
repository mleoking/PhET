// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Represents a main running model for the 1st two tabs. Contains a collection of kits and boxes. Kits are responsible
 * for their buckets and atoms.
 */
public class KitCollectionModel {
    private List<Kit> kits = new LinkedList<Kit>();
    private List<CollectionBox> boxes = new LinkedList<CollectionBox>();

    private LayoutBounds layoutBounds;//picometers
    private Property<Kit> currentKit;

    public final Property<Boolean> allCollectionBoxesFilled = new Property<Boolean>( false ); // this will remain false if we have no collection boxes

    public KitCollectionModel( LayoutBounds layoutBounds ) {
        this.layoutBounds = layoutBounds;
    }

    public void addKit( final Kit kit ) {
        if ( currentKit == null ) {
            currentKit = new Property<Kit>( kit );
            kit.show();

            // handle kit visibility when this changes
            currentKit.addObserver( new ChangeObserver<Kit>() {
                public void update( Kit newValue, Kit oldValue ) {
                    newValue.show();
                    oldValue.hide();
                }
            } );
        }
        else {
            kit.hide();
        }
        kits.add( kit );

        for ( Atom2D atomModel : kit.getAtoms() ) {
            atomModel.addListener( new Atom2D.Adapter() {
                @Override
                public void droppedByUser( Atom2D atom ) {
                    boolean wasDroppedInCollectionBox = false;

                    // don't drop an atom from the kit to the collection box directly
                    if ( kit.isAtomInPlay( atom ) ) {
                        Molecule molecule = kit.getMolecule( atom );

                        // check to see if we are trying to drop it in a collection box.
                        for ( CollectionBox box : boxes ) {

                            // permissive, so that if the box bounds and molecule bounds intersect, we call it a "hit"
                            if ( box.getDropBounds().intersects( molecule.getPositionBounds() ) ) {

                                // if our box takes this type of molecule
                                if ( box.willAllowMoleculeDrop( molecule ) ) {
                                    kit.moleculePutInCollectionBox( molecule, box );
                                    wasDroppedInCollectionBox = true;
                                    break;
                                }
                            }
                        }
                    }

                    if ( !wasDroppedInCollectionBox ) {
                        kit.atomDropped( atom );
                    }
                }
            } );
        }

        kit.addMoleculeListener( new Kit.MoleculeAdapter() {
            @Override
            public void addedMolecule( Molecule molecule ) {
                for ( CollectionBox box : getCollectionBoxes() ) {
                    if ( box.willAllowMoleculeDrop( molecule ) ) {
                        box.onAcceptedMoleculeCreation( molecule );
                    }
                }
            }
        } );
    }

    public CollectionBox getFirstTargetBox( Molecule molecule ) {
        for ( CollectionBox box : boxes ) {
            if ( box.willAllowMoleculeDrop( molecule ) ) {
                return box;
            }
        }
        return null;
    }

    public void addCollectionBox( CollectionBox box ) {
        boxes.add( box );

        // listen to when our collection boxes change, so that we can identify when all of our collection boxes are filled
        box.quantity.addObserver( new SimpleObserver() {
            public void update() {
                boolean allFull = true;
                for ( CollectionBox collectionBox : boxes ) {
                    if ( !collectionBox.isFull() ) {
                        allFull = false;
                    }
                }
                allCollectionBoxesFilled.set( !boxes.isEmpty() && allFull );
            }
        } );
    }

    public List<Kit> getKits() {
        return kits;
    }

    public List<CollectionBox> getCollectionBoxes() {
        return boxes;
    }

    public PBounds getAvailableKitBounds() {
        return layoutBounds.getAvailableKitBounds();
    }

    public PBounds getAvailablePlayAreaBounds() {
        return layoutBounds.getAvailablePlayAreaBounds();
    }

    public Kit getCurrentKit() {
        return currentKit.get();
    }

    public Property<Kit> getCurrentKitProperty() {
        return currentKit;
    }

    public int getCurrentKitIndex() {
        int index = kits.indexOf( currentKit.get() );
        if ( index < 0 ) {
            throw new RuntimeException( "Could not find current kit index" );
        }
        return index;
    }

    public boolean hasNextKit() {
        return getCurrentKitIndex() + 1 < kits.size();
    }

    public boolean hasPreviousKit() {
        return getCurrentKitIndex() - 1 >= 0;
    }

    public void goToNextKit() {
        if ( hasNextKit() ) {
            currentKit.set( kits.get( getCurrentKitIndex() + 1 ) );
        }
    }

    public void goToPreviousKit() {
        if ( hasPreviousKit() ) {
            currentKit.set( kits.get( getCurrentKitIndex() - 1 ) );
        }
    }
}
