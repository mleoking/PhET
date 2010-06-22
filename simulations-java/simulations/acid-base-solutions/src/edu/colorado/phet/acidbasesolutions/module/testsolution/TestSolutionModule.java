/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.testsolution;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.PureWaterSolution;
import edu.colorado.phet.acidbasesolutions.module.ABSModule;

/**
 * "Test Solution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestSolutionModule extends ABSModule {
    
    private static final AqueousSolution DEFAULT_SOLUTION = new PureWaterSolution();
    
    private final ABSModel model;
    private final TestSolutionCanvas canvas;
    private final TestSolutionControlPanel controlPanel;
    
    public TestSolutionModule() {
        super( ABSStrings.TEST_SOLUTION );
        
        model = new ABSModel( DEFAULT_SOLUTION );
        
        canvas = new TestSolutionCanvas( model );
        setSimulationPanel( canvas );
        
        controlPanel = new TestSolutionControlPanel( this, model );
        setControlPanel( controlPanel );
        
        reset();
    }
    
    @Override
    public void reset() {
        model.setSolution( DEFAULT_SOLUTION );
        model.getMagnifyingGlass().setVisible( ABSConstants.MAGNIFYING_GLASS_VISIBLE );
        model.getConcentrationGraph().setVisible( ABSConstants.CONCENTRATION_GRAPH_VISIBLE );
        model.getPHMeter().setLocation( ABSConstants.PH_METER_LOCATION );
        model.getPHMeter().setVisible( ABSConstants.PH_METER_VISIBLE );
        model.setWaterVisible( ABSConstants.WATER_VISIBLE );
    }
}
