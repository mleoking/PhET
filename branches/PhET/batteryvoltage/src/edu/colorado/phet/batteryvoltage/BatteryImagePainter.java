package edu.colorado.phet.batteryvoltage;

import electron.paint.BufferedImagePainter;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BatteryImagePainter extends BufferedImagePainter {
    boolean t;

    public BatteryImagePainter( BufferedImage bi ) {
        super( bi );
    }

    public void setPaintImage( boolean t ) {
        this.t = t;
    }

    public void paint( Graphics2D g ) {
        if( t ) {
            super.paint( g );
        }
    }
}
