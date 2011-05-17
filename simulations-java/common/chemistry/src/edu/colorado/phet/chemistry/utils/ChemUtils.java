package edu.colorado.phet.chemistry.utils;

import java.util.*;

import edu.colorado.phet.chemistry.model.Atomic;

/**
 * Miscellaneous chemistry utils, mostly related to chemical formulas
 */
public class ChemUtils {

    /*
    * Creates a symbol (HTML fragment) based on the list of atoms in the molecule.
    * The atoms must be specified in order of appearance in the symbol.
    * Examples:
    *    [C,C,H,H,H,H] becomes "C<sub>2</sub>H<sub>4</sub>"
    *    [H,H,O] becomes "H<sub>2</sub>O"
    */
    public static String createSymbol( Atomic[] atoms ) {
        return toSubscript( createSymbolWithoutSubscripts( atoms ) );
    }

    /*
    * Creates a symbol (text) based on the list of atoms in the molecule.
    * The atoms must be specified in order of appearance in the symbol.
    * Examples:
    *    [C,C,H,H,H,H] becomes "C2H4"
    *    [H,H,O] becomes "H2O"
    */
    public static String createSymbolWithoutSubscripts( Atomic[] atoms ) {
        StringBuffer b = new StringBuffer();
        int atomCount = 1;
        for ( int i = 0; i < atoms.length; i++ ) {
            if ( i == 0 ) {
                // first atom is treated differently
                b.append( atoms[i].getSymbol() );
            }
            else if ( atoms[i].isSameElement( atoms[i - 1] ) ) {
                // this atom is the same as the previous atom
                atomCount++;
            }
            else {
                // this atom is NOT the same
                if ( atomCount > 1 ) {
                    // create a subscript
                    b.append( String.valueOf( atomCount ) );
                }
                atomCount = 1;
                b.append( atoms[i].getSymbol() );
            }
        }
        if ( atomCount > 1 ) {
            // create a subscript for the final atom
            b.append( String.valueOf( atomCount ) );
        }
        return b.toString();
    }

    /**
     * Return an integer that can be used for sorting atom symbols alphabetically. Lower values will be returned for
     * symbols that should go first. Two-letter symbols will come after a one-letter symbol with the same first
     * character (Br after B). See http://en.wikipedia.org/wiki/Hill_system, for without carbon
     *
     * @param atom An atom
     * @return Value for sorting
     */
    private static int nonCarbonHillSortValue( Atomic atom ) {
        int value = 1000 * ( (int) atom.getSymbol().charAt( 0 ) );
        if ( atom.getSymbol().length() > 1 ) {
            value += (int) atom.getSymbol().charAt( 1 );
        }
        return value;
    }

    /**
     * Returns an integer that can be used for sorting atom symbols for the Hill system when the molecule contains
     * carbon. See http://en.wikipedia.org/wiki/Hill_system
     *
     * @param atom An atom
     * @return Value for sorting (lowest is first)
     */
    private static int carbonHillSortValue( Atomic atom ) {
        if ( atom.isCarbon() ) {
            return 0;
        }
        if ( atom.isHydrogen() ) {
            return 1;
        }
        return nonCarbonHillSortValue( atom );
    }

    /**
     * @param atoms A collection of atoms in a molecule
     * @return The molecular formula of the molecule in the Hill system. Returned as an HTML fragment. See
     *         http://en.wikipedia.org/wiki/Hill_system for more information.
     */
    public static String hillOrderedSymbol( Collection<Atomic> atoms ) {
        boolean containsCarbon = false;
        for ( Atomic atom : atoms ) {
            if ( atom.isCarbon() ) {
                containsCarbon = true;
                break;
            }
        }
        List<Atomic> sortedAtoms = new LinkedList<Atomic>( atoms );
        if ( containsCarbon ) {
            // carbon first, then hydrogen, then others alphabetically
            Collections.sort( sortedAtoms, new Comparator<Atomic>() {
                public int compare( Atomic a, Atomic b ) {
                    return new Integer( carbonHillSortValue( a ) ).compareTo( carbonHillSortValue( b ) );
                }
            } );
        }
        else {
            // compare alphabetically since there is no carbon
            Collections.sort( sortedAtoms, new Comparator<Atomic>() {
                public int compare( Atomic a, Atomic b ) {
                    return new Integer( nonCarbonHillSortValue( a ) ).compareTo( nonCarbonHillSortValue( b ) );
                }
            } );
        }
        return createSymbol( sortedAtoms.toArray( new Atomic[sortedAtoms.size()] ) );
    }

    /*
    * Handles HTML subscript formatting of molecule symbols.
    * All numbers in a string are assumed to be part of a subscript, and will be enclosed in a <sub> tag.
    * For example, "C2H4" becomes "C<sub>2</sub>H<sub>4</sub>".
    */
    public static String toSubscript( String inString ) {
        String outString = "";
        boolean sub = false; // are we in a <sub> tag?
        for ( int i = 0; i < inString.length(); i++ ) {
            final char c = inString.charAt( i );
            if ( !sub && Character.isDigit( c ) ) {
                // start the subscript tag when a digit is found
                outString += "<sub>";
                sub = true;
            }
            else if ( sub && !Character.isDigit( c ) ) {
                // end the subscript tag when a non-digit is found
                outString += "</sub>";
                sub = false;
            }
            outString += c;
        }
        // end the subscript tag if inString ends with a digit
        if ( sub ) {
            outString += "</sub>";
            sub = false;
        }
        return outString;
    }
}
