/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.theramp.model.RampObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Aug 9, 2005
 * Time: 12:58:15 AM
 * Copyright (c) Aug 9, 2005 by Sam Reid
 */

public class ObjectSelectionPanel extends JPanel {

    private Font selectedFont = new Font( "Lucida Sans", Font.BOLD, 16 );
    private Font normalFont = new Font( "Lucida Sans", Font.PLAIN, 12 );

    public ObjectSelectionPanel( final RampModule rampModule, final RampObject[] imageElements ) {
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        ButtonGroup bg = new ButtonGroup();
        final JRadioButton[] jRadioButtons = new JRadioButton[imageElements.length];
        for( int i = 0; i < imageElements.length; i++ ) {
            final RampObject imageElement = imageElements[i];
            BufferedImage image = null;
            try {
                image = ImageLoader.loadBufferedImage( imageElements[i].getLocation() );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            image = BufferedImageUtils.rescaleYMaintainAspectRatio( null, image, 35 );
            ImageIcon icon = new ImageIcon( image );
            JRadioButton jRadioButton = new JRadioButton( imageElement.getName() + " (" + imageElement.getMass() + " kg)", icon );
            jRadioButtons[i] = jRadioButton;
            if( i == 0 ) {
                jRadioButton.setSelected( true );
                jRadioButton.setFont( selectedFont );
            }
            else {
                jRadioButton.setFont( normalFont );
            }
            bg.add( jRadioButton );
            final int i1 = i;
            jRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {

                    rampModule.setObject( imageElement );
                    for( int j = 0; j < jRadioButtons.length; j++ ) {
                        JRadioButton radioButton = jRadioButtons[j];
                        if( j == i1 ) {
                            radioButton.setFont( selectedFont );
                        }
                        else {
                            radioButton.setFont( normalFont );
                        }
                    }
                }
            } );
            add( jRadioButton );

        }
        setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "Choose Object" ) );
    }
}
