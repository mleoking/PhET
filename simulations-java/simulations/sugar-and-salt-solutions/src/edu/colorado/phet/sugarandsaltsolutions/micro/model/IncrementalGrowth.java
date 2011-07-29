// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride.SodiumChlorideCrystal;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * This class handles incremental crystallization of particles when the concentration surpasses the saturation point.
 *
 * @author Sam Reid
 */
public class IncrementalGrowth {

    //Keep track of the last time a crystal was formed so that they can be created gradually instead of all at once
    private double lastNaClCrystallizationTime;
    private MicroModel model;

    public IncrementalGrowth( MicroModel model ) {
        this.model = model;
    }

    //Check to see whether it is time to create or add to existing crystals, if the solution is over saturated
    public void formNaClCrystals( double dt, ObservableProperty<Boolean> sodiumChlorideUnsaturated ) {
        double timeSinceLast = model.getTime() - lastNaClCrystallizationTime;

        //Make sure at least 1 second has passed, then convert to crystals
        if ( !sodiumChlorideUnsaturated.get() && timeSinceLast > 1 && model.sodiumChlorideCrystals.size() == 0 ) {

            //Create a crystal if there weren't any
            System.out.println( "No crystals, starting a new one" );
            startSodiumChlorideCrystal();
            lastNaClCrystallizationTime = model.getTime();
        }

        //If the solution is saturated, try adding on to an existing crystal
        else if ( !sodiumChlorideUnsaturated.get() ) {

            //Find existing crystal(s)
            SodiumChlorideCrystal crystal = model.sodiumChlorideCrystals.get( 0 );

            //Enumerate all particles and distances from crystal sites, but only look for sites that are underwater, otherwise particles would try to fly out of the solution (and get stuck at the boundary)
            ArrayList<CrystallizationMatch> matches = getAllCrystallizationMatches( crystal );
            if ( matches.size() > 0 ) {

                //Find a matching particle nearby one of the sites and join it together
                CrystallizationMatch match = matches.get( 0 );

                //If close enough, join the lattice
                if ( match.distance <= model.FREE_PARTICLE_SPEED * dt ) {

                    //Remove the particle from the list of free particles
                    model.freeParticles.remove( match.particle );

                    //Add it as a constituent of the crystal
                    crystal.addConstituent( match.openSite.toConstituent() );
                }

                //Otherwise, move closest particle toward the lattice
                else {
                    match.particle.velocity.set( match.openSite.absolutePosition.minus( match.particle.getPosition() ).getInstanceOfMagnitude( MicroModel.FREE_PARTICLE_SPEED ) );
                }
            }

            //No matches, so start a new crystal
            else {
                System.out.println( "No matches, starting a new crystal" );
                startSodiumChlorideCrystal();
            }
        }
    }

    //Look for all open site on its lattice and sort by distance to available participants
    public ArrayList<CrystallizationMatch> getAllCrystallizationMatches( SodiumChlorideCrystal crystal ) {
        ArrayList<CrystallizationMatch> matches = new ArrayList<CrystallizationMatch>();
        for ( Particle freeParticle : model.freeParticles ) {
            for ( OpenSite<SphericalParticle> openSite : crystal.getOpenSites() ) {
                if ( model.solution.shape.get().contains( openSite.shape.getBounds2D() ) && openSite.matches( freeParticle ) ) {
                    matches.add( new CrystallizationMatch( freeParticle, openSite ) );
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
    private void startSodiumChlorideCrystal() {
        Option<Particle> selected = model.freeParticles.selectRandom( Sodium.class, Chloride.class );
        if ( selected.isSome() ) {
            //If there is no crystal, create one with one particle
            convertToCrystal( selected.get() );
        }
    }

    //Convert the specified particle to a crystal
    private void convertToCrystal( Particle particle ) {
        SodiumChlorideCrystal crystal = new SodiumChlorideCrystal( particle.getPosition(), randomAngle() );
        crystal.addConstituent( new Constituent<SphericalParticle>( (SphericalParticle) particle, ImmutableVector2D.ZERO ) );

        model.freeParticles.remove( particle );
        model.sodiumChlorideCrystals.add( crystal );
    }
}