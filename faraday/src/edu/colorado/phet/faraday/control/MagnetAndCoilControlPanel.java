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

import javax.swing.*;

import edu.colorado.phet.faraday.control.panel.BarMagnetPanel;
import edu.colorado.phet.faraday.control.panel.PickupCoilPanel;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.module.MagnetAndCoilModule;
import edu.colorado.phet.faraday.view.BarMagnetGraphic;
import edu.colorado.phet.faraday.view.CoilGraphic;
import edu.colorado.phet.faraday.view.CompassGridGraphic;
import edu.colorado.phet.faraday.view.FieldMeterGraphic;

/**
 * MagnetAndCoilControlPanel is the control panel for the "Magnet & Coil" module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MagnetAndCoilControlPanel extends FaradayControlPanel {

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
     * @param barMagnetModel
     * @param compassModel
     * @param barMagnetGraphic
     * @param gridGraphic
     * @param fieldMeterGraphic
     * @param pickupCoilModel
     * @param coilGraphic
     * @param lightBulbGraphic
     * @param voltmeterGraphic
     */
    public MagnetAndCoilControlPanel( 
        MagnetAndCoilModule module,
        /* Bar Magnet stuff */
        BarMagnet barMagnetModel,
        Compass compassModel,
        BarMagnetGraphic barMagnetGraphic,
        CompassGridGraphic gridGraphic,
        FieldMeterGraphic fieldMeterGraphic,
        /* Pickup Coil stuff */
        PickupCoil pickupCoilModel,
        CoilGraphic coilGraphic,
        Lightbulb lightbulbModel,
        Voltmeter voltmeterModel ) {

        super( module );
        
        BarMagnetPanel barMagnetPanel = new BarMagnetPanel( barMagnetModel, compassModel, barMagnetGraphic, gridGraphic, fieldMeterGraphic );
        barMagnetPanel.setSeeInsideVisible( false );
        barMagnetPanel.setFieldMeterEnabled( false );
        addFullWidth( barMagnetPanel );
        
        PickupCoilPanel pickupCoilPanel = new PickupCoilPanel( pickupCoilModel, coilGraphic, lightbulbModel, voltmeterModel );
        addFullWidth( pickupCoilPanel );
    }
}