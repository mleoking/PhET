// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride.SodiumChlorideCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride.SodiumChlorideLattice;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * This class handles incremental crystallization of particles when the concentration surpasses the saturation point.
 *
 * @author Sam Reid
 */
public class CrystalFormation {

    //Keep track of the last time a crystal was formed so that they can be created gradually instead of all at once
    private double lastNaClCrystallizationTime;
    private MicroModel model;

    public CrystalFormation( MicroModel model ) {
        this.model = model;
    }

    //Check to see whether it is time to create or add to existing crystals, if the solution is over saturated
    public void formNaClCrystals( double dt, ObservableProperty<Boolean> sodiumChlorideUnsaturated ) {
        double timeSinceLast = model.getTime() - lastNaClCrystallizationTime;

        //Make sure at least 1 second has passed, then convert to crystals
        if ( !sodiumChlorideUnsaturated.get() && timeSinceLast > 1 && model.sodiumChlorideCrystals.size() == 0 ) {

            //Create a crystal if there weren't any
            startSodiumChlorideCrystal();
            lastNaClCrystallizationTime = model.getTime();
        }

        //try adding on to an existing crystal
        else if ( !sodiumChlorideUnsaturated.get() ) {

            //Find existing crystal(s)
            SodiumChlorideCrystal crystal = model.sodiumChlorideCrystals.get( 0 );

            //Look for an open site on its lattice
            ArrayList<CrystalSite> openSites = crystal.getOpenSites();

            class Match {
                public final Particle particle;
                public final CrystalSite crystalSite;
                public final double distance;

                Match( Particle particle, CrystalSite crystalSite ) {
                    this.particle = particle;
                    this.crystalSite = crystalSite;
                    this.distance = particle.position.get().minus( crystalSite.position ).getMagnitude();
                }

                @Override public String toString() {
                    return "Match{" +
                           "particle=" + particle +
                           ", crystalSite=" + crystalSite +
                           ", distance=" + distance +
                           '}';
                }
            }

            //Enumerate all particles and distances from crystal sites, but only look for sites that are underwater, otherwise particles would try to fly out of the solution (and get stuck at the boundary)
            ArrayList<Match> matches = new ArrayList<Match>();
            for ( Particle freeParticle : model.freeParticles ) {
                for ( CrystalSite openSite : openSites ) {
                    if ( solutionContains( openSite ) && openSite.matches( freeParticle ) ) {
                        matches.add( new Match( freeParticle, openSite ) );
                    }
                }
            }

            if ( matches.size() > 0 ) {

                //Find a matching particle nearby one of the sites and join it together
                Collections.sort( matches, new Comparator<Match>() {
                    public int compare( Match o1, Match o2 ) {
                        return Double.compare( o1.distance, o2.distance );
                    }
                } );
                Match match = matches.get( 0 );

                //If close enough, join the lattice
                if ( match.distance <= model.FREE_PARTICLE_SPEED * dt ) {

                    //Remove the particle
                    model.freeParticles.remove( match.particle );
                    model.sphericalParticles.remove( (SphericalParticle) match.particle );

                    //Remove the old crystal
                    for ( Constituent<SphericalParticle> constituent : crystal ) {
                        model.sphericalParticles.remove( constituent.particle );
                    }
                    model.sodiumChlorideCrystals.remove( crystal );

                    //Create a new crystal that combines the pre-existing crystal and the new particle
                    model.addSaltCrystal( new SodiumChlorideCrystal( crystal.position.get(), (SodiumChlorideLattice) crystal.growCrystal( match.crystalSite ), crystal.angle ) );
                }

                //Otherwise, move closest particle toward the lattice
                else {
                    match.particle.velocity.set( match.crystalSite.position.minus( match.particle.position.get() ).getInstanceOfMagnitude( model.FREE_PARTICLE_SPEED ) );
                }
            }

            //No matches, so start a new crystal
            else {
                startSodiumChlorideCrystal();
            }
        }
    }

    //Determine whether the solution shape contains the specified point
    private boolean solutionContains( CrystalSite site ) {
        return model.solution.shape.get().getBounds2D().contains( site.getTargetBounds() );
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
        SodiumChlorideCrystal crystal = new SodiumChlorideCrystal( particle.position.get(), new SodiumChlorideLattice( (SphericalParticle) particle ), randomAngle() );

        model.freeParticles.remove( particle );

        //TODO: eliminate this cast
        model.sphericalParticles.remove( (SphericalParticle) particle );

        model.addSaltCrystal( crystal );
    }
}