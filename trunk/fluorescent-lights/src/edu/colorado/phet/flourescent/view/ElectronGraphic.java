/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.flourescent.view;

import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.flourescent.FluorescentLightsConfig;
import edu.colorado.phet.flourescent.model.Electron;

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

    static{
        try {
            IMAGE = ImageLoader.loadBufferedImage( FluorescentLightsConfig.ELECTRON_IMAGE_FILE_NAME );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public ElectronGraphic( Component component, Electron electron ) {
        super( component );
        this.electron = electron;
        electron.addObserver( this );
        setImage( IMAGE );
        setRegistrationPoint( (int)(IMAGE.getWidth()/2),(int)(IMAGE.getWidth()/2));
    }

    public void update() {
        this.setLocation( (int)electron.getPosition().getX(), (int)electron.getPosition().getY() );
        setBoundsDirty();
        repaint();
    }
}
