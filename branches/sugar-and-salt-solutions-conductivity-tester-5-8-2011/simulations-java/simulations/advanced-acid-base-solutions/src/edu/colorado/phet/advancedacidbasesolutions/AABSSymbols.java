// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.advancedacidbasesolutions;

/**
 * Collection of symbols used in this sim.
 * These do not require localization, their use is universal.
 * <p>
 * HTML is used to handle superscripts and subscripts.
 * Many symbols are HTML fragments, so they can be combined into more complex HTML documents.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AABSSymbols {
    
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
    
    // specific acids and their conjugate bases
    public static final String HCl = "HCl";
    public static final String Cl_MINUS = "Cl<sup>-</sup>";
    public static final String HClO4 = "HClO<sub>4</sub>";
    public static final String ClO4_MINUS = "ClO<sub>4</sub><sup>-</sup>";
    public static final String HClO2 = "HClO<sub>2</sub>";
    public static final String ClO2_MINUS = "ClO<sub>2</sub><sup>-</sup>";
    public static final String HClO = "HClO";
    public static final String ClO_MINUS = "ClO<sup>-</sup>";
    public static final String HF = "HF";
    public static final String F_MINUS = "F<sup>-</sup>";
    public static final String CH3COOH = "CH<sub>3</sub>COOH";
    public static final String CH3COO_MINUS = "CH<sub>3</sub>COO<sup>-</sup>";
    
    // specific weak bases and their conjugate acids
    public static final String NH3 = "NH<sub>3</sub>";
    public static final String NH4_PLUS = "NH<sub>4</sub><sup>+</sup>";
    public static final String C5H5N = "C<sub>5</sub>H<sub>5</sub>N";
    public static final String C5H5NH_PLUS = "C<sub>5</sub>H<sub>5</sub>NH<sup>+</sup>";
    
    // specific strong bases and their metals
    public static final String NaOH = "NaOH";
    public static final String Na_PLUS = "Na<sup>+</sup>";
}
