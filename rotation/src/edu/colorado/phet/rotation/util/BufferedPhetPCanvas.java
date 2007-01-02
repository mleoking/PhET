package edu.colorado.phet.rotation.util;

import edu.colorado.phet.piccolo.PhetPCanvas;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jan 1, 2007
 * Time: 11:57:30 PM
 * Copyright (c) Jan 1, 2007 by Sam Reid
 */

public class BufferedPhetPCanvas extends PhetPCanvas {
    private BufferedImage bufferedImage;

    public void paintComponent( Graphics g ) {
        if( bufferedImage == null || bufferedImage.getWidth() != getWidth() || bufferedImage.getHeight() != getHeight() ) {
            bufferedImage = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB );
        }

        Graphics2D bufferedGraphics = bufferedImage.createGraphics();
        super.paintComponent( bufferedGraphics );
        Graphics2D g2 = (Graphics2D)g;
        g2.drawRenderedImage( bufferedImage, new AffineTransform() );

    }
}
