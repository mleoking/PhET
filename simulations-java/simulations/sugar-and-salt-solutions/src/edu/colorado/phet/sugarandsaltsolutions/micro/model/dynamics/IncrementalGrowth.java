// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.OpenSite;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.UpdateStrategy.FREE_PARTICLE_SPEED;

/**
 * This class handles incremental crystallization of particles when the concentration surpasses the saturation point.
 * I originally tried just specifying the "T extends particle" generic type and using Crystal
 *
 * @author Sam Reid
 */
public abstract class IncrementalGrowth<T extends Particle, U extends Crystal<T>> {

    //Keep track of the last time a crystal was formed so that they can be created gradually instead of all at once
    private double lastNewCrystalFormationTime;

    //The main model for timing and adding/removing particles and crystals
    protected final MicroModel model;

    //The list of all crystals of the appropriate type
    private final ItemList<U> crystals;

    //Randomness for crystal formation times and which crystals to bond to
    private final Random random = new Random();

    public IncrementalGrowth( MicroModel model, ItemList<U> crystals ) {
        this.model = model;
        this.crystals = crystals;
    }

    //Check to see whether it is time to create or add to existing crystals, if the solution is over saturated
    public void allowCrystalGrowth( double dt, ObservableProperty<Boolean> saturated ) {
        double timeSinceLast = model.getTime() - lastNewCrystalFormationTime;

        //Make sure at least 1 second has passed, then convert to crystals
        if ( saturated.get() && timeSinceLast > 1 && crystals.size() == 0 ) {

            //Create a crystal if there weren't any
            System.out.println( "No crystals, starting a new one" );
            formNewCrystal();
            lastNewCrystalFormationTime = model.getTime();
        }

        //If the solution is saturated, try adding on to an existing crystal
        else if ( saturated.get() ) {

            //Randomly choose an existing crystal to possibly bond to
            Crystal<T> crystal = crystals.get( random.nextInt( crystals.size() ) );

            //Enumerate all particles and distances from crystal sites, but only look for sites that are underwater, otherwise particles would try to fly out of the solution (and get stuck at the boundary)
            ArrayList<CrystallizationMatch<T>> matches = getAllCrystallizationMatchesSorted( crystal );
            if ( matches.size() > 0 ) {

                //Find a matching particle nearby one of the sites and join it together
                CrystallizationMatch<T> match = matches.get( 0 );

                //With 1% chance, form a new crystal anyways (if there aren't too many crystals)
                if ( random.nextDouble() > 0.99 && crystals.size() <= 2 ) {
                    System.out.println( "Random choice to form new crystal instead of joining another" );
                    formNewCrystal();
                }

                //If close enough, join the lattice
                else if ( match.distance <= FREE_PARTICLE_SPEED * dt ) {

                    //Remove the particle from the list of free particles
                    model.freeParticles.remove( match.particle );

                    //Add it as a constituent of the crystal
                    crystal.addConstituent( new Constituent<T>( match.particle, match.site.relativePosition ) );
                }

                //Otherwise, move closest particle toward the lattice
                else if ( match.distance <= model.beaker.getWidth() / 8 ) {
                    match.particle.velocity.set( match.site.absolutePosition.minus( match.particle.getPosition() ).getInstanceOfMagnitude( FREE_PARTICLE_SPEED ) );
                }

                else {
                    System.out.println( "Best match was too far away (" + match.distance / model.beaker.getWidth() + " beaker widths, so starting a new crystal with a random particle" );
                    formNewCrystal();
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
    public ArrayList<CrystallizationMatch<T>> getAllCrystallizationMatchesSorted( Crystal<T> crystal ) {
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

    //Choose a single element to begin a new crystal
    protected abstract Option<T> selectSeed();

    //Convert the specified particle to a crystal and add it to the model
    private void convertToCrystal( T particle ) {
        U crystal = newCrystal( particle.getPosition() );
        crystal.addConstituent( new Constituent<T>( particle, ImmutableVector2D.ZERO ) );

        model.freeParticles.remove( particle );
        crystals.add( crystal );
    }

    //Create the right subtype of crystal at the specified location.  It will be populated by the convertToCrystal method
    protected abstract U newCrystal( ImmutableVector2D position );
}