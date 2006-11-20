/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.photoelectric.PhotoelectricConfig;
import edu.colorado.phet.photoelectric.module.PhotoelectricModule;
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
    private PhotoelectricModule module;

    public ElectronGraphicManager( PhotoelectricModule module ) {
        this.module = module;
    }

    public void electronProduced( ElectronSource.ElectronProductionEvent event ) {
        final ApparatusPanel apparatusPanel = module.getApparatusPanel();
        Electron electron = event.getElectron();
        final ElectronGraphic eg = new ElectronGraphic( apparatusPanel, electron );
        apparatusPanel.addGraphic( eg, PhotoelectricConfig.ELECTRON_LAYER );

        electron.addChangeListener( new Electron.ChangeListenerAdapter() {
            public void leftSystem( Electron.ChangeEvent changeEvent ) {
                apparatusPanel.removeGraphic( eg );
            }
        } );
    }
}