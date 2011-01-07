/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.model;

import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.view.PeriodicTableNode;

/**
 * This class identifies and classifies atoms based on their configuration of
 * protons, neturons, and electrons.
 *
 * @author John Blanco
 */
public class AtomIdentifier {

    // This data structure maps the proton number, a.k.a. the atomic number,
    // to the element name.
    private static final HashMap<Integer, String> MAP_PROTON_COUNT_TO_NAME = new HashMap<Integer, String>() {
        {
            put( 0, new String( BuildAnAtomStrings.ELEMENT_NONE_NAME ) );//for an unbuilt or empty atom
            put( 1, new String( BuildAnAtomStrings.ELEMENT_HYDROGEN_NAME ) );
            put( 2, new String( BuildAnAtomStrings.ELEMENT_HELIUM_NAME ) );
            put( 3, new String( BuildAnAtomStrings.ELEMENT_LITHIUM_NAME ) );
            put( 4, new String( BuildAnAtomStrings.ELEMENT_BERYLLIUM_NAME ) );
            put( 5, new String( BuildAnAtomStrings.ELEMENT_BORON_NAME ) );
            put( 6, new String( BuildAnAtomStrings.ELEMENT_CARBON_NAME ) );
            put( 7, new String( BuildAnAtomStrings.ELEMENT_NITROGEN_NAME ) );
            put( 8, new String( BuildAnAtomStrings.ELEMENT_OXYGEN_NAME ) );
            put( 9, new String( BuildAnAtomStrings.ELEMENT_FLUORINE_NAME ) );
            put( 10, new String( BuildAnAtomStrings.ELEMENT_NEON_NAME ) );
            put( 11, new String( BuildAnAtomStrings.ELEMENT_SODIUM_NAME ) );
            put( 12, new String( BuildAnAtomStrings.ELEMENT_MAGNESIUM_NAME ) );
            put( 13, new String( BuildAnAtomStrings.ELEMENT_ALUMINUM_NAME ) );
            put( 14, new String( BuildAnAtomStrings.ELEMENT_SILICON_NAME ) );
            put( 15, new String( BuildAnAtomStrings.ELEMENT_PHOSPHORUS_NAME ) );
            put( 16, new String( BuildAnAtomStrings.ELEMENT_SULPHER_NAME ) );
            put( 17, new String( BuildAnAtomStrings.ELEMENT_CHLORINE_NAME ) );
            put( 18, new String( BuildAnAtomStrings.ELEMENT_ARGON_NAME ) );
            put( 19, new String( BuildAnAtomStrings.ELEMENT_POTASSIUM_NAME ) );
            put( 20, new String( BuildAnAtomStrings.ELEMENT_CALCIUM_NAME ) );
        }
    };

    // This data structure lists the isotopes that are considered stable for
    // the purposes of this simulation.  This means that their half life is
    // less than the age of the universe.  The table only goes up as high as
    // needed for the sim.  This is taken from the table at
    // https://docs.google.com/document/edit?id=1VGGhLUetiwijbDneU-U6BPrjRlkI0rt939zk8Y4AuA4&authkey=CMLM4ZUC&hl=en#
    private static final ArrayList<Isotope> STABLE_ISOTOPES = new ArrayList<Isotope>() {
        {
            //H
            add( new Isotope( 1, 0 ) );
            add( new Isotope( 2, 1 ) );
            //He
            add( new Isotope( 3, 1 ) );
            add( new Isotope( 4, 2 ) );
            //Li
            add( new Isotope( 6, 3 ) );
            add( new Isotope( 7, 4 ) );
            //Be
            add( new Isotope( 9, 5 ) );
            //B
            add( new Isotope( 10, 5 ) );
            add( new Isotope( 11, 6 ) );
            //C
            add( new Isotope( 12, 6 ) );
            add( new Isotope( 13, 7 ) );
            //N
            add( new Isotope( 14, 7 ) );
            add( new Isotope( 15, 8 ) );
            //O
            add( new Isotope( 16, 8 ) );
            add( new Isotope( 17, 9 ) );
            add( new Isotope( 18, 10 ) );
            //F
            add( new Isotope( 19, 10 ) );
            //Ne
            add( new Isotope( 20, 10 ) );
            add( new Isotope( 21, 11 ) );
            add( new Isotope( 22, 12 ) );
        }
    };

    public static String getSymbol( IAtom atom ) {
        return getSymbol( atom.getNumProtons() );
    }

    public static String getSymbol( int protonCount ) {
        if ( protonCount == 0 ) {
            return BuildAnAtomStrings.ELEMENT_NONE_SYMBOL;
        }
        else {
            return PeriodicTableNode.getElementAbbreviation( protonCount );
        }
    }

    public static String getName( IAtom atom ) {
        return getName( atom.getNumProtons() );
    }

    public static String getName( int protonCount ) {
        String symbol = MAP_PROTON_COUNT_TO_NAME.get( protonCount );
        if ( symbol == null ) {
            System.err.println( "Error: No element found for proton count = " + protonCount );
            symbol = "Unknown";
        }
        return symbol;
    }

    public static boolean isStable( IAtom atom ) {
        return atom.getMassNumber() == 0 || STABLE_ISOTOPES.contains( new Isotope( atom.getMassNumber(), atom.getNumNeutrons() ) );
    }

    public static double getAtomicMass( IAtom atom ) {
        // TODO Auto-generated method stub
        return 0;
    }

    private static class Isotope {
        public final int massNumber;
        public final int neutronNumber;

        //Regenerate equals and hashcode if you change the contents of the isotope

        public Isotope( int massNumber, int neutronNumber ) {
            this.massNumber = massNumber;
            this.neutronNumber = neutronNumber;
        }

        //Autogenerated

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            Isotope isotope = (Isotope) o;

            if ( massNumber != isotope.massNumber ) {
                return false;
            }
            if ( neutronNumber != isotope.neutronNumber ) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = massNumber;
            result = 31 * result + neutronNumber;
            return result;
        }
    }
}
