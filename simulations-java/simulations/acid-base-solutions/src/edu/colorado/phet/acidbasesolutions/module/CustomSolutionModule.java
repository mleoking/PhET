// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.module;

import java.awt.Insets;

import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.controls.CustomSolutionControls;
import edu.colorado.phet.acidbasesolutions.controls.TestControls;
import edu.colorado.phet.acidbasesolutions.controls.ViewControls;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.ABSModel.SolutionFactory;
import edu.colorado.phet.acidbasesolutions.model.WeakAcidSolution.CustomWeakAcidSolution;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;

/**
 * "Custom Solution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CustomSolutionModule extends ABSModule {
    
    private final ABSModel model;
    private final ABSCanvas canvas;
    private final CustomSolutionControlPanel controlPanel;
    
    public CustomSolutionModule( boolean dev ) {
        super( ABSStrings.CUSTOM_SOLUTION );
        
        SolutionFactory solutionFactory = new SolutionFactory() {
            public AqueousSolution createSolution() {
                return new CustomWeakAcidSolution();
            }
        };
        model = new ABSModel( solutionFactory );
        
        canvas = new ABSCanvas( model, dev );
        setSimulationPanel( canvas );
        
        controlPanel = new CustomSolutionControlPanel( this, model );
        setControlPanel( controlPanel );
        
        reset();
    }
    
    @Override
    public void reset() {
        model.reset();
    }
    
    // Control panel for this module.
    private static class CustomSolutionControlPanel extends ControlPanel {

        public CustomSolutionControlPanel( Resettable resettable, ABSModel model ) {
            setInsets( new Insets( 2, 5, 2, 5 ) );
            addControlFullWidth( new CustomSolutionControls( model ) );
            addControlFullWidth( new ViewControls( model ) );
            addControlFullWidth( new TestControls( model ) );
            addResetAllButton( resettable );
        }
    }
}
