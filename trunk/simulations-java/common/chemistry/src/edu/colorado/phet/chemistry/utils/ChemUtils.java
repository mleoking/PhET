package edu.colorado.phet.chemistry.utils;

import java.util.*;

import edu.colorado.phet.chemistry.model.Element;

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
    public static String createSymbol( Element[] elements ) {
        return toSubscript( createSymbolWithoutSubscripts( elements ) );
    }

    /*
    * Creates a symbol (text) based on the list of atoms in the molecule.
    * The atoms must be specified in order of appearance in the symbol.
    * Examples:
    *    [C,C,H,H,H,H] becomes "C2H4"
    *    [H,H,O] becomes "H2O"
    */
    public static String createSymbolWithoutSubscripts( Element[] elements ) {
        StringBuffer b = new StringBuffer();
        int atomCount = 1;
        for ( int i = 0; i < elements.length; i++ ) {
            if ( i == 0 ) {
                // first atom is treated differently
                b.append( elements[i].getSymbol() );
            }
            else if ( elements[i].equals( elements[i - 1] ) ) {
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
                b.append( elements[i].getSymbol() );
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
     * @param element An element
     * @return Value for sorting
     */
    private static int nonCarbonHillSortValue( Element element ) {
        int value = 1000 * ( (int) element.getSymbol().charAt( 0 ) );
        if ( element.getSymbol().length() > 1 ) {
            value += (int) element.getSymbol().charAt( 1 );
        }
        return value;
    }

    /**
     * Returns an integer that can be used for sorting atom symbols for the Hill system when the molecule contains
     * carbon. See http://en.wikipedia.org/wiki/Hill_system
     *
     * @param element An element
     * @return Value for sorting (lowest is first)
     */
    private static int carbonHillSortValue( Element element ) {
        if ( element.isCarbon() ) {
            return 0;
        }
        if ( element.isHydrogen() ) {
            return 1;
        }
        return nonCarbonHillSortValue( element );
    }

    /**
     * @param atoms A collection of atoms in a molecule
     * @return The molecular formula of the molecule in the Hill system. Returned as an HTML fragment. See
     *         http://en.wikipedia.org/wiki/Hill_system for more information.
     */
    public static String hillOrderedSymbol( Collection<Element> atoms ) {
        boolean containsCarbon = false;
        for ( Element atom : atoms ) {
            if ( atom.isCarbon() ) {
                containsCarbon = true;
                break;
            }
        }
        List<Element> sortedAtoms = new LinkedList<Element>( atoms );
        if ( containsCarbon ) {
            // carbon first, then hydrogen, then others alphabetically
            Collections.sort( sortedAtoms, new Comparator<Element>() {
                public int compare( Element a, Element b ) {
                    return new Integer( carbonHillSortValue( a ) ).compareTo( carbonHillSortValue( b ) );
                }
            } );
        }
        else {
            // compare alphabetically since there is no carbon
            Collections.sort( sortedAtoms, new Comparator<Element>() {
                public int compare( Element a, Element b ) {
                    return new Integer( nonCarbonHillSortValue( a ) ).compareTo( nonCarbonHillSortValue( b ) );
                }
            } );
        }
        return createSymbol( sortedAtoms.toArray( new Element[sortedAtoms.size()] ) );
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
