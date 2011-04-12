package edu.colorado.phet.buildamolecule.model;

import java.util.List;

/**
 * Contains multiple buckets of different types of atoms
 */
public class Kit {
    private final List<Bucket> buckets;

    public Kit( List<Bucket> buckets ) {
        this.buckets = buckets;
    }

    public List<Bucket> getBuckets() {
        return buckets;
    }
}
