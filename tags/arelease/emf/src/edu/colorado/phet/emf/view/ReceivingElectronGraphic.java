/**
 * Class: ReceivingElectronGraphic
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: Dec 4, 2003
 */
package edu.colorado.phet.emf.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.emf.model.Electron;

import java.awt.*;
import java.awt.event.MouseEvent;

public class ReceivingElectronGraphic extends ElectronGraphic {
    public ReceivingElectronGraphic( ApparatusPanel apparatusPanel, Electron electron ) {
        super( apparatusPanel, electron );
    }

    public ReceivingElectronGraphic( ApparatusPanel apparatusPanel, Electron electron, Image image ) {
        super( apparatusPanel, electron, image );
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return false;
    }
}
