/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.testsolution;

import java.awt.Insets;

import edu.colorado.phet.acidbasesolutions.controls.FixedSolutionControls;
import edu.colorado.phet.acidbasesolutions.controls.TestControls;
import edu.colorado.phet.acidbasesolutions.controls.ViewControls;
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
        setInsets( new Insets( 5, 5, 5, 5 ) );
        addControlFullWidth( new FixedSolutionControls( model ) );
        addControlFullWidth( new TestControls( model ) );
        addControlFullWidth( new ViewControls( model ) );
        addResetAllButton( resettable );
    }
}
