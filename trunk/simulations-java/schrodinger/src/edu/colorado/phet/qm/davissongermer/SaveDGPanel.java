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

    private void origVersion( DGPlotPanel dgPlotPanel, Frame parentFrame ) {
        Image copy = dgPlotPanel.getLayer().toImage();
        BufferedImage c2 = new BufferedImage( copy.getWidth( null ), copy.getHeight( null ), BufferedImage.TYPE_INT_RGB );//trim the south part.
        c2.createGraphics().drawImage( copy, new AffineTransform(), null );
        SavedGraph savedGraph = new SavedGraph( MessageFormat.format( QWIStrings.getResourceBundle().getString( "energy.vs.position.save.0" ), new Object[]{new Integer( saveCount )} ), c2, parentFrame );
        savedGraph.setVisible( true );
        saveCount++;
    }

}
