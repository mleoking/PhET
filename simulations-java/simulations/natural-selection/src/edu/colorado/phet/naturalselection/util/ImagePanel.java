/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.util;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;

/**
 * Creates a panel that contains the desired image
 *
 * @author Jonathan Olson
 */
public class ImagePanel extends JPanel {

    private BufferedImage image;

    /**
     * Constructor
     * @param imageName The filename of the image
     */
    public ImagePanel( String imageName ) {
        image = NaturalSelectionResources.getImage( imageName );
        setPreferredSize( new Dimension( image.getWidth(), image.getHeight() ) );
    }

    /**
     * Constructor
     * @param actualImage The image
     */
    public ImagePanel( BufferedImage actualImage ) {
        image = actualImage;
        setPreferredSize( new Dimension( image.getWidth(), image.getHeight() ) );
    }

    public void paintComponent( Graphics g ) {
        g.drawImage( image, 0, 0, null );

    }

}
