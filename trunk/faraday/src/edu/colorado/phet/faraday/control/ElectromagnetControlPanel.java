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

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.faraday.control.panel.ElectromagnetPanel;
import edu.colorado.phet.faraday.model.ACSource;
import edu.colorado.phet.faraday.model.Battery;
import edu.colorado.phet.faraday.model.Compass;
import edu.colorado.phet.faraday.model.SourceCoil;
import edu.colorado.phet.faraday.module.ElectromagnetModule;
import edu.colorado.phet.faraday.view.CoilGraphic;
import edu.colorado.phet.faraday.view.CompassGridGraphic;
import edu.colorado.phet.faraday.view.FieldMeterGraphic;

/**
 * ElectromagnetControlPanel is the control panel for the "Electromagnet" module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ElectromagnetControlPanel extends ControlPanel {
    
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
    public ElectromagnetControlPanel( 
            ElectromagnetModule module,
            SourceCoil sourceCoilModel,
            Battery batteryModel,
            ACSource acSourceModel,
            Compass compassModel,
            CoilGraphic coilGraphic,
            CompassGridGraphic gridGraphic, 
            FieldMeterGraphic fieldMeterGraphic ) {

        super( module );
        
        ElectromagnetPanel electromagnetPanel = new ElectromagnetPanel(
                sourceCoilModel, batteryModel, acSourceModel, compassModel,
                coilGraphic, gridGraphic, fieldMeterGraphic );
        addFullWidth( electromagnetPanel );
    }
}