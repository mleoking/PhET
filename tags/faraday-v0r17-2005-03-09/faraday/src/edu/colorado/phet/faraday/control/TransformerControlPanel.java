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

import edu.colorado.phet.faraday.control.panel.ElectromagnetPanel;
import edu.colorado.phet.faraday.control.panel.PickupCoilPanel;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.module.TransformerModule;
import edu.colorado.phet.faraday.view.*;

/**
 * TransformerControlPanel is the control panel for the "Transformer" module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TransformerControlPanel extends FaradayControlPanel {
    
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
     * @param sourceCoilModel
     * @param batteryModel
     * @param acSourceModel
     * @param compassModel
     * @param sourceCoilGraphic
     * @param gridGraphic
     * @param fieldMeterGraphic
     * @param pickupCoilModel
     * @param pickupCoilGraphic
     * @param lightbulbModel
     * @param voltmeterModel
     */
    public TransformerControlPanel( TransformerModule module,
            /* Electromagnet stuff */
            SourceCoil sourceCoilModel,
            Battery batteryModel,
            ACSource acSourceModel,
            Compass compassModel,
            CoilGraphic sourceCoilGraphic,
            CompassGridGraphic gridGraphic,
            FieldMeterGraphic fieldMeterGraphic,
            /* Pickup Coil stuff */
            PickupCoil pickupCoilModel,
            CoilGraphic pickupCoilGraphic,
            Lightbulb lightbulbModel,
            Voltmeter voltmeterModel ) {
        
        super( module );
        
        ElectromagnetPanel electromagnetPanel = new ElectromagnetPanel(
                sourceCoilModel, batteryModel, acSourceModel, compassModel,
                sourceCoilGraphic, gridGraphic, fieldMeterGraphic );
        electromagnetPanel.setFieldMeterEnabled( false );
        addFullWidth( electromagnetPanel );
        
        PickupCoilPanel pickupCoilPanel = new PickupCoilPanel( 
                pickupCoilModel, pickupCoilGraphic, lightbulbModel, voltmeterModel );
        addFullWidth( pickupCoilPanel );
    }
}