// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction2;
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

    public KitCollectionModel( LayoutBounds layoutBounds ) {
        this.layoutBounds = layoutBounds;
    }

    public void addKit( Kit kit ) {
        if ( currentKit == null ) {
            currentKit = new Property<Kit>( kit );
            kit.show();

            // handle kit visibility when this changes
            currentKit.addObserver( new VoidFunction2<Kit, Kit>() {
                public void apply( Kit newKit, Kit oldKit ) {
                    newKit.show();
                    oldKit.hide();
                }
            } );
        }
        else {
            kit.hide();
        }
        kits.add( kit );
    }

    public void addCollectionBox( CollectionBox box ) {
        boxes.add( box );
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

    public Kit getCurrentKit() {
        return currentKit.getValue();
    }

    public Property<Kit> getCurrentKitProperty() {
        return currentKit;
    }

    public int getCurrentKitIndex() {
        for ( int i = 0; i < kits.size(); i++ ) {
            if ( kits.get( i ) == currentKit.getValue() ) {
                return i;
            }
        }
        throw new RuntimeException( "Could not find current kit index" );
    }

    public boolean hasNextKit() {
        return getCurrentKitIndex() + 1 < kits.size();
    }

    public boolean hasPreviousKit() {
        return getCurrentKitIndex() - 1 >= 0;
    }

    public void nextKit() {
        if ( hasNextKit() ) {
            currentKit.setValue( kits.get( getCurrentKitIndex() + 1 ) );
        }
    }

    public void previousKit() {
        if ( hasPreviousKit() ) {
            currentKit.setValue( kits.get( getCurrentKitIndex() - 1 ) );
        }
    }
}
