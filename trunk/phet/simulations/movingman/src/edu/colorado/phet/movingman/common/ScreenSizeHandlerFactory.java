/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.common;

import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


/**
 * User: Sam Reid
 * Date: Feb 1, 2005
 * Time: 8:13:34 PM
 * Copyright (c) Feb 1, 2005 by Sam Reid
 */

public class ScreenSizeHandlerFactory {

    public static abstract class ScreenSizeHandler {
        private int defaultSwingFontSize;
        private int closeImageWidth;
        private int zoomButtonHeight;

        protected ScreenSizeHandler( int defaultSwingFontSize, int closeImageWidth, int zoomButtonHeight ) {
            this.defaultSwingFontSize = defaultSwingFontSize;
            this.closeImageWidth = closeImageWidth;
            this.zoomButtonHeight = zoomButtonHeight;
        }

        public int getDefaultSwingFontSize() {
            return defaultSwingFontSize;
        }

//        public BufferedImage getCloseImage() {
//            try {
//                BufferedImage im = ImageLoader.loadBufferedImage( "images/x-25.gif" );
//                BufferedImage scaled = BufferedImageUtils.rescaleYMaintainAspectRatio( null, im, closeImageWidth );
//                return scaled;
//            }
//            catch( IOException e ) {
//                e.printStackTrace();
//                throw new RuntimeException( e );
//            }
//
//        }

        public BufferedImage getZoomInButtonImage() {
            try {
                BufferedImage im = ImageLoader.loadBufferedImage( "images/icons/glass-20-plus.gif" );
                BufferedImage scaled = BufferedImageUtils.rescaleYMaintainAspectRatio( null, im, zoomButtonHeight );
                return scaled;
            }
            catch( IOException e ) {
                e.printStackTrace();
                throw new RuntimeException( e );
            }
        }

        public BufferedImage getZoomOutButtonImage() {
            try {
                BufferedImage im = ImageLoader.loadBufferedImage( "images/icons/glass-20-minus.gif" );
                BufferedImage scaled = BufferedImageUtils.rescaleYMaintainAspectRatio( null, im, zoomButtonHeight );
                return scaled;
            }
            catch( IOException e ) {
                e.printStackTrace();
                throw new RuntimeException( e );
            }
        }
    }

    public static ScreenSizeHandler getScreenSizeHandler() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        if( width <= 800 ) {
            return new Handler800();
        }
        else if( width <= 1040 ) {
            return new Handler1040();
        }
        else {
            return new Handler1280();
        }
    }

    public static class Handler800 extends ScreenSizeHandler {

        public Handler800() {
            super( 8, 10, 10 );
        }
    }

    public static class Handler1040 extends ScreenSizeHandler {

        public Handler1040() {
            super( 12, 16, 16 );
        }
    }

    public static class Handler1280 extends ScreenSizeHandler {

        public Handler1280() {
            super( 16, 24, 16 );
        }
    }
}
