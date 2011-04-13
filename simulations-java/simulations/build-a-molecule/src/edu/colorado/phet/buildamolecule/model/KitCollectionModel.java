package edu.colorado.phet.buildamolecule.model;

import java.util.List;

/**
 * Represents a main running model for the 1st two tabs. Contains a collection of kits and boxes. Kits are responsible
 * for their buckets and atoms.
 */
public class KitCollectionModel {
    private List<Kit> kits;
    private List<CollectionBox> boxes;

    public KitCollectionModel( List<Kit> kits, List<CollectionBox> boxes ) {
        this.kits = kits;
        this.boxes = boxes;
    }

    public List<Kit> getKits() {
        return kits;
    }

    public List<CollectionBox> getBoxes() {
        return boxes;
    }
}
