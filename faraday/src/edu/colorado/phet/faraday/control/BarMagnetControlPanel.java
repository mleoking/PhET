/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control;

import edu.colorado.phet.faraday.control.panel.BarMagnetPanel;
import edu.colorado.phet.faraday.model.BarMagnet;
import edu.colorado.phet.faraday.model.Compass;
import edu.colorado.phet.faraday.module.BarMagnetModule;
import edu.colorado.phet.faraday.view.BarMagnetGraphic;
import edu.colorado.phet.faraday.view.CompassGridGraphic;
import edu.colorado.phet.faraday.view.FieldMeterGraphic;

/**
 * BarMagnetControlPanel is the control panel for the "Bar Magnet" module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnetControlPanel extends FaradayControlPanel {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * <p>
     * The structure of the code (the way that code blocks are nested)
     * reflects the structure of the panel.
     * 
     * @param module the module that this control panel is associated with.
     * @param magnetModel
     * @param compassModel
     * @param magnetGraphic
     * @param gridGraphic
     * @param fieldMeterGraphic
     */
    public BarMagnetControlPanel( 
            BarMagnetModule module, 
            BarMagnet magnetModel, 
            Compass compassModel, 
            BarMagnetGraphic magnetGraphic, 
            CompassGridGraphic gridGraphic, 
            FieldMeterGraphic fieldMeterGraphic ) {

        super( module );
        BarMagnetPanel barMagnetPanel = new BarMagnetPanel( magnetModel, compassModel, 
                magnetGraphic, gridGraphic, fieldMeterGraphic );
        addFullWidth( barMagnetPanel );
    }
}