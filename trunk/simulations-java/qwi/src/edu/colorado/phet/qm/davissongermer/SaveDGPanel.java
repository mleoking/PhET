/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;

/**
 * User: Sam Reid
 * Date: Feb 18, 2006
 * Time: 12:23:42 PM
 * Copyright (c) Feb 18, 2006 by Sam Reid
 */

public class SaveDGPanel {
    private int saveCount = 1;

    public void savePanel( DGPlotPanel dgPlotPanel, Frame parentFrame, String text ) {
        dgPlotPanel.saveDataAsLayer( text );
//        Image copy = super.getLayer().toImage( image.getImage().getWidth( null ), image.getImage().getHeight( null ), Color.white );
//        origVersion( dgPlotPanel, parentFrame );
    }

}
