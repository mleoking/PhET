/**
 * Class: GraphicsUtil
 * Package: edu.colorado.phet.lasers.view.util
 * Author: Another Guy
 * Date: Apr 8, 2003
 */
package edu.colorado.phet.graphics.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.util.Enumeration;

public class GraphicsUtil {

    /**
     * Add a component to a container with a GridBagLayout, creating
     * GridBagConstraints for it.
     * @param container
     * @param component
     * @param gridX
     * @param gridY
     * @param gridWidth
     * @param gridHeight
     * @param fill
     * @param anchor
     * @throws AWTException
     */
    public static void addGridBagComponent( Container container,
                                            Component component,
                                            int gridX, int gridY,
                                            int gridWidth, int gridHeight,
                                            int fill, int anchor )
    throws AWTException {
        LayoutManager lm = container.getLayout();
        if( !( lm instanceof GridBagLayout )) {
            throw new AWTException( "Invalid layout: " + lm );
        }
        else {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = gridX;
            gbc.gridy = gridY;
            gbc.gridwidth = gridWidth;
            gbc.gridheight = gridHeight;
            gbc.fill = fill;
            gbc.anchor = anchor;
            ((GridBagLayout)lm).setConstraints( component, gbc );
            container.add( component );
        }
    }

    /**
     * Places a frame on the screen, centered left and right and placed
     * vertically at the golden mean
     * @param frame
     */
    public static void centerFrameOnScreen( JFrame frame ) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        double screenWidth = d.getWidth();
        double screenHeight = d.getHeight();
        double frameLocX = Math.max(( screenWidth - frame.getWidth() ) / 2, 0 );
        double frameLocY = Math.max(( screenHeight - frame.getHeight() ) * 0.382, 0 );
        frame.setLocation( (int)frameLocX, (int)frameLocY );
    }

    /**
     * Sets the bounds for a dialog so it is centered over a frame
     * @param dialog
     */
    public static void centerDialogInParent( JDialog dialog ) {
        Rectangle frameBounds = dialog.getParent().getBounds();
        Rectangle dialogBounds = new Rectangle(
                (int)( frameBounds.getMinX() + frameBounds.getWidth() / 2 - dialog.getWidth() / 2 ),
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
    public static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage)image;
            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
         PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }


    // This method returns a buffered image with the contents of an image
    // This method returns true if the specified image has transparent pixels
    // Taken from The Java Developer's Almanac, 1.4
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see e665 Determining If an Image Has Transparent Pixels
        boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                image.getWidth(null), image.getHeight(null), transparency);
        } catch ( Exception e) {
            // The system does not have a screen
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }
}
