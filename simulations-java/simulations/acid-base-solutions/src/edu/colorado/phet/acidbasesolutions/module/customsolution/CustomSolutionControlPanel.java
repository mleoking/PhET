/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.customsolution;

import java.awt.Insets;

import edu.colorado.phet.acidbasesolutions.controls.CustomSolutionControls;
import edu.colorado.phet.acidbasesolutions.controls.TestControls;
import edu.colorado.phet.acidbasesolutions.controls.ViewControls;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;

/**
 * Control panel for the "Custom Solution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CustomSolutionControlPanel extends ControlPanel {

    public CustomSolutionControlPanel( Resettable resettable, ABSModel model ) {
        setInsets( new Insets( 2, 5, 2, 5 ) );
        addControlFullWidth( new CustomSolutionControls( model ) );
        addControlFullWidth( new TestControls( model ) );
        addControlFullWidth( new ViewControls( model ) );
        addResetAllButton( resettable );
    }
}
