/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.quantum.view;

import java.awt.Component;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.DischargeLampsResources;
import edu.colorado.phet.dischargelamps.quantum.model.Electron;

/**
 * ElectronGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ElectronGraphic extends PhetImageGraphic implements SimpleObserver {

    //----------------------------------------------------------------
    // Class attributes and methods
    //----------------------------------------------------------------
    private static final BufferedImage IMAGE = DischargeLampsResources.getImage( DischargeLampsConfig.ELECTRON_IMAGE_FILE_NAME );
    private Electron electron;

    /**
     * @param component
     * @param electron
     */
    public ElectronGraphic( Component component, Electron electron ) {
        super( component );
        this.setIgnoreMouse( true );
        this.electron = electron;
        electron.addObserver( this );
        setImage( IMAGE );
        setRegistrationPoint( (int) ( IMAGE.getWidth() / 2 ), (int) ( IMAGE.getWidth() / 2 ) );
        update();
    }

    public void update() {
        this.setLocation( (int) electron.getPosition().getX(), (int) electron.getPosition().getY() );
        setBoundsDirty();
        repaint();
    }
}
