/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.customsolution;

import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.WeakAcidSolution.CustomWeakAcidSolution;
import edu.colorado.phet.acidbasesolutions.module.ABSModule;

/**
 * "Custom Solution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CustomSolutionModule extends ABSModule {

    private final ABSModel model;
    private final CustomSolutionCanvas canvas;
    private final CustomSolutionControlPanel controlPanel;
    
    public CustomSolutionModule() {
        super( ABSStrings.CUSTOM_SOLUTION );
        
        model = new ABSModel( new CustomWeakAcidSolution() );
        
        canvas = new CustomSolutionCanvas( model );
        setSimulationPanel( canvas );
        
        controlPanel = new CustomSolutionControlPanel( this, model );
        setControlPanel( controlPanel );
    }
    
    @Override
    public void reset() {
        //XXX
    }
}
