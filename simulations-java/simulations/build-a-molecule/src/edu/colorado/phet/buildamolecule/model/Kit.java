package edu.colorado.phet.buildamolecule.model;

import java.awt.geom.Point2D;
import java.util.List;

import edu.colorado.phet.buildamolecule.model.buckets.AtomModel;
import edu.colorado.phet.buildamolecule.model.buckets.Bucket;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Contains multiple buckets of different types of atoms
 */
public class Kit {
    private final List<Bucket> buckets;

    public static final double KIT_Y = -250;
    public static final double KIT_X_CENTER = -100;
    public static final double BUCKET_PADDING = 50;

    public Kit( List<Bucket> buckets ) {
        this.buckets = buckets;

        double usedWidth = 0;

        // lays out all of the buckets from the left to right
        for ( int i = 0; i < buckets.size(); i++ ) {
            Bucket bucket = buckets.get( i );
            if ( i != 0 ) {
                usedWidth += BUCKET_PADDING;
            }
            bucket.setPosition( new ImmutableVector2D( usedWidth, KIT_Y ) );
            usedWidth += bucket.getWidth();
        }

        // centers the buckets horizontally within the kit
        for ( Bucket bucket : buckets ) {
            bucket.setPosition( new ImmutableVector2D( bucket.getPosition().getX() - usedWidth / 2 + KIT_X_CENTER + bucket.getWidth() / 2, KIT_Y ) );
            for ( AtomModel atomModel : bucket.getAtoms() ) {
                atomModel.setPosition( atomModel.getPosition().getAddedInstance( bucket.getPosition() ) );
            }
        }
    }

    public List<Bucket> getBuckets() {
        return buckets;
    }
}
