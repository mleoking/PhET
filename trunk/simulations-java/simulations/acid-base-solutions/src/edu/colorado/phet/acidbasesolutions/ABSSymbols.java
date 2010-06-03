/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions;

/**
 * Collection of symbols used in this sim.
 * These do not require localization, their use is universal.
 * <p>
 * HTML is used to handle superscripts and subscripts.
 * Many symbols are HTML fragments, so they can be combined into more complex HTML documents.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSSymbols {
    
    /* not intended for instantiation */
    private ABSSymbols() {}
    
    // generic acid/base/metal symbols
    public static final String A = "<i>A</i>";
    public static final String B = "<i>B</i>";
    public static final String M = "<i>M</i>";
    
    // general symbols
    public static final String H2O = "H<sub>2</sub>O";
    public static final String H3O_PLUS = "H<sub>3</sub>O<sup>+</sup>";
    public static final String OH_MINUS = "OH<sup>-</sup>";
    public static final String HA = "H" + A;
    public static final String A_MINUS = A + "<sup>-</sup>";
    public static final String BH_PLUS = B + "H<sup>+</sup>";
    public static final String MOH = M + "OH";
    public static final String M_PLUS = M + "<sup>+</sup>";
    public static final String Ka = "K<sub>a</sub>";
    public static final String Kb = "K<sub>b</sub>";
    public static final String Kw = "K<sub>w</sub>";
}
