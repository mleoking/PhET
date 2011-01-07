// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.module;

import java.awt.Insets;

import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.controls.FixedSolutionControls;
import edu.colorado.phet.acidbasesolutions.controls.TestControls;
import edu.colorado.phet.acidbasesolutions.controls.ViewControls;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.PureWaterSolution;
import edu.colorado.phet.acidbasesolutions.model.ABSModel.SolutionFactory;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;

/**
 * "Introduction" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroductionModule extends ABSModule {
    
    private final ABSModel model;
    private final ABSCanvas canvas;
    private final IntroductionControlPanel controlPanel;
    
    public IntroductionModule( boolean dev ) {
        super( ABSStrings.TEST_SOLUTION );
        
        SolutionFactory solutionFactory = new SolutionFactory() {
            public AqueousSolution createSolution() {
                return new PureWaterSolution();
            }
        };
        model = new ABSModel( solutionFactory );
        
        canvas = new ABSCanvas( model, dev );
        setSimulationPanel( canvas );
        
        controlPanel = new IntroductionControlPanel( this, model );
        setControlPanel( controlPanel );
        
        reset();
    }
    
    @Override
    public void reset() {
        model.reset();
    }
    
    // control panel for this module
    private static class IntroductionControlPanel extends ControlPanel {

        public IntroductionControlPanel( Resettable resettable, ABSModel model ) {
            setInsets( new Insets( 5, 5, 5, 5 ) );
            addControlFullWidth( new FixedSolutionControls( model ) );
            addControlFullWidth( new ViewControls( model ) );
            addControlFullWidth( new TestControls( model ) );
            addResetAllButton( resettable );
        }
    }
}
