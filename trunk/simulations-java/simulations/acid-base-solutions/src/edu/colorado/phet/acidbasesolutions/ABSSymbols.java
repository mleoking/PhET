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
    
    // generic acid/base symbols, HTM fragments
    public static final String A = "<i>A</i>";
    public static final String B = "<i>B</i>";
    public static final String M = "<i>M</i>";
    
    // general symbols, HTML fragments
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
    
    // symbols for specific acids and their conjugate bases, HTML fragments
    public static final String HCl = "HCl";
    public static final String Cl_MINUS = "Cl<sup>-</sup>";
    public static final String HClO = "HClO";
    public static final String ClO_MINUS = "ClO<sup>-</sup>";
    
    // symbols for specific weak bases and their conjugate acids, HTML fragments
    public static final String NaOH = "NaOH";
    public static final String Na_PLUS = "Na<sup>+</sup>";
    public static final String NH3 = "NH<sub>3</sub>";
    public static final String NH4_PLUS = "NH<sub>4</sub><sup>+</sup>";
    public static final String C5H5N = "C<sub>5</sub>H<sub>5</sub>N";
    public static final String C5H5NH_PLUS = "C<sub>5</sub>H<sub>5</sub>NH<sup>+</sup>";
    
   // symbols for specific strong bases and their metals, HTML fragments
}
