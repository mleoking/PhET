/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.customsolution;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakAcidSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakBaseSolution;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.ICustomSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakAcidSolution.CustomWeakAcidSolution;
import edu.colorado.phet.acidbasesolutions.module.ABSModule;

/**
 * "Custom Solution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CustomSolutionModule extends ABSModule {

    private static final AqueousSolution DEFAULT_SOLUTION = new CustomWeakAcidSolution();
    
    private final ABSModel model;
    private final CustomSolutionCanvas canvas;
    private final CustomSolutionControlPanel controlPanel;
    
    public CustomSolutionModule() {
        super( ABSStrings.CUSTOM_SOLUTION );
        
        model = new ABSModel( DEFAULT_SOLUTION );
        
        canvas = new CustomSolutionCanvas( model );
        setSimulationPanel( canvas );
        
        controlPanel = new CustomSolutionControlPanel( this, model );
        setControlPanel( controlPanel );
        
        reset();
    }
    
    @Override
    public void reset() {
        model.setSolution( DEFAULT_SOLUTION );
        AqueousSolution solution = model.getSolution();
        if ( solution instanceof ICustomSolution ) {
            ICustomSolution customSolution = (ICustomSolution) solution;
            customSolution.setConcentration( ABSConstants.CONCENTRATION_RANGE.getDefault() );
            if ( customSolution instanceof WeakAcidSolution || customSolution instanceof WeakBaseSolution ) {
                customSolution.setStrength( ABSConstants.WEAK_STRENGTH_RANGE.getDefault() );
            }
        }
        model.getMagnifyingGlass().setVisible( ABSConstants.MAGNIFYING_GLASS_VISIBLE );
        model.getConcentrationGraph().setVisible( ABSConstants.CONCENTRATION_GRAPH_VISIBLE );
        model.getPHMeter().setLocation( ABSConstants.PH_METER_LOCATION );
        model.getPHMeter().setVisible( ABSConstants.PH_METER_VISIBLE );
        model.setWaterVisible( ABSConstants.WATER_VISIBLE );
    }
}
