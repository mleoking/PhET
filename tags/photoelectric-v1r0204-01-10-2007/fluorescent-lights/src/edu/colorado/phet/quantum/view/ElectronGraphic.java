/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.quantum.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.quantum.model.Electron;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
    private static BufferedImage IMAGE;
    private Electron electron;

    static {
        try {
            IMAGE = ImageLoader.loadBufferedImage( DischargeLampsConfig.ELECTRON_IMAGE_FILE_NAME );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

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
        setRegistrationPoint( (int)( IMAGE.getWidth() / 2 ), (int)( IMAGE.getWidth() / 2 ) );
        update();
    }

    public void update() {
        this.setLocation( (int)electron.getPosition().getX(), (int)electron.getPosition().getY() );
        setBoundsDirty();
        repaint();
    }
}
