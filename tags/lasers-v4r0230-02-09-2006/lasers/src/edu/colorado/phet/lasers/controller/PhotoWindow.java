/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.view.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * PhotoWindow
 * <p>
 * A dialog that shows an annotated photo of a real laser.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoWindow extends JDialog {

    public PhotoWindow( JFrame owner ) {
        super( owner, false );
        BufferedImage img = null;
        try {
            img = ImageLoader.loadBufferedImage( "images/annotated-laser-2.png" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        setContentPane( new PhotoPanel( img ) );
        pack();
    }

    private class PhotoPanel extends JPanel {
        private BufferedImage img;

        PhotoPanel( BufferedImage img ) {
            super( null );
            this.img = img;
            setPreferredSize( new Dimension( img.getWidth(), img.getHeight() ) );
        }

        protected void paintComponent( Graphics g ) {
            super.paintComponent( g );
            g.drawImage( img, 0, 0, null );
        }
    }
}
