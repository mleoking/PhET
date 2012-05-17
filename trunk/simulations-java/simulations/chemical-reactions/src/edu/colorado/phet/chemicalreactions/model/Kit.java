// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.colorado.phet.common.phetcommon.model.property.Property;

public class Kit {
    private final List<MoleculeBucket> buckets;

    private final List<MoleculeBucket> reactantBuckets;
    private final List<MoleculeBucket> productBuckets;

    private final List<Atom> atoms = new LinkedList<Atom>();
    private final Set<Molecule> molecules = new HashSet<Molecule>();
    public final Property<Boolean> visible = new Property<Boolean>( false );
    public final Property<Boolean> hasMoleculesInBoxes = new Property<Boolean>( false );

    private final LayoutBounds layoutBounds;

    private final List<MoleculeListener> moleculeListeners = new LinkedList<MoleculeListener>();

    public Kit( final LayoutBounds layoutBounds, final List<MoleculeBucket> reactantBuckets, final List<MoleculeBucket> productBuckets ) {
        this.reactantBuckets = reactantBuckets;
        this.productBuckets = productBuckets;

        this.buckets = new ArrayList<MoleculeBucket>() {{
            addAll( reactantBuckets );
            addAll( productBuckets );
        }};
        this.layoutBounds = layoutBounds;

        resetKit();

        // lays out the buckets correctly, and takes care of changing atom positions
        layout();
    }

    public void layout() {
        double kitY = getLayoutBounds().getAvailableKitModelBounds().getCenterY() - 20;
        double kitXCenter = getLayoutBounds().getAvailableKitModelBounds().getCenterX();

        double usedWidth = 0;

        double BUCKET_PADDING_PLUS = 300;
        double BUCKET_PADDING_ARROW = 400;

        // lays out all of the buckets from the left to right
        for ( int i = 0; i < this.buckets.size(); i++ ) {
            MoleculeBucket bucket = this.buckets.get( i );
            if ( i != 0 ) {
                if ( bucket == getProductBuckets().get( 0 ) ) {
                    // padding between reactants and products
                    usedWidth += BUCKET_PADDING_ARROW;
                }
                else {
                    // padding between two reactants or two products (for plus)
                    usedWidth += BUCKET_PADDING_PLUS;
                }
            }
            bucket.setPosition( new Point2D.Double( usedWidth, kitY ) );
            usedWidth += bucket.getSize().getWidth();
        }

        // centers the buckets horizontally within the kit
        for ( MoleculeBucket bucket : buckets ) {

            // also note: this moves the atoms also!
            bucket.setPosition( new Point2D.Double( bucket.getPosition().getX() - usedWidth / 2 + kitXCenter + bucket.getSize().getWidth() / 2, kitY ) );
        }
    }

    public void resetKit() {
        // TODO fill in here
    }

    public void show() {
        visible.set( true );
    }

    public void hide() {
        visible.set( false );
    }

    public boolean isContainedInBucket( Molecule molecule ) {
        for ( MoleculeBucket bucket : buckets ) {
            if ( bucket.getMolecules().contains( molecule ) ) {
                return true;
            }
        }
        return false;
    }

    public List<MoleculeBucket> getBuckets() {
        return buckets;
    }

    public Set<Molecule> getMolecules() {
        return molecules;
    }

    public MoleculeBucket getBucketForShape( MoleculeShape shape ) {
        for ( MoleculeBucket bucket : buckets ) {
            if ( bucket.getShape() == shape ) {
                return bucket;
            }
        }
        throw new RuntimeException( "Bucket not found for shape: " + shape );
    }

    public boolean hasMoleculesOutsideOfBuckets() {
        return !molecules.isEmpty() || hasMoleculesInBoxes.get();
    }

    public void addMoleculeListener( MoleculeListener listener ) {
        moleculeListeners.add( listener );
    }

    public void removeMoleculeListener( MoleculeListener listener ) {
        moleculeListeners.remove( listener );
    }

    public LayoutBounds getLayoutBounds() {
        return layoutBounds;
    }

    public List<MoleculeBucket> getProductBuckets() {
        return productBuckets;
    }

    public List<MoleculeBucket> getReactantBuckets() {
        return reactantBuckets;
    }

    public Molecule getMolecule( Atom atom ) {
        for ( Molecule molecule : molecules ) {
            if ( molecule.getAtoms().contains( atom ) ) {
                return molecule;
            }
        }
        //return null;
        throw new RuntimeException( "molecule not found for atom: " + atom );
    }

    public static interface MoleculeListener {
        public void addedMolecule( Molecule molecule );

        public void removedMolecule( Molecule molecule );
    }

    public static class MoleculeAdapter implements MoleculeListener {
        public void addedMolecule( Molecule molecule ) {
        }

        public void removedMolecule( Molecule molecule ) {
        }
    }
}
