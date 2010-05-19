/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.dielectric;

import java.awt.Frame;

import edu.colorado.phet.capacitorlab.control.DielectricPropertiesControlPanel;
import edu.colorado.phet.capacitorlab.control.MetersControlPanel;
import edu.colorado.phet.capacitorlab.control.ViewControlPanel;
import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.module.CLControlPanel;

/**
 * Control panel for the "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricControlPanel extends CLControlPanel {

    public DielectricControlPanel( Frame parentFrame, DielectricModule module, CLModel model, DielectricCanvas canvas, boolean dev ) {
        addControlFullWidth( new ViewControlPanel() );
        addControlFullWidth( new MetersControlPanel( parentFrame, model, canvas, dev ) );
        addControlFullWidth( new DielectricPropertiesControlPanel( model ) );
        addResetAllButton( module );
    }
}
