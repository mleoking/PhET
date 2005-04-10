/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.flourescent;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.flourescent.model.FluorescentLightModel;
import edu.colorado.phet.flourescent.model.ElectronSource;
import edu.colorado.phet.flourescent.model.Cathode;
import edu.colorado.phet.flourescent.view.ElectronGraphic;

import javax.swing.*;
import java.awt.*;

/**
 * ModuleA
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ModuleA extends Module implements ElectronSource.Listener {

    protected ModuleA( AbstractClock clock ) {
        super( "Module A", clock );

        ApparatusPanel apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( Color.white );
        setApparatusPanel( apparatusPanel );

        FluorescentLightModel model = new FluorescentLightModel();
        setModel( model );

        setControlPanel( new ControlPanel( this ) );
        PhetGraphic circuitGraphic = new PhetImageGraphic( getApparatusPanel(), "images/battery-w-wires.png");
        circuitGraphic.setLocation( FluorescentLightsConfig.CIRCUIT_ULC);
        apparatusPanel.addGraphic( circuitGraphic );

        // Add the cathode to the model
        Cathode cathode = new Cathode( model );
        model.addModelElement( cathode );
        cathode.addListener( this );

        cathode.setElectronsPerSecond( 1 );
        cathode.setPosition( 200, 200 );
    }

    //----------------------------------------------------------------
    // Managing graphics bound to the model
    //----------------------------------------------------------------
    public void electronProduced( ElectronSource.ElectronSourceEvent event ) {
        ElectronGraphic graphic = new ElectronGraphic( getApparatusPanel(), event.getElectron() );
        getApparatusPanel().addGraphic( graphic, FluorescentLightsConfig.ELECTRON_LAYER );
    }
}
