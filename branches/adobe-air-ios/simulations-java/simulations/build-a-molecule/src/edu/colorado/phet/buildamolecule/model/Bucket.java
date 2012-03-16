//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.List;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.SphereBucket;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A bucket for atoms
 */
public class Bucket extends SphereBucket<Atom2D> {
    private final Element element;

    public Bucket( IClock clock, Element element, int quantity ) {
        // automatically compute the desired width with a height of 200;
        this( new PDimension( calculateIdealBucketWidth( element.getRadius(), quantity ), 200 ), clock, element, quantity );
    }

    /**
     * Constructor.  The dimensions used are just numbers, i.e. they are not
     * meant to be any specific size (such as meters).  This enabled
     * reusability in any 2D model.
     *
     * @param size     Physical size of the bucket (model space)
     * @param element  The element of the atoms in the bucket
     * @param quantity The number of atoms starting in the bucket
     */
    public Bucket( Dimension2D size, IClock clock, Element element, int quantity ) {
        super( new Point2D.Double(), size, element.getColor(), BuildAMoleculeStrings.getAtomName( element ), element.getRadius() );
        this.element = element;

        for ( int i = 0; i < quantity; i++ ) {
            super.addParticleFirstOpen( new Atom2D( element, clock ), true );
        }
    }

    /**
     * Make sure we can fit all of our atoms in just two rows
     *
     * @param radius   Atomic radius (picometers)
     * @param quantity Quantity of atoms in bucket
     * @return Width of bucket
     */
    public static int calculateIdealBucketWidth( double radius, int quantity ) {
        // calculate atoms to go on the bottom row
        int numOnBottomRow = ( quantity <= 2 ) ? quantity : ( quantity / 2 + 1 );

        // figure out our width, accounting for radius-padding on each side
        double width = 2 * radius * ( numOnBottomRow + 1 );

        // add a bit, and make sure we don't go under 350
        return (int) Math.max( 350, width + 1 );
    }

    @Override public void setPosition( Point2D point ) {
        // when we move the bucket, we must also move our contained atoms
        ImmutableVector2D delta = new ImmutableVector2D( point ).minus( new ImmutableVector2D( getPosition() ) );
        for ( Atom2D atom : getAtoms() ) {
            atom.setPositionAndDestination( atom.getPosition().getAddedInstance( delta ) );
        }
        super.setPosition( point );
    }

    public List<Atom2D> getAtoms() {
        return getParticleList();
    }

    /**
     * Instantly place the atom in the correct position, whether or not it is in the bucket
     *
     * @param atom The atom
     */
    public void placeAtom( final Atom2D atom ) {
        if ( containsParticle( atom ) ) {
            removeParticle( atom );
        }
        super.addParticleFirstOpen( atom, true );
    }

    public double getWidth() {
        return getContainerShape().getBounds().getWidth();
    }

    public Element getElement() {
        return element;
    }

}