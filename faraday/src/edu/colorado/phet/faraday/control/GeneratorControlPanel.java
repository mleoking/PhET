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
import edu.colorado.phet.faraday.control.panel.PickupCoilPanel;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.module.GeneratorModule;
import edu.colorado.phet.faraday.view.CoilGraphic;
import edu.colorado.phet.faraday.view.CompassGridGraphic;
import edu.colorado.phet.faraday.view.FieldMeterGraphic;
import edu.colorado.phet.faraday.view.TurbineGraphic;

/**
 * GeneratorControlPanel is the control panel for the "Generator" module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GeneratorControlPanel extends FaradayControlPanel {
    
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
     */
    public GeneratorControlPanel( GeneratorModule module,
            /* Generator stuff */
            Turbine turbineModel,
            Compass compassModel,
            TurbineGraphic turbineGraphic,
            CompassGridGraphic gridGraphic,
            FieldMeterGraphic fieldMeterGraphic,
            /* Pickup Coil stuff */
            PickupCoil pickupCoilModel,
            CoilGraphic coilGraphic,
            Lightbulb lightbulbModel,
            Voltmeter voltmeterModel ) {

            super( module );
            
            BarMagnetPanel barMagnetPanel = new BarMagnetPanel( turbineModel, compassModel, turbineGraphic.getBarMagnetGraphic(), gridGraphic, fieldMeterGraphic );
            barMagnetPanel.setFlipPolarityVisible( false );
            barMagnetPanel.setSeeInsideVisible( false );
            barMagnetPanel.setFieldMeterEnabled( false );
            addFullWidth( barMagnetPanel );
            
            PickupCoilPanel pickupCoilPanel = new PickupCoilPanel( pickupCoilModel, coilGraphic, lightbulbModel, voltmeterModel );
            addFullWidth( pickupCoilPanel );
    }
}