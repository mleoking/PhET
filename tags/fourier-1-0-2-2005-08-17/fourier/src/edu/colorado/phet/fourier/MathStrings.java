/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier;

import java.text.MessageFormat;


/**
 * MathStrings contains characters, strings and utility functions
 * related to the drawing of mathematical equations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MathStrings {

    /* Not intended for instantiation. */
    private MathStrings() {}
    
    //----------------------------------------------------------------------------
    // Math symbol characters
    //----------------------------------------------------------------------------
    
    public static char C_AMPLITUDE         = 'A';
    public static char C_ANGULAR_FREQUENCY = '\u03C9'; // omega
    public static char C_DELTA             = '\u0394';
    public static char C_FREQUENCY         = 'f';
    public static char C_INFINITY          = '\u221E';
    public static char C_INTEGRAL          = '\u222b';
    public static char C_LENGTH            = 'L';
    public static char C_MODE              = 'n';
    public static char C_PERIOD            = 'T';
    public static char C_PI                = '\u03c0';
    public static char C_SIGMA             = '\u03a3';
    public static char C_SPACE             = 'x';
    public static char C_SQUARE_ROOT       = '\u221a';
    public static char C_TIME              = 't';
    public static char C_WAVELENGTH        = '\u03BB'; // lamda
    public static char C_WAVE_NUMBER       = 'k';
    
    //----------------------------------------------------------------------------
    // Parts for constructing equations
    //----------------------------------------------------------------------------
    
    // An
    private static final String COEFFICIENT = C_AMPLITUDE + "<sub>{0}</sub>";
    private static final String COEFFICIENT_N = C_AMPLITUDE + "<sub>" + C_MODE + "</sub>";

    // Functions
    private static final String F_X = "F(" + C_SPACE + ")";
    private static final String F_T = "F(" + C_TIME + ")";
    private static final String F_XT = "F(" + C_SPACE + "," + C_TIME + ")";
    
    /*
     * The terms are format strings, expressed in the syntax of java.text.MessageFormat.
     * Argument {0} is the amplitude coefficient.
     * Argument {1} is the harmonic number (aka "mode").
     */
    
    // Terms for the space (x) domain
    private static final String WAVE_NUMBER_X = 
        "{0} sin( " + C_WAVE_NUMBER + " <sub>{1}</sub>" + C_SPACE + " )";
    private static final String WAVELENGTH_X = 
        "{0} sin( 2" + C_PI + C_SPACE + " / " + C_WAVELENGTH + "<sub>{1}</sub> )";
    private static final String MODE_X = 
        "{0} sin( 2" + C_PI + "{1}" + C_SPACE + " / " + C_LENGTH + " )";
    
    // Terms for the time (t) domain
    private static final String ANGULAR_FREQUENCY_T = 
        "{0} sin( " + C_ANGULAR_FREQUENCY + "<sub>{1}</sub>" + C_TIME + " )";
    private static final String FREQUENCY_T =
        "{0} sin( 2" + C_PI + C_FREQUENCY + "<sub>{1}</sub>" + C_TIME + " )";
    private static final String PERIOD_T = 
        "{0} sin( 2" + C_PI + C_TIME + " / " + C_PERIOD + "<sub>{1}</sub> )";
    private static final String MODE_T = 
        "{0} sin( 2" + C_PI + "{1}" + C_TIME + " / " + C_PERIOD + " )";
    
    // Terms for the space-&-time (x,t) domain
    private static final String WAVENUMBER_AND_ANGULAR_FREQUENCY_XT = 
        "{0} sin( " + C_WAVE_NUMBER + "<sub>{1}</sub>" + C_SPACE + " - " + C_ANGULAR_FREQUENCY + "<sub>{1}</sub>" + C_TIME + " )";
    private static final String WAVELENGTH_AND_PERIOD_XT =
        "{0} sin( 2" + C_PI + "(" + C_SPACE + "/" +  C_WAVELENGTH + "<sub>{1}</sub> - " + C_TIME + "/" + C_PERIOD + "<sub>{1}</sub> ) )";
    private static final String MODE_XT = 
        "{0} sin( 2" + C_PI + "{1}( " + C_SPACE + "/" + C_LENGTH + " - " + C_TIME + "/" + C_PERIOD + " ) )";
    
    //----------------------------------------------------------------------------
    // Static utility methods
    //----------------------------------------------------------------------------
    
    /**
     * Gets the function string that corresponds to a domain.
     * 
     * @param domain
     * @return the function string
     */
    public static String getFunction( int domain ) {
        
        assert( FourierConstants.isValidDomain( domain ) );
        
        String function = null;
        
        switch ( domain ) {
            case FourierConstants.DOMAIN_SPACE:
                function = MathStrings.F_X;
                break;
            case FourierConstants.DOMAIN_TIME:
                function = MathStrings.F_T;
                break;
            case FourierConstants.DOMAIN_SPACE_AND_TIME:
                function = MathStrings.F_XT;
                break;
           default:
        }
        assert( function != null ); // you added a new domain and forgot to add it here
        
        return function;
    }

    /**
     * Gets the string that represents a generic coefficient.
     */
    public static String getCoefficient( int harmonicNumber ) {
        Object[] args = { new Integer( harmonicNumber ) };
        String coefficient = MessageFormat.format( COEFFICIENT, args );
        return coefficient;
    }
    
    /**
     * Gets the string that represents a generic coefficient.
     */
    public static String getCoefficient() {
        return COEFFICIENT_N;
    }
    
    /**
     * Gets the term that corresponds to a domain and math form.
     * Not all combinations of domain and math form are valid.
     * 
     * @param domain
     * @param mathForm
     * @throws IllegalArgumentException if the an illegal combination of args is provided
     * @return the term format string
     */
    public static String getTerm( int domain, int mathForm ) {
        
        assert( FourierConstants.isValidDomain( domain ) );
        assert( FourierConstants.isValidMathForm( mathForm ) );
        
        String term = null;
        
        switch ( domain ) {
            case FourierConstants.DOMAIN_SPACE:
                switch ( mathForm ) {
                    case FourierConstants.MATH_FORM_WAVE_NUMBER:
                        term = MathStrings.WAVE_NUMBER_X;
                        break;
                    case FourierConstants.MATH_FORM_WAVELENGTH:
                        term = MathStrings.WAVELENGTH_X;
                         break;
                    case FourierConstants.MATH_FORM_MODE:
                        term = MathStrings.MODE_X;
                        break;
                    default:
                }
                break;
            case FourierConstants.DOMAIN_TIME:
                switch ( mathForm ) {
                    case FourierConstants.MATH_FORM_ANGULAR_FREQUENCY:
                        term = MathStrings.ANGULAR_FREQUENCY_T;
                        break;
                    case FourierConstants.MATH_FORM_FREQUENCY:
                        term = MathStrings.FREQUENCY_T;
                        break;
                    case FourierConstants.MATH_FORM_PERIOD:
                        term = MathStrings.PERIOD_T;
                        break;
                    case FourierConstants.MATH_FORM_MODE:
                        term = MathStrings.MODE_T;
                        break;
                    default:
                }
                break;
            case FourierConstants.DOMAIN_SPACE_AND_TIME:
                switch ( mathForm ) {
                    case FourierConstants.MATH_FORM_WAVE_NUMBER_AND_ANGULAR_FREQUENCY:
                        term = MathStrings.WAVENUMBER_AND_ANGULAR_FREQUENCY_XT;
                        break;
                    case FourierConstants.MATH_FORM_WAVELENGTH_AND_PERIOD:
                        term = MathStrings.WAVELENGTH_AND_PERIOD_XT;
                        break;
                    case FourierConstants.MATH_FORM_MODE:
                        term = MathStrings.MODE_XT;
                        break;
                    default:
                }
                break;
            default:
        }
        
        if ( term == null ) {
            throw new IllegalArgumentException( 
                    "illegal combination of domain (" + domain + ") " + "and math form (" + mathForm + ")" );
        }
        
        return term;
    }
}
