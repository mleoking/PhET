/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import edu.colorado.phet.acidbasesolutions.model.Acid.CustomAcid;
import edu.colorado.phet.acidbasesolutions.persistence.SolutionsConfig;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * SolutionsDefaults contains default settings for SolutionsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionsDefaults {
    
    // default sizes
    public static final PDimension BEAKER_SIZE = new PDimension( 400, 400 );
    public static final double PH_PROBE_HEIGHT = BEAKER_SIZE.getHeight() + 55;
    public static final PDimension CONCENTRATION_GRAPH_OUTLINE_SIZE = new PDimension( 400, 500 );
    
    private static SolutionsDefaults INSTANCE = new SolutionsDefaults();
    
    public static SolutionsDefaults getInstance() {
        return INSTANCE;
    }
    
    private final SolutionsConfig config;

    /* singleton */
    private SolutionsDefaults() {
        config = new SolutionsConfig();
        // solution controls
        config.setSoluteName( new CustomAcid().getName() );
        config.setConcentration( 1E-2 );
        config.setStrength( 1E-6 );
        // beaker view controls
        config.setDisassociatedComponentsRatioVisible( false );
        config.setHydroniumHydroxideRatioVisible( false );
        config.setMoleculeCountsVisible( false );
        config.setBeakerLabelVisible( false );
        // misc view controls
        config.setConcentrationGraphVisible( true );
        config.setSymbolLegendVisible( false );
        config.setEquilibriumExpressionsVisible( false );
        config.setReactionEquationsVisible( false );
    }
    
    public SolutionsConfig getConfig() {
        return config;
    }
}
