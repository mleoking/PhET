/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions;

/**
 * Collection of symbols used in this sim.
 * These do not require localization, their use is universal.
 * <p>
 * HTML is used to handle superscripts and subscripts.
 * May symbols are HTML fragments, so they can be combined into more 
 * complex HTML documents.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSSymbols {
    
    // general symbols, HTML fragments
    public static final String H2O = "H<sub>2</sub>O";
    public static final String H3O_PLUS = "H<sub>3</sub>O<sup>+</sup>";
    public static final String OH_MINUS = "OH<sup>-</sup>";
    public static final String HA = "HA";
    public static final String HA_EMPHASIS = "H<i>A</i>";
    public static final String A_MINUS = "A<sup>-</sup>";
    public static final String B = "B";
    public static final String B_EMPHASIS = "<i>B</i>";
    public static final String BH_PLUS = "BH<sup>+</sup>";
    public static final String MOH = "MOH";
    public static final String MOH_EMPHASIS = "<i>M</i>OH";
    public static final String M_PLUS = "M<sup>+</sup>";
    public static final String Ka = "K<sub>a</sub>";
    public static final String Kb = "K<sub>b</sub>";
    public static final String Kw = "K<sub>w</sub>";
    
    // symbols for specific acids and their conjugate bases, HTML fragments
    public static final String HCl = "HCl";
    public static final String Cl_MINUS = "Cl<sup>-</sup>";
    public static final String HClO = "HClO";
    public static final String ClO_MINUS = "ClO<sup>-</sup>";
    
    // symbols for specific bases and their conjugate acids, HTML fragments
    public static final String CH3NH2 = "CH<sub>3</sub>NH<sub>2</sub>";
    public static final String CH3NH3 = "CH<sub>3</sub>NH<sub>3</sub>";
    public static final String NaOH = "NaOH";
    public static final String Na_PLUS = "Na<sup>+</sup>";
}
