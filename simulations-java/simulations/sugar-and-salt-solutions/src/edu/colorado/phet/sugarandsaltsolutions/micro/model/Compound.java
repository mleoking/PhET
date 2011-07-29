// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;

/**
 * A compound represents 0 or more (usually 1 or more) constituents which can be put into solution.  It may be constructed from a lattice.
 * The type is generic since some compounds such as NaCl are made of SphericalParticles while others such as Sucrose are made from molecules with their own substructure
 * Adding the type parameter at this level makes it so we don't have as many casts when acquiring components during dissolve or iteration processes.
 *
 * @author Sam Reid
 */
public class Compound<T extends Particle> extends Particle implements Iterable<Constituent<T>> {

    //Members in the compound
    protected final ArrayList<Constituent<T>> constituents = new ArrayList<Constituent<T>>();

    //The time the lattice entered the water, if any
    private Option<Double> underwaterTime = new None<Double>();

    //Put the vectors at the same random angle so all crystals don't come out at right angles
    protected final double angle;

    public Compound( ImmutableVector2D position, double angle ) {
        super( position );
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }

    //Set the position of the compound, and update the location of all constituents
    //TODO: there is also a public api for setting the position of the compound through Property<ImmutableVector2D>, but which does not update the constituent locations
    //Maybe this method should auto-call when that property changes, or the property shouldn't be public
    @Override protected void setPosition( ImmutableVector2D location ) {
        super.setPosition( location );
        updateConstituentLocations();
    }

    private void updateConstituentLocations() {
        for ( Constituent constituent : constituents ) {
            constituent.particle.setPosition( position.get().plus( constituent.location ) );
        }
    }

    //The shape of a lattice is the combined area of its constituents, using bounding rectangles to improve performance
    @Override public Shape getShape() {
        final Rectangle2D bounds2D = constituents.get( 0 ).particle.getShape().getBounds2D();
        Rectangle2D rect = new Rectangle2D.Double( bounds2D.getX(), bounds2D.getY(), bounds2D.getWidth(), bounds2D.getHeight() );
        for ( Constituent constituent : constituents ) {
            rect = rect.createUnion( constituent.particle.getShape().getBounds2D() );
        }
        return rect;
    }

    public Iterator<Constituent<T>> iterator() {
        return constituents.iterator();
    }

    public boolean isUnderwater() {
        return underwaterTime.isSome();
    }

    public void setUnderwater( double time ) {
        this.underwaterTime = new Some<Double>( time );
    }

    public double getUnderWaterTime() {
        return underwaterTime.get();
    }

    //Returns the number of constituents in the compound
    public int numberConstituents() {
        return constituents.size();
    }

    //Gets the constituent at the specified index
    public Constituent getConstituent( int i ) {
        return constituents.get( i );
    }

    //Removes the specified constituent from the compound
    public void removeConstituent( Constituent constituent ) {
        constituents.remove( constituent );
    }

    //From the compound's constituents, choose one near the edge that would be good to release as part of a dissolving process
    //Note that since the lattice can take the shape of an arc, this can still leave orphaned particles floating in the air.  This should probably be resolved
    //TODO: A better way to to this would be to check that the dropped component doesn't create two separated components from the lattice graph
    public Constituent getConstituentToDissolve() {
        ArrayList<Constituent> c = new ArrayList<Constituent>( constituents );
        Collections.sort( c, new Comparator<Constituent>() {
            public int compare( Constituent o1, Constituent o2 ) {
                return Double.compare( o1.particle.position.get().getY(), o2.particle.position.get().getY() );
            }
        } );
        return c.get( c.size() - 1 );
    }

    //Get all the spherical particles within this compound and its children recursively, so they can be displayed with PNodes
    public Iterable<SphericalParticle> getAllSphericalParticles() {
        ArrayList<SphericalParticle> sphericalParticles = new ArrayList<SphericalParticle>();
        for ( Constituent<T> constituent : constituents ) {
            if ( constituent.particle instanceof SphericalParticle ) {
                sphericalParticles.add( (SphericalParticle) constituent.particle );
            }
            else if ( constituent.particle instanceof Compound ) {
                Compound<? extends Particle> compound = (Compound<? extends Particle>) constituent.particle;
                Iterable<SphericalParticle> subParticles = compound.getAllSphericalParticles();
                for ( SphericalParticle subParticle : subParticles ) {
                    sphericalParticles.add( subParticle );
                }
            }
        }
        return sphericalParticles;
    }
}