package edu.colorado.phet.naturalselection.util;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.naturalselection.NaturalSelectionResources;

public class ImagePanel extends JPanel {

    private BufferedImage image;

    public ImagePanel( String imageName ) {
        image = NaturalSelectionResources.getImage( imageName );
    }

    public void paintComponent( Graphics g ) {
        g.drawImage( image, 0, 0, null );

    }

}
