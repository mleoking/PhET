/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.resources;

import edu.colorado.phet.simlauncher.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * ImageResource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ThumbnailResource extends SimResource {
    private ImageIcon imageIcon;

    public ThumbnailResource( URL url, File localRoot ) {
        super( url, localRoot );

        // If we're online and the local copy isn't current, go get 
        try {
            if( isUpdatable() ) {
//            if( PhetSiteConnection.instance().isConnected() && !isCurrent() ) {
                BufferedImage bImg = ImageLoader.loadBufferedImage( url );
//                double maxThumbnailHeight = 100;
//                if( bImg.getHeight() > 100 ) {
//                    AffineTransform sTx = AffineTransform.getScaleInstance( maxThumbnailHeight / bImg.getHeight( ),
//                                                                            maxThumbnailHeight / bImg.getHeight( ));
//                    AffineTransformOp atxOp = new AffineTransformOp( sTx, AffineTransformOp.TYPE_BICUBIC );
//                    bImg = atxOp.filter( bImg, null);
//                }
                imageIcon = new ImageIcon( bImg );
            }
            else if( getLocalFile() != null && getLocalFile().exists() ) {
                imageIcon = new ImageIcon( getLocalFile().getAbsolutePath()  );
            }
            else {
                imageIcon = new ImageIcon( new NoImageImage() );
            }
        }
        catch( IOException e ) {
            imageIcon = new ImageIcon( new NoImageImage() );
        }
    }

    public ImageIcon getImageIcon() {
        if( imageIcon == null ) {
            return new ImageIcon( new NoImageImage() );
        }
        else {
            return imageIcon;
        }
    }

    private class NoImageImage extends BufferedImage {

        public NoImageImage() {
            super( 100, 30, BufferedImage.TYPE_INT_RGB );
            Graphics2D g2 = this.createGraphics();
            g2.setColor( Color.white );
            g2.fillRect( 0, 0, getWidth(), getHeight() );
            g2.setColor( Color.black );
            g2.drawString( "Thumbnail not available", 10, 10 );
            g2.dispose();
        }
    }
}
