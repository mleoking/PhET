/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.dielectric;

import java.awt.Frame;

import edu.colorado.phet.capacitorlab.control.DeveloperControlPanel;
import edu.colorado.phet.capacitorlab.control.DielectricPropertiesControlPanel;
import edu.colorado.phet.capacitorlab.control.MetersControlPanel;
import edu.colorado.phet.capacitorlab.control.ViewControlPanel;
import edu.colorado.phet.capacitorlab.module.CLControlPanel;
import edu.colorado.phet.common.phetcommon.application.Module;

/**
 * Control panel for the "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricControlPanel extends CLControlPanel {
    
    public DielectricControlPanel( Frame parentFrame, Module module, DielectricModel model, DielectricCanvas canvas, boolean hasDielectricPropertiesControl, boolean dev ) {
        addControlFullWidth( new ViewControlPanel( canvas ) );
        addControlFullWidth( new MetersControlPanel( model, canvas ) );
        if ( hasDielectricPropertiesControl ) {
            addControlFullWidth( new DielectricPropertiesControlPanel( model, canvas.getCapacitorNode() ) );
        }
        if ( dev ) {
            addControlFullWidth( new DeveloperControlPanel( parentFrame, model ) );
        }
        addResetAllButton( module );
    }
}
