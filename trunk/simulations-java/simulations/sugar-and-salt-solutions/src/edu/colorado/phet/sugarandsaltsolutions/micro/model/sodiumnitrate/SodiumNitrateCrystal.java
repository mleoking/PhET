// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.SodiumIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.FreeOxygenIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.NitrogenIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.SodiumIonParticle;

import static java.lang.Math.PI;
import static java.lang.Math.random;

/**
 * This crystal for Sodium Chloride salt updates the positions of the molecules to ensure they move as a crystal
 *
 * @author Sam Reid
 */
public class SodiumNitrateCrystal extends Crystal<Particle> {
    private ArrayList<NitrateMolecule> nitrateMolecules = new ArrayList<NitrateMolecule>();

    //The distance between nitrogen and oxygen should be the sum of their radii, but the blue background makes it hard to tell that N and O are bonded.
    //Therefore we bring the outer O's closer to the N so there is some overlap.
    public static final double NITROGEN_OXYGEN_SPACING = ( new NitrogenIonParticle().radius + new FreeOxygenIonParticle().radius ) * 0.85;

    public SodiumNitrateCrystal( ImmutableVector2D position, SodiumNitrateLattice lattice ) {
        super( position );

        //Recursive method to traverse the graph and create particles
        fill( lattice, lattice.components.getFirst(), new ArrayList<edu.colorado.phet.sugarandsaltsolutions.micro.model.Component>(), new ImmutableVector2D() );

        //Update positions so the lattice position overwrites constituent particle positions
        stepInTime( new ImmutableVector2D(), 0.0 );
    }

    //Recursive method to traverse the graph and create particles at the right locations
    private void fill( SodiumNitrateLattice lattice, Component component, ArrayList<Component> handled, ImmutableVector2D relativePosition ) {

        if ( component instanceof SodiumIon ) {
            constituents.add( new Constituent( new SodiumIonParticle(), relativePosition ) );
        }
        //Nitrate
        else {
            //Keep track of relative positions so the nitrate group won't dissolve
            addNitrate( new NitrateMolecule( angle, relativePosition ), relativePosition );
        }
        handled.add( component );
        ArrayList<Bond> bonds = lattice.getBonds( component );
        final double spacing = new SodiumIonParticle().radius * 2 + NITROGEN_OXYGEN_SPACING;
        for ( Bond bond : bonds ) {
            if ( !handled.contains( bond.destination ) ) {
                fill( lattice, bond.destination, handled, relativePosition.plus( getDelta( spacing, bond ).getRotatedInstance( angle ) ) );
            }
        }
    }

    private void addNitrate( NitrateMolecule nitrateMolecule, ImmutableVector2D relativePosition ) {
        nitrateMolecules.add( nitrateMolecule );
        for ( Constituent<Particle> constituent : nitrateMolecule ) {
            constituents.add( new Constituent<Particle>( constituent.particle, constituent.location.plus( relativePosition ) ) );
        }
    }

    public ArrayList<NitrateMolecule> getNitrateMolecules() {
        return nitrateMolecules;
    }

    //Dissolves the sodium nitrate crystal, but keeps the structure of the NO3 molecules
    public ArrayList<? extends Particle> dissolve() {
        ArrayList<Particle> freeParticles = new ArrayList<Particle>();

        //Set the sodium ions free
        for ( Constituent constituent : this ) {
            constituent.particle.velocity.set( velocity.get().getRotatedInstance( random() * PI * 2 ) );
            if ( constituent.particle instanceof SodiumIonParticle ) {
                freeParticles.add( constituent.particle );
            }
        }

        //add new compounds for the nitrates so they will remain together
        for ( final NitrateMolecule nitrateMolecule : getNitrateMolecules() ) {
            nitrateMolecule.position.set( position.get().plus( nitrateMolecule.position.get() ) );
            nitrateMolecule.velocity.set( velocity.get().getRotatedInstance( random() * PI * 2 ) );
            freeParticles.add( nitrateMolecule );
        }
        return freeParticles;
    }
}