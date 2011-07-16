package edu.colorado.phet.sugarandsaltsolutions.micro.model;

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

    public static class ChlorideIon extends Component {
        @Override public String toString() {
            return "Cl";
        }
    }

    public static class Nitrate extends Component {
        @Override public String toString() {
            return "NO3";
        }
    }

    public static class CalciumIon extends Component {
        @Override public String toString() {
            return "Ca2+";
        }
    }
}
