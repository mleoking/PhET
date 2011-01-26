// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations;

/**
 * Collection of symbols used in this sim.
 * These do not require localization, their use is universal.
 * HTML is used to create subscripts.
 * Symbols that are HTML fragments can be combined into more complex HTML documents.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BCESymbols {

    /* not intended for instantiation */
    private BCESymbols() {}

    // atoms
    public static final String C = "C";
    public static final String H = "H";
    public static final String N = "N";
    public static final String O = "O";
    public static final String S = "S";

    // molecules
    public static final String CH4 = toSubscript( "CH4" );
    public static final String CO2 = toSubscript( "CO2" );
    public static final String H2 = toSubscript( "H2" );
    public static final String H2O = toSubscript( "H2O" );
    public static final String N2 = toSubscript( "N2" );
    public static final String NH3 = toSubscript( "NH3" );
    public static final String O2 = toSubscript( "O2" );

    /*
     * Handles HTML subscript formatting.
     * All numbers in a string are assumed to be part of a subscript, and will be enclosed in a <sub> tag.
     * For example, "C2H4" is converted to "C<sub>2</sub>H<sub>4</sub>".
     */
    private static final String toSubscript( String inString ) {
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
        // end the subscript tag if the string ends with a digit
        if ( sub ) {
            outString += "</sub>";
            sub = false;
        }
        return outString;
    }
}
