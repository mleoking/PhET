/**
 * Class: ReceivingElectronGraphic
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: Dec 4, 2003
 */
package edu.colorado.phet.emf.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.emf.model.Electron;
import edu.colorado.phet.emf.Config;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class ReceivingElectronGraphic extends ElectronGraphic {

    private static BufferedImage image;
    static {
        try {
            image = ImageLoader.loadBufferedImage( Config.bigElectronImg );
//            image = ImageLoader.loadBufferedImage( Config.smallElectronImg );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public ReceivingElectronGraphic( ApparatusPanel apparatusPanel, Electron electron ) {
        super( apparatusPanel, image, electron );
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return false;
    }
}
