// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.controller;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;

/**
 * PhotoWindow
 * <p/>
 * A dialog that shows an annotated photo of a real laser.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoWindow extends PaintImmediateDialog {

    public PhotoWindow( JFrame owner, BufferedImage img ) {
        super( owner, false );
        setContentPane( new PhotoPanel( img ) );
        pack();
    }

    public PhotoWindow( JFrame owner ) {
        super( owner, false );
        BufferedImage img = null;
        try {
            img = ImageLoader.loadBufferedImage( "lasers/images/annotated-laser-2.jpg" );
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
