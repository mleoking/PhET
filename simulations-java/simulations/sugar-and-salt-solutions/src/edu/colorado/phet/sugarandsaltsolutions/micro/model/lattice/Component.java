package edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice;

/**
 * A single component in a crystal lattice such as an element (ion) or molecule.
 *
 * @author Sam Reid
 */
public class Component {
    public static class SodiumIon extends Component {
        @Override public String toString() {
            return "Na";
        }
    }

    static class ChlorideIon extends Component {
        @Override public String toString() {
            return "Cl";
        }
    }

    static class Nitrate extends Component {
        @Override public String toString() {
            return "NO3";
        }
    }
}
