package edu.colorado.phet.chemistry.utils;

import edu.colorado.phet.chemistry.model.Atom;

public class ChemUtils {

    /*
    * Creates a symbol (HTML fragment) based on the list of atoms in the molecule.
    * The atoms must be specified in order of appearance in the symbol.
    * Examples:
    *    [C,C,H,H,H,H] becomes "C<sub>2</sub>H<sub>4</sub>"
    *    [HHO] becomes "H<sub>2</sub>O"
    */
    public static String createSymbol( Atom[] atoms ) {
        StringBuffer b = new StringBuffer();
        int atomCount = 1;
        for ( int i = 0; i < atoms.length; i++ ) {
            if ( i == 0 ) {
                // first atom is treated differently
                b.append( atoms[i].getSymbol() );
            }
            else if ( atoms[i].getClass().equals( atoms[i - 1].getClass() ) ) {
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
        return toSubscript( b.toString() );
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
