/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.util;

import javax.swing.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * GraphicsUtil
 *
 * @author ?
 * @version $Revision$
 */
public class GraphicsUtil {

    /**
     * Creates and returns a buffered image that is a rotated version of a specified
     * buffered image. The transform is done so that the image is not truncated.
     *
     * @param bImage
     * @param theta  Angle the image is to be rotated, in radians.
     * @return the rotated image.
     */
    public static BufferedImage getRotatedImage( BufferedImage bImage, double theta ) {
        // Determine the correct point of the image about which to rotate it. If we don't
        // do this correctly, the image will get clipped when it is rotated into certain
        // quadrants
        Point2D pr = new Point2D.Double();
        // Normalize theta to be between 0 and PI*2
        theta = ( ( theta % ( Math.PI * 2 ) ) + Math.PI * 2 ) % ( Math.PI * 2 );
        if( theta >= 0 && theta <= Math.PI / 2 ) {
            pr.setLocation( 0, bImage.getHeight() );
        }
        if( theta > Math.PI / 2 && theta <= Math.PI ) {
            pr.setLocation( bImage.getWidth(), bImage.getHeight() );
        }
        if( theta > Math.PI && theta <= Math.PI * 3 / 2 ) {
            pr.setLocation( bImage.getWidth(), bImage.getHeight() );
        }
        if( theta > Math.PI * 3 / 2 && theta <= Math.PI * 2 ) {
            pr.setLocation( bImage.getWidth(), bImage.getHeight() );
        }
        AffineTransform rtx = AffineTransform.getRotateInstance( theta, pr.getX(), pr.getY() );
        BufferedImageOp op = new AffineTransformOp( rtx, AffineTransformOp.TYPE_BILINEAR );
        BufferedImage result = op.filter( bImage, null );
        return result;
    }

