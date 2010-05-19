/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.introduction;

import java.awt.Frame;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.module.CLModule;

/**
 * The "Introduction" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroductionModule extends CLModule {
    
    private final CLModel model;
    private final IntroductionCanvas canvas;
    private final IntroductionControlPanel controlPanel;

    public IntroductionModule( Frame parentFrame, boolean dev ) {
        super( CLStrings.TAB_INTRODUCTION );
        
        model = new CLModel();
        
        canvas = new IntroductionCanvas( model, dev );
        setSimulationPanel( canvas );
        
        controlPanel = new IntroductionControlPanel( this );
        setControlPanel( controlPanel );
    }
    
    @Override
    public void reset() {
        model.reset();
        canvas.reset();
    }
}
