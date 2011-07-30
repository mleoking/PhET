// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.Option;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel.FREE_PARTICLE_SPEED;

/**
 * This class handles incremental crystallization of particles when the concentration surpasses the saturation point.
 * I originally tried just specifying the "T extends particle" generic type and using Crystal
 *
 * @author Sam Reid
 */
public abstract class IncrementalGrowth<T extends Particle, U extends Crystal<T>> {

    //Keep track of the last time a crystal was formed so that they can be created gradually instead of all at once
    private double lastNewCrystalFormationTime;
    protected final MicroModel model;
    private final ItemList<U> crystals;

    public IncrementalGrowth( MicroModel model, ItemList<U> crystals ) {
        this.model = model;
        this.crystals = crystals;
    }

    //Check to see whether it is time to create or add to existing crystals, if the solution is over saturated
    public void allowCrystalGrowth( double dt, ObservableProperty<Boolean> unsaturated ) {
        double timeSinceLast = model.getTime() - lastNewCrystalFormationTime;

        //Make sure at least 1 second has passed, then convert to crystals
        if ( !unsaturated.get() && timeSinceLast > 1 && crystals.size() == 0 ) {

            //Create a crystal if there weren't any
            System.out.println( "No crystals, starting a new one" );
            formNewCrystal();
            lastNewCrystalFormationTime = model.getTime();
        }

        //If the solution is saturated, try adding on to an existing crystal
        else if ( !unsaturated.get() ) {

            //Find existing crystal(s)
            Crystal<T> crystal = crystals.get( 0 );

            //Enumerate all particles and distances from crystal sites, but only look for sites that are underwater, otherwise particles would try to fly out of the solution (and get stuck at the boundary)
            ArrayList<CrystallizationMatch<T>> matches = getAllCrystallizationMatches( crystal );
            if ( matches.size() > 0 ) {

                //Find a matching particle nearby one of the sites and join it together
                CrystallizationMatch<T> match = matches.get( 0 );

                //If close enough, join the lattice
                if ( match.distance <= FREE_PARTICLE_SPEED * dt ) {

                    //Remove the particle from the list of free particles
                    model.freeParticles.remove( match.particle );

                    //Add it as a constituent of the crystal
                    crystal.addConstituent( new Constituent<T>( match.particle, match.site.relativePosition ) );
                }

                //Otherwise, move closest particle toward the lattice
                else {
                    match.particle.velocity.set( match.site.absolutePosition.minus( match.particle.getPosition() ).getInstanceOfMagnitude( FREE_PARTICLE_SPEED ) );
                }
            }

            //No matches, so start a new crystal
            else {
                System.out.println( "No matches, starting a new crystal" );
                formNewCrystal();
            }
        }
    }

    //Look for all open site on its lattice and sort by distance to available participants
    public ArrayList<CrystallizationMatch<T>> getAllCrystallizationMatches( Crystal<T> crystal ) {
        ArrayList<CrystallizationMatch<T>> matches = new ArrayList<CrystallizationMatch<T>>();
        for ( Particle freeParticle : model.freeParticles ) {
            for ( OpenSite<T> openSite : crystal.getOpenSites() ) {
                if ( model.solution.shape.get().contains( openSite.shape.getBounds2D() ) && openSite.matches( freeParticle ) ) {
                    matches.add( new CrystallizationMatch<T>( (T) freeParticle, openSite ) );
                }
            }
        }

        //Find the best site
        Collections.sort( matches, new Comparator<CrystallizationMatch>() {
            public int compare( CrystallizationMatch o1, CrystallizationMatch o2 ) {
                return Double.compare( o1.distance, o2.distance );
            }
        } );
        return matches;
    }

    //Convert a particle to a crystal, or add to existing crystals to decrease the concentration below the saturation point
    private void formNewCrystal() {
        Option<?> selected = selectSeed();
        if ( selected.isSome() ) {
            //If there is no crystal, create one with one particle
            convertToCrystal( (T) selected.get() );
        }
    }

    protected abstract Option<?> selectSeed();

    //Convert the specified particle to a crystal
    private void convertToCrystal( T particle ) {
        U crystal = toCrystal( particle );
        crystal.addConstituent( new Constituent<T>( particle, ImmutableVector2D.ZERO ) );

        model.freeParticles.remove( particle );
        crystals.add( crystal );
    }

    protected abstract U toCrystal( T particle );
}