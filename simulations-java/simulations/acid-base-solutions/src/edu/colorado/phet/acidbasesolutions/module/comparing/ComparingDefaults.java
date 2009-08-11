/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.comparing;

import edu.colorado.phet.acidbasesolutions.model.Acid.CustomAcid;
import edu.colorado.phet.acidbasesolutions.model.Base.CustomBase;
import edu.colorado.phet.acidbasesolutions.persistence.ComparingConfig;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * ComparingDefaults contains default settings for ComparingModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ComparingDefaults {
    
    // default sizes
    public static final PDimension BEAKER_SIZE = new PDimension( 360, 400 );
    public static final PDimension CONCENTRATION_GRAPH_OUTLINE_SIZE = new PDimension( 360, 350 );

    private static ComparingDefaults INSTANCE = new ComparingDefaults();
    
    public static ComparingDefaults getInstance() {
        return INSTANCE;
    }
    
    private final ComparingConfig config;
    
    /* singleton */
    private ComparingDefaults() {
        config = new ComparingConfig();
        // left solution controls
        config.setSoluteNameLeft( new CustomAcid().getName() );
        config.setConcentrationLeft( 1E-2 );
        config.setStrengthLeft( 1E-6 );
        // right solution controls
        config.setSoluteNameRight( new CustomBase().getName() );
        config.setConcentrationRight( 1E-2 );
        config.setStrengthRight( 1E-6 );
        // view control
        config.setBeakersSelected( true );
        config.setGraphsSelected( false );
        config.setEquationsSelected( false );
        // beaker view controls
        config.setSoluteComponentsRatioVisible( false );
        config.setHydroniumHydroxideRatioVisible( false );
        config.setMoleculeCountsVisible( false );
        config.setBeakerLabelVisible( false );
        // equations controls
        config.setEquationsScalingEnabled( false );
    }
    
    public ComparingConfig getConfig() {
        return config;
    }
}
