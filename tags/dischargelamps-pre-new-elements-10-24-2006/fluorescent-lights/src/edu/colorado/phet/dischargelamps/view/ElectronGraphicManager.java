/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: 
 * Branch : $Name:  
 * Modified by : $Author: 
 * Revision : $Revision: 
 * Date modified : $Date: 
 */

package edu.colorado.phet.dischargelamps.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.quantum.model.Electron;
import edu.colorado.phet.quantum.model.ElectronSource;
import edu.colorado.phet.quantum.view.ElectronGraphic;

/**
 * ElectronGraphicManager
 * <p/>
 * Creates, manages and removes ElectronGraphics
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ElectronGraphicManager implements ElectronSource.ElectronProductionListener {
    private ApparatusPanel apparatusPanel;

    public ElectronGraphicManager( ApparatusPanel apparatusPanel ) {
        this.apparatusPanel = apparatusPanel;
    }

    public void electronProduced( ElectronSource.ElectronProductionEvent event ) {
        Electron electron = event.getElectron();

        // Create a graphic for the electron
        final ElectronGraphic graphic = new ElectronGraphic( apparatusPanel, electron );
        apparatusPanel.addGraphic( graphic, DischargeLampsConfig.ELECTRON_LAYER );

        // Create a listener that will remove the graphic when the electron leaves the system
        electron.addChangeListener( new Electron.ChangeListenerAdapter() {
            public void leftSystem( Electron.ChangeEvent changeEvent ) {
                apparatusPanel.removeGraphic( graphic );
            }
        } );
    }
}
