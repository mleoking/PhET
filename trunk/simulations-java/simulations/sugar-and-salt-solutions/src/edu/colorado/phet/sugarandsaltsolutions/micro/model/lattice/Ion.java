package edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice;

/**
 * @author Sam Reid
 */
public class Ion {
    public static class SodiumIon extends Ion {
        @Override public String toString() {
            return "Na";
        }
    }

    static class ChlorideIon extends Ion {
        @Override public String toString() {
            return "Cl";
        }
    }
}
