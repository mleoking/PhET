package edu.colorado.phet.batteryvoltage;

import java.awt.image.BufferedImage;

import edu.colorado.phet.batteryvoltage.common.electron.paint.BufferedImagePainter;
import edu.colorado.phet.batteryvoltage.man.voltListeners.VoltageListener;

public class ImageChanger implements VoltageListener {
    BufferedImagePainter bip;
    BufferedImage leftPlus;
    BufferedImage rightPlus;
    int numElectrons;

    public ImageChanger( BufferedImagePainter bip, BufferedImage leftPlus, BufferedImage rightPlus, int numElectrons ) {
        this.bip = bip;
        this.leftPlus = leftPlus;
        this.rightPlus = rightPlus;
        this.numElectrons = numElectrons;
    }

    public void voltageChanged( int value, Battery b ) {
        if ( value >= numElectrons / 2 ) {
            bip.setImage( leftPlus );
        }
        else {
            bip.setImage( rightPlus );
        }
    }
}
