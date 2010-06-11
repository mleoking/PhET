/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.testsolution;

import edu.colorado.phet.acidbasesolutions.controls.TestSolutionControl;
import edu.colorado.phet.acidbasesolutions.controls.ToolsControl;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;

/**
 * Control panel for the "Test Solution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestSolutionControlPanel extends ControlPanel {

    public TestSolutionControlPanel( Resettable resettable, ABSModel model ) {
        addControlFullWidth( new TestSolutionControl( model ) );
        addControlFullWidth( new ToolsControl( model ) );
        addResetAllButton( resettable );
    }
}
