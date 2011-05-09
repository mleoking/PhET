// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers;

/**
 * Collection of symbols used in this sim.
 * These do not require localization, their use is universal.
 * HTML is used to subscripts.
 * Symbols that are HTML fragments can be combined into more complex HTML documents.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RPALSymbols {
    
    /*
     * Symbols for the sandwich components.
     * These do not require localization because the names are not visible to the user.
     * But they are required by StackedLayoutNode to determine which stack an associated image goes in.
     */
    public static final String BREAD = "bread";
    public static final String MEAT = "meat";
    public static final String CHEESE = "cheese";
    public static final String SANDWICH = "sandwich";
    
    // molecules
    public static final String C = toSubscript( "C" );
    public static final String C2H2 = toSubscript( "C2H2" );
    public static final String C2H4 = toSubscript( "C2H4" );
    public static final String C2H5Cl = toSubscript( "C2H5Cl" );
    public static final String C2H5OH = toSubscript( "C2H5OH" );
    public static final String C2H6 = toSubscript( "C2H6" );
    public static final String CH2O = toSubscript( "CH2O" );
    public static final String CH3OH = toSubscript( "CH3OH" );
    public static final String CH4 = toSubscript( "CH4" );
    public static final String Cl2 = toSubscript( "Cl2" );
    public static final String CO = toSubscript( "CO" );
    public static final String CO2 = toSubscript( "CO2" );
    public static final String CS2 = toSubscript( "CS2" );
    public static final String F2 = toSubscript( "F2" );
    public static final String H2 = toSubscript( "H2" );
    public static final String H2O = toSubscript( "H2O" );
    public static final String H2S = toSubscript( "H2S" );
    public static final String HCl = toSubscript( "HCl" );
    public static final String HF = toSubscript( "HF" );
    public static final String N2 = toSubscript( "N2" );
    public static final String N2O = toSubscript( "N2O" );
    public static final String NH3 = toSubscript( "NH3" );
    public static final String NO = toSubscript( "NO" );
    public static final String NO2 = toSubscript( "NO2" );
    public static final String O2 = toSubscript( "O2" );
    public static final String OF2 = toSubscript( "OF2" );
    public static final String P4 = toSubscript( "P4" );
    public static final String PCl3 = toSubscript( "PCl3" );
    public static final String PCl5 = toSubscript( "PCl5" );
    public static final String PF3 = toSubscript( "PF3" );
    public static final String PH3 = toSubscript( "PH3" );
    public static final String S = toSubscript( "S" );
    public static final String SO2 = toSubscript( "SO2" );
    public static final String SO3 = toSubscript( "SO3" );
    
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
