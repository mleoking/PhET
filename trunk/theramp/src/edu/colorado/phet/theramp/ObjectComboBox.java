/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.theramp.common.PhetLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Feb 4, 2005
 * Time: 7:55:21 AM
 * Copyright (c) Feb 4, 2005 by Sam Reid
 */

public class ObjectComboBox extends JComboBox {
    private RampControlPanel controlPanel;
    private static Font font = new Font( "Lucida Sans", Font.BOLD, 10 );

    public ObjectComboBox( final RampObject[] rampObjects, final RampControlPanel controlPanel ) {
        super( toLabelArray( rampObjects, controlPanel ) );
        setRenderer( new ComboBoxRenderer() );
        this.controlPanel = controlPanel;
        setBorder( PhetLookAndFeel.createSmoothBorder( "Choose Object" ) );
        addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
//                Object sel = getSelectedItem();
                int index = getSelectedIndex();
                controlPanel.setup( rampObjects[index] );
            }
        } );
        setFont( font );
    }

    private static ImageIcon[] toLabelArray( RampObject[] imageElements, Component component ) {
        ImageIcon[] lab = new ImageIcon[imageElements.length];
        for( int i = 0; i < lab.length; i++ ) {
            try {
                BufferedImage image = ImageLoader.loadBufferedImage( imageElements[i].getLocation() );
                image = BufferedImageUtils.rescaleYMaintainAspectRatio( component, image, 35 );
                ImageIcon icon = new ImageIcon( image );
//                icon.setDescription( imageElements[i].getName() );
//                icon.setDescription( imageElements[i].getName() );
//                icon.setDescription( imageElements[i].getName()+" ("+imageElements[i].getMass()+" kg)");
                icon.setDescription( imageElements[i].getName() + " (" + imageElements[i].getMass() + " kg)" );

                lab[i] = icon;
//                lab[i] = new JLabel( imageElements[i].getName(), icon, JLabel.CENTER );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        return lab;
    }

    public static class ComboBoxRenderer extends JLabel implements ListCellRenderer {
        public ComboBoxRenderer() {
            setOpaque( true );
            setHorizontalAlignment( CENTER );
            setVerticalAlignment( CENTER );

//            setFont( new Font( "Lucida Sans",Font.BOLD, 10) );
        }

        public Component getListCellRendererComponent( JList list,
                                                       Object value,
                                                       int index,
                                                       boolean isSelected,
                                                       boolean cellHasFocus ) {
            if( isSelected ) {
                setBackground( list.getSelectionBackground() );
                setForeground( list.getSelectionForeground() );
            }
            else {
                setBackground( list.getBackground() );
                setForeground( list.getForeground() );
            }

            ImageIcon icon = (ImageIcon)value;
            setText( icon.getDescription() );
            setIcon( icon );
            setFont( font );
            return this;
        }
    }
}
