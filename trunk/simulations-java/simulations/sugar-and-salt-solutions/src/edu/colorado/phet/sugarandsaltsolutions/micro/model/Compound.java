// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
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

    //Put the vectors at the same random angle so all compounds don't come out at the same angle
    private double angle;

    public Compound( ImmutableVector2D position, double angle ) {
        super( position );
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }

    //Set the position of the compound, and update the location of all constituents
    @Override public void setPosition( ImmutableVector2D location ) {
        super.setPosition( location );
        updateConstituentLocations();
    }

    //Update all constituents with their correct absolute location based on the crystal location and their relative location within the crystal
    protected void updateConstituentLocations() {
        for ( Constituent constituent : constituents ) {
            updateConstituentLocation( constituent );
        }
    }

    //Update the constituent with its correct absolute location based on the crystal location and its relative location within the crystal, and the crystal's angle
    private void updateConstituentLocation( Constituent constituent ) {
        constituent.particle.setPosition( getPosition().plus( constituent.relativePosition.getRotatedInstance( angle ) ) );
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

    public boolean isUnderwaterTimeRecorded() {
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
    public void removeConstituent( Constituent<T> constituent ) {
        constituents.remove( constituent );
    }

    public void addConstituent( Constituent<T> constituent ) {
        constituents.add( constituent );
        updateConstituentLocation( constituent );
    }

    //Get all the spherical particles within this compound and its children recursively, so they can be displayed with PNodes
    public Iterable<SphericalParticle> getAllSphericalParticles() {
        ArrayList<SphericalParticle> sphericalParticles = new ArrayList<SphericalParticle>();
        for ( Constituent<T> constituent : constituents ) {
            if ( constituent.particle instanceof SphericalParticle ) {
                sphericalParticles.add( (SphericalParticle) constituent.particle );
            }
            else if ( constituent.particle instanceof Compound<?> ) {
                Compound<?> compound = (Compound<?>) constituent.particle;
                Iterable<SphericalParticle> subParticles = compound.getAllSphericalParticles();
                for ( SphericalParticle subParticle : subParticles ) {
                    sphericalParticles.add( subParticle );
                }
            }
        }
        return sphericalParticles;
    }

    //TODO: should constituents use ItemList?
    public int count( Class<?> type ) {
        int count = 0;
        for ( Constituent<T> constituent : constituents ) {
            if ( constituent.particle.getClass().equals( type ) ) {
                count++;
            }
        }
        return count;
    }

    //Determine whether the compound contains the specified particle, to ignore intra-molecular forces in WaterModel
    public boolean containsParticle( T particle ) {
        for ( Constituent<T> constituent : constituents ) {
            if ( constituent.particle == particle ) {
                return true;
            }
        }
        return false;
    }

    //Sets the position and angle of the compound, and updates the location of all constituents
    public void setPositionAndAngle( ImmutableVector2D modelPosition, float angle ) {
        super.setPosition( modelPosition );
        this.angle = angle;
        updateConstituentLocations();
    }
}