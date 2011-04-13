package edu.colorado.phet.buildamolecule.model;

import java.util.LinkedList;
import java.util.List;

import edu.umd.cs.piccolo.util.PBounds;

/**
 * Represents a main running model for the 1st two tabs. Contains a collection of kits and boxes. Kits are responsible
 * for their buckets and atoms.
 */
public class KitCollectionModel {
    private List<Kit> kits = new LinkedList<Kit>();
    private List<CollectionBox> boxes = new LinkedList<CollectionBox>();

    private PBounds availableKitBounds = new PBounds( -800, -500, 1100, 200 );

    public KitCollectionModel() {
    }

    public void addKit( Kit kit ) {
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
        return availableKitBounds;
    }
}
