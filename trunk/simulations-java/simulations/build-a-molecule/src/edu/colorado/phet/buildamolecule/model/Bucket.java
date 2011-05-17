// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.List;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.module.AbstractBuildAMoleculeModule;
import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.SphereBucket;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A bucket for atoms
 */
public class Bucket extends SphereBucket<AtomModel> {
    private final Element atomType;

    public Bucket( IClock clock, Function0<Atom> atomFactory, int quantity ) {
        // automatically compute the desired width with a height of 200;
        this( new PDimension( AbstractBuildAMoleculeModule.calculateIdealBucketWidth( atomFactory.apply().getRadius(), quantity ), 200 ), clock, atomFactory, quantity );
    }

    public Bucket( Dimension2D size, IClock clock, Function0<Atom> atomFactory, int quantity ) {
        this( size, 1, 0, clock, atomFactory, quantity );
    }

    /**
     * Constructor.  The dimensions used are just numbers, i.e. they are not
     * meant to be any specific size (such as meters).  This enabled
     * reusability in any 2D model.
     */
    public Bucket( Dimension2D size, double usableWidthProportion, double yOffset, IClock clock, Function0<Atom> atomFactory, int quantity ) {
        // TODO: improve the getAtomName call
        // TODO: improve passing of the element. possibly don't pass the actual factory?
        super( new Point2D.Double(), size, atomFactory.apply().getColor(), BuildAMoleculeStrings.getAtomName( atomFactory.apply().getElement() ), atomFactory.apply().getRadius() );
        this.atomType = atomFactory.apply().getElement();

        for ( int i = 0; i < quantity; i++ ) {
            super.addParticleFirstOpen( new AtomModel( atomFactory.apply(), clock ), true );
        }
    }

    @Override public void setPosition( Point2D point ) {
        // when we move the bucket, we must also move our contained atoms
        ImmutableVector2D delta = new ImmutableVector2D( point ).minus( new ImmutableVector2D( getPosition() ) );
        for ( AtomModel atom : getAtoms() ) {
            atom.setPositionAndDestination( atom.getPosition().getAddedInstance( delta ) );
        }
        super.setPosition( point );
    }

    public List<AtomModel> getAtoms() {
        return getParticleList();
    }

    /**
     * Instantly place the atom in the correct position, whether or not it is in the bucket
     *
     * @param atom The atom
     */
    public void placeAtom( final AtomModel atom ) {
        if ( containsParticle( atom ) ) {
            removeParticle( atom );
        }
        super.addParticleFirstOpen( atom, !false );
    }

    public double getWidth() {
        return getContainerShape().getBounds().getWidth();
    }

    public Element getElement() {
        return atomType;
    }

}