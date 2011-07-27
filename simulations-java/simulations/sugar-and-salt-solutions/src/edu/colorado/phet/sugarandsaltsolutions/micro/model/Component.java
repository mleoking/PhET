package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.CalciumIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.ChlorideIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.SodiumIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate.NitrateMolecule;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.SucroseMolecule;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;

/**
 * A single component in a crystal lattice such as an element (ion) or molecule.
 *
 * @author Sam Reid
 */
public abstract class Component {

    //Means for converting from the lattice graph data structure "Component" to instances of particle that will take place in the physical model
    public abstract Particle constructLatticeConstituent( double angle );

    public static class SodiumIon extends Component {
        @Override public String toString() {
            return "Na";
        }

        @Override public Particle constructLatticeConstituent( double angle ) {
            return new SodiumIonParticle();
        }
    }

    public static class ChlorideIon extends Component {
        @Override public String toString() {
            return "Cl";
        }

        @Override public Particle constructLatticeConstituent( double angle ) {
            return new ChlorideIonParticle();
        }
    }

    public static class Nitrate extends Component {
        @Override public String toString() {
            return "NO3";
        }

        @Override public Particle constructLatticeConstituent( double angle ) {
            return new NitrateMolecule( angle, ZERO );
        }
    }

    public static class CalciumIon extends Component {
        @Override public String toString() {
            return "Ca2+";
        }

        @Override public Particle constructLatticeConstituent( double angle ) {
            return new CalciumIonParticle();
        }
    }

    //A sugar molecule, but named sugar component to emphasize its role in lattice construction.
    public static class SucroseComponent extends Component {
        @Override public Particle constructLatticeConstituent( double angle ) {
            return new SucroseMolecule( ImmutableVector2D.ZERO );
        }
    }
}
