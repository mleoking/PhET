/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.model.resources;

//import edu.colorado.phet.simlauncher.util.ImageLoader;

import edu.colorado.phet.common.view.util.ImageLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * ImageResource
 * <p/>
 * The thumbnail for a Simulation
 * <p/>
 * Thumbnail resources get downloaded with the catalog. They should be localy available whether the
 * simulation is installed or not.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ThumbnailResource extends SimResource {
    private ImageIcon imageIcon;

    public ThumbnailResource( URL url, File localRoot ) {
        super( url, localRoot );
        createImageIcon();
    }

    /**
     * Creates the ImageIcon for the resource
     */
    private void createImageIcon() {
//        if( true ) {               imageIcon = new ImageIcon( new NoImageImage() ); return; }

        // If we're online and the local copy isn't current, go get
        try {
            if( isUpdatable() ) {
                BufferedImage bImg = ImageLoader.loadBufferedImage( url );
                //Make sure it isn't bigger than a maximum height
//                double maxThumbnailHeight = 50;
//                if( bImg.getHeight() > 100 ) {
//                    AffineTransform sTx = AffineTransform.getScaleInstance( maxThumbnailHeight / bImg.getHeight(),
//                                                                            maxThumbnailHeight / bImg.getHeight() );
//                    AffineTransformOp atxOp = new AffineTransformOp( sTx, AffineTransformOp.TYPE_BILINEAR );
//                    bImg = atxOp.filter( bImg, null );
//                }

                imageIcon = new ImageIcon( bImg );
            }
            else if( getLocalFile() != null && getLocalFile().exists() ) {

                // Try to load the image from a file. This is to see if it's corrupted
                Image image = null;
                try {
                    // Read from a file
                    File file = new File( getLocalFile().getAbsolutePath() );
                    image = ImageIO.read( file );
                }
                catch( IOException e ) {
                    imageIcon = new ImageIcon( new NoImageImage() );
                    return;
                }
                imageIcon = new ImageIcon( getLocalFile().getAbsolutePath() );
            }
            else {
//                imageIcon = null;
                imageIcon = new ImageIcon( new NoImageImage() );
            }
        }
        catch( IOException e ) {
            imageIcon = new ImageIcon( new NoImageImage() );
        }
    }

    public void download() throws SimResourceException {
        super.download();
        createImageIcon();
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    /**
     * The image to be used for the thumbnail if no image is available from the catalog
     */
    public class NoImageImage extends BufferedImage {

        public NoImageImage() {
            super( 100, 30, BufferedImage.TYPE_INT_RGB );
            Graphics2D g2 = this.createGraphics();
            g2.setColor( Color.white );
            g2.fillRect( 0, 0, getWidth(), getHeight() );
            g2.setColor( Color.black );
            g2.drawString( "Thumbnail not", 10, 10 );
            g2.drawString( "available", 10, 20 );
            g2.dispose();
        }
    }
}
