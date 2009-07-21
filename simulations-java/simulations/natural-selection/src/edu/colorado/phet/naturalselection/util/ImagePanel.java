/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.util;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.NaturalSelectionApplication;

/**
 * Creates a panel that contains the desired image
 *
 * @author Jonathan Olson
 */
public class ImagePanel extends JPanel {

    private BufferedImage image;

    private boolean enabled = true;

    /**
     * Constructor
     *
     * @param imageName The filename of the image
     */
    public ImagePanel( String imageName ) {
        image = NaturalSelectionResources.getImage( imageName );
        setPreferredSize( new Dimension( image.getWidth(), image.getHeight() ) );
    }

    /**
     * Constructor
     *
     * @param actualImage The image
     */
    public ImagePanel( BufferedImage actualImage ) {
        image = actualImage;
        setPreferredSize( new Dimension( image.getWidth(), image.getHeight() ) );
    }

    public void setEnabled( boolean enabled ) {
        if ( enabled != this.enabled ) {
            this.enabled = enabled;

            repaint();
        }
    }

    public void paintComponent( Graphics g ) {
        Color controlColor = NaturalSelectionConstants.COLOR_CONTROL_PANEL;
        if ( NaturalSelectionApplication.isHighContrast() ) {
            controlColor = Color.BLACK;
        }
        if ( enabled ) {
            g.drawImage( image, 0, 0, controlColor, null );
        }
        else {
            g.drawImage( image, 0, 0, controlColor, null );

            Color alphaColor = new Color( controlColor.getRed(), controlColor.getGreen(), controlColor.getBlue(), 175 );
            g.setColor( alphaColor );
            g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
        }
    }

}
