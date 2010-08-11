/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.customsolution;

import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.ABSModel.SolutionFactory;
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
    
    public CustomSolutionModule( boolean dev ) {
        super( ABSStrings.CUSTOM_SOLUTION );
        
        SolutionFactory solutionFactory = new SolutionFactory() {
            public AqueousSolution createSolution() {
                return new CustomWeakAcidSolution();
            }
        };
        model = new ABSModel( getClock(), solutionFactory );
        
        canvas = new CustomSolutionCanvas( model, dev );
        setSimulationPanel( canvas );
        
        controlPanel = new CustomSolutionControlPanel( this, model );
        setControlPanel( controlPanel );
        
        reset();
    }
    
    @Override
    public void reset() {
        model.reset();
    }
}
