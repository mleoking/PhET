// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier;

import edu.colorado.phet.fourier.enums.Domain;
import edu.colorado.phet.fourier.enums.MathForm;
import edu.colorado.phet.fourier.enums.WaveType;


/**
 * MathStrings contains characters, strings and utility functions
 * related to the drawing of mathematical equations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MathStrings {

    /* Not intended for instantiation. */
    private MathStrings() {
    }

    //----------------------------------------------------------------------------
    // Math symbol characters
    //----------------------------------------------------------------------------

    // Greek characters
    public static char C_LAMDA = '\u03BB';
    public static char C_OMEGA = '\u03C9';
    public static char C_PI = '\u03c0';
    public static char C_SIGMA = '\u03a3';

    // Symbols
    public static char C_AMPLITUDE = 'A';
    public static char C_ANGULAR_FREQUENCY = C_OMEGA;
    public static char C_FREQUENCY = 'f';
    public static char C_INFINITY = '\u221E';
    public static char C_INTEGRAL = '\u222b';
    public static char C_LENGTH = 'L';
    public static char C_MODE = 'n';
    public static char C_PERIOD = 'T';
    public static char C_SPACE = 'x';
    public static char C_TIME = 't';
    public static char C_WAVELENGTH = C_LAMDA;
    public static char C_WAVE_NUMBER = 'k';

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
            "{0} sin( 2" + C_PI + "(" + C_SPACE + "/" + C_WAVELENGTH + "<sub>{1}</sub> - " + C_TIME + "/" + C_PERIOD + "<sub>{1}</sub> ) )";
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
     * @throws IllegalArgumentException if domain is not supported
     */
    public static String getFunction( Domain domain ) {

        String function = null;

        if ( domain == Domain.SPACE ) {
            function = MathStrings.F_X;
        }
        else if ( domain == Domain.TIME ) {
            function = MathStrings.F_T;
        }
        else if ( domain == Domain.SPACE_AND_TIME ) {
            function = MathStrings.F_XT;
        }

        if ( function == null ) {
            throw new IllegalArgumentException( "unsupported domain: " + domain );
        }

        return function;
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
     * @param waveType
     * @return the term format string
     * @throws IllegalArgumentException if the an illegal combination of args is provided
     */
    public static String getTerm( Domain domain, MathForm mathForm, WaveType waveType ) {

        String term = null;

        if ( domain == Domain.SPACE ) {
            if ( mathForm == MathForm.WAVE_NUMBER ) {
                term = MathStrings.WAVE_NUMBER_X;
            }
            else if ( mathForm == MathForm.WAVELENGTH ) {
                term = MathStrings.WAVELENGTH_X;
            }
            else if ( mathForm == MathForm.MODE ) {
                term = MathStrings.MODE_X;
            }
        }
        else if ( domain == Domain.TIME ) {
            if ( mathForm == MathForm.ANGULAR_FREQUENCY ) {
                term = MathStrings.ANGULAR_FREQUENCY_T;
            }
            else if ( mathForm == MathForm.FREQUENCY ) {
                term = MathStrings.FREQUENCY_T;
            }
            else if ( mathForm == MathForm.PERIOD ) {
                term = MathStrings.PERIOD_T;
            }
            else if ( mathForm == MathForm.MODE ) {
                term = MathStrings.MODE_T;
            }
        }
        else if ( domain == Domain.SPACE_AND_TIME ) {
            if ( mathForm == MathForm.WAVE_NUMBER_AND_ANGULAR_FREQUENCY ) {
                term = MathStrings.WAVENUMBER_AND_ANGULAR_FREQUENCY_XT;
            }
            else if ( mathForm == MathForm.WAVELENGTH_AND_PERIOD ) {
                term = MathStrings.WAVELENGTH_AND_PERIOD_XT;
            }
            else if ( mathForm == MathForm.MODE ) {
                term = MathStrings.MODE_XT;
            }
        }

        if ( term == null ) {
            throw new IllegalArgumentException(
                    "illegal combination of domain (" + domain + ") " + "and math form (" + mathForm + ")" );
        }

        // All of the equations are in terms of sine. Do we need to change to cosine?
        if ( waveType == WaveType.COSINES ) {
            term = term.replaceAll( "sin\\(", "cos(" );
        }

        return term;
    }
}
