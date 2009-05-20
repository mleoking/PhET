/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.Acid.CustomAcid;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * SolutionsDefaults contains default settings for SolutionsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionsDefaults {

    /* Not intended for instantiation */
    private SolutionsDefaults() {}
    
    // default sizes
    public static final PDimension BEAKER_SIZE = new PDimension( 400, 400 );
    public static final double PH_PROBE_HEIGHT = BEAKER_SIZE.getHeight() + 55;
    public static final PDimension CONCENTRATION_GRAPH_OUTLINE_SIZE = new PDimension( 400, 500 );
    
    // solution controls
    public static final Solute SOLUTE = new CustomAcid();
    public static final double CONCENTRATION = 1E-2;
    public static final double STRENGTH = 1E-6;
    
    // visibility controls
    public static final boolean DISASSOCIATED_COMPONENTS_RATIO_VISIBLE = false;
    public static final boolean HYDRONIUM_HYDROXIDE_RATIO_VISIBLE = false;
    public static final boolean MOLECULE_COUNTS_VISIBLE = false;
    public static final boolean BEAKER_LABEL_VISIBLE = false;
    public static final boolean CONCENTRATION_GRAPH_VISIBLE = true;
    public static final boolean SYMBOL_LEGEND_VISIBLE = false;
    public static final boolean EQUILIBRIUM_EXPRESSIONS_VISIBLE = false;
    public static final boolean REACTION_EQUATIONS_VISIBLE = false;
    
}
