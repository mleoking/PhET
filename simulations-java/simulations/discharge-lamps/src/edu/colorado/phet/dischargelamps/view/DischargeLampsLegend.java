package edu.colorado.phet.dischargelamps.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.quantum.model.Atom;
import edu.colorado.phet.common.quantum.model.QuantumModel;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.lasers.view.AbstractLegend;
import edu.colorado.phet.lasers.view.AnnotatedAtomGraphic;

/**
 * Created by: Sam
 * May 25, 2008 at 11:23:40 PM
 */
public class DischargeLampsLegend extends AbstractLegend {
    public DischargeLampsLegend() {
        addForKey( getAtomImage(), "Legend.atom" );
        addForKey( getElectronImage(), "Legend.electron" );
        add3PhotonLegendItems();
    }

    protected BufferedImage getElectronImage() {
        BufferedImage electronImage = null;
        try {
            electronImage = ImageLoader.loadBufferedImage( DischargeLampsConfig.ELECTRON_IMAGE_FILE_NAME );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return electronImage;
    }
}
