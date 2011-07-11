// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.control;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.moleculepolarity.common.model.MPModel;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;

/**
 * Control panel for this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MPControlPanel extends ControlPanel {

    public MPControlPanel( MPModel model, ViewProperties viewProperties, Resettable resettable ) {
        addControlFullWidth( new ModelPanel( viewProperties.modelRepresentation ) );
        addControlFullWidth( new ViewPanel( viewProperties ) );
        addControlFullWidth( new TestPanel( model.eFieldEnabled ) );
        addResetAllButton( resettable );
    }
}