    /**
     * Sets the alpha for a Graphics2D
     *
     * @param g2
     * @param alpha
     */
    public static void setAlpha( Graphics2D g2, double alpha ) {
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, (float)alpha ) );
    }

    /**
     * Creates an AffineTransform that scales about a specified point
     *
     * @param scale
     * @param x
     * @param y
     * @return the scaled transform.
     * @deprecated
     */
    public static AffineTransform scaleInPlaceTx( double scale, double x, double y ) {
        AffineTransform atx = new AffineTransform();
        return scaleInPlaceTx( atx, scale, x, y );
    }

    /**
     * Sets up an AffineTransform to scale about a specified point
     *
     * @param atx
     * @param scale
     * @param x
     * @param y
     * @return the scaled transform.
     */
    public static AffineTransform scaleInPlaceTx( AffineTransform atx,
                                                  double scale, double x, double y ) {
        atx.setToIdentity();
        atx.translate( x, y );
        atx.scale( scale, scale );
        atx.translate( -x, -y );
        return atx;
    }

    // Sets anti-aliasing on for a specified Graphics2D
    public static Graphics2D setAntiAliasingOn( Graphics2D g2 ) {
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        return g2;
    }

    // This method maximizes a frame; the iconified bit is not affected.
    public static void maximizeFrame( Frame frame ) {
        int state = frame.getExtendedState();
        // Set the maximized bits
        state |= Frame.MAXIMIZED_BOTH;
        // Maximize the frame
        frame.setExtendedState( state );
    }

    // This method iconifies a frame; the maximized bits are not affected.
    public static void iconifyFrame( Frame frame ) {
        int state = frame.getExtendedState();
        // Set the iconified bit
        state |= Frame.ICONIFIED;
        // Iconify the frame
        frame.setExtendedState( state );
    }

    // This method deiconifies a frame; the maximized bits are not affected.
    public static void deiconifyFrame( Frame frame ) {
        int state = frame.getExtendedState();
        // Clear the iconified bit
        state &= ~Frame.ICONIFIED;
        // Deiconify the frame
        frame.setExtendedState( state );
    }

    // This method minimizes a frame; the iconified bit is not affected
    public static void minimizeFrame( Frame frame ) {
        int state = frame.getExtendedState();
        // Clear the maximized bits
        state &= ~Frame.MAXIMIZED_BOTH;
        // Maximize the frame
        frame.setExtendedState( state );
    }

    public static void addMenuAt( JFrame frame, JMenu newMenu, int index ) {
        frame.setJMenuBar( addMenuAt( newMenu, frame.getJMenuBar(), index ) );
    }

    /**
     * @param newMenu
     * @param menuBar
     * @param index
     * @return The same JMenuBar, for cascading.
     *         todo See if the same thing can be done with Container.add( component, index )
     */
    public static JMenuBar addMenuAt( JMenu newMenu, JMenuBar menuBar, int index ) {

        ArrayList menuList = new ArrayList();
        for( int i = 0; i < menuBar.getMenuCount(); i++ ) {
            if( i == index ) {
                menuList.add( newMenu );
            }
            menuList.add( menuBar.getMenu( i ) );
        }
        menuBar.removeAll();
        //        menuBar = new JMenuBar();
        for( int i = 0; i < menuList.size(); i++ ) {
            JMenu menu = (JMenu)menuList.get( i );
            menuBar.add( menu );
        }
        return menuBar;
    }

    public static GridBagConstraints getGridBagConstraints( int gridX, int gridY,
                                                            int gridWidth, int gridHeight,
                                                            int fill, int anchor ) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.gridwidth = gridWidth;
        gbc.gridheight = gridHeight;
        gbc.fill = fill;
        gbc.anchor = anchor;

        return gbc;
    }

    /**
     * Add a component to a container with a GridBagLayout, creating
     * GridBagConstraints for it.
     *
     * @param container
     * @param component
     * @param gridX
     * @param gridY
     * @param gridWidth
     * @param gridHeight
     * @param fill
     * @param anchor
     * @throws java.awt.AWTException
     */
    public static void addGridBagComponent( Container container,
                                            Component component,
                                            int gridX, int gridY,
                                            int gridWidth, int gridHeight,
                                            int fill, int anchor )

            throws AWTException {
        addGridBagComponent( container, component, gridX, gridY,
                             gridWidth, gridHeight, fill, anchor, new Insets( 0, 0, 0, 0 ) );
    }

    public static void addGridBagComponent( Container container,
                                            Component component,
                                            int gridX, int gridY,
                                            int gridWidth, int gridHeight,
                                            int fill, int anchor, Insets insets )
            throws AWTException {
        LayoutManager lm = container.getLayout();
        if( !( lm instanceof GridBagLayout ) ) {
            throw new AWTException( "Invalid layout: " + lm );
        }
        else {
            GridBagConstraints gbc = getGridBagConstraints( gridX, gridY,
                                                            gridWidth, gridHeight, fill, anchor );
            gbc.insets = insets;
            ( (GridBagLayout)lm ).setConstraints( component, gbc );
            container.add( component );
        }
    }

    public static void centerWindowOnScreen( Window window ) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        window.setLocation( (int)( screenSize.getWidth() / 2 - window.getWidth() / 2 ),
                            (int)( screenSize.getHeight() / 2 - window.getHeight() / 2 ) );
    }

    /**
     * Sets the bounds for a dialog so it is centered over a frame
     *
     * @param dialog
     */
    public static void centerDialogInParent( JDialog dialog ) {
        Rectangle frameBounds = dialog.getParent().getBounds();
        Rectangle dialogBounds = new Rectangle( (int)( frameBounds.getMinX() + frameBounds.getWidth() / 2 - dialog.getWidth() / 2 ),
                                                (int)( frameBounds.getMinY() + frameBounds.getHeight() / 2 - dialog.getHeight() / 2 ),
                                                dialog.getWidth(), dialog.getHeight() );
        dialog.setBounds( dialogBounds );
    }

    // This method returns the selected radio button in a button group
    // Taken from The Java Developer's Almanac, 1.4
    public static JRadioButton getSelection( ButtonGroup group ) {
        for( Enumeration e = group.getElements(); e.hasMoreElements(); ) {
            JRadioButton b = (JRadioButton)e.nextElement();
            if( b.getModel() == group.getSelection() ) {
                return b;
            }
        }
        return null;
    }

    // This method returns true if the specified image has transparent pixels
    // Taken from The Java Developer's Almanac, 1.4
    public static boolean hasAlpha( Image image ) {
        // If buffered image, the color model is readily available
        if( image instanceof BufferedImage ) {
            BufferedImage bimage = (BufferedImage)image;
            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber( image, 0, 0, 1, 1, false );
        try {
            pg.grabPixels();
        }
        catch( InterruptedException e ) {
        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }

    /**
     * Gets the transparency of an image.
     * 
     * @param image the image
     * @return OPAQUE, BITMASK or TRANSLUCENT (see java.awt.Transparency)
     */
    public static int getTransparency( Image image ) {
        // If buffered image, the color model is readily available
        if( image instanceof BufferedImage ) {
            BufferedImage bimage = (BufferedImage)image;
            return bimage.getColorModel().getTransparency();
        }
        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber( image, 0, 0, 1, 1, false );
        try {
            pg.grabPixels();
        }
        catch( InterruptedException e ) {
        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        
        int transparency = Transparency.OPAQUE;
        if ( cm != null ) {
            transparency = cm.getTransparency();
        }
        return transparency;
    }
    
    // This method returns a buffered image with the contents of an image
    // Taken from The Java Developer's Almanac, 1.4
    public static BufferedImage toBufferedImage( Image image ) {
        if( image instanceof BufferedImage ) {
            return (BufferedImage)image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon( image ).getImage();

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Create the buffered image
            int transparency = getTransparency( image );
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage( image.getWidth( null ), image.getHeight( null ), transparency );
        }
        catch( HeadlessException e ) {
            // The system does not have a screen
        }

        if( bimage == null ) {
            // Create a buffered image using the default color model
            boolean hasAlpha = hasAlpha( image );
            int type = BufferedImage.TYPE_INT_RGB;
            if( hasAlpha ) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage( image.getWidth( null ), image.getHeight( null ), type );
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage( image, 0, 0, null );
        g.dispose();

        return bimage;
    }
}
