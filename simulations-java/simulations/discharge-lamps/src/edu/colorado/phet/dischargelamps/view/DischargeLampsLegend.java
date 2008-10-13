package edu.colorado.phet.dischargelamps.view;

import java.awt.image.BufferedImage;

import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.DischargeLampsResources;
import edu.colorado.phet.lasers.view.AbstractLegend;

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
        return DischargeLampsResources.getImage( DischargeLampsConfig.ELECTRON_IMAGE_FILE_NAME );
    }
}
