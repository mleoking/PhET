/**
 * Class: ReceivingElectronGraphic Package: edu.colorado.phet.emf.view Author:
 * Another Guy Date: Dec 4, 2003
 */

package edu.colorado.phet.radiowaves.view;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.radiowaves.EmfConfig;
import edu.colorado.phet.radiowaves.common_1200.graphics.ApparatusPanel;
import edu.colorado.phet.radiowaves.model.Electron;

public class ReceivingElectronGraphic extends ElectronGraphic {

    private static BufferedImage image;

    static {
        try {
            //            image = ImageLoader.loadBufferedImage( Config.bigElectronImg );
            image = ImageLoader.loadBufferedImage( EmfConfig.smallElectronImg );
        }
        catch ( IOException e ) {
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
