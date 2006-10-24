/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import edu.colorado.phet.common.view.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Feb 4, 2005
 * Time: 7:55:21 AM
 * Copyright (c) Feb 4, 2005 by Sam Reid
 */

public class ImageComboBox extends JComboBox {

    public static class Item {
        String label;
        String imageLocation;

        public Item( String label, String imageLocation ) {
            this.label = label;
            this.imageLocation = imageLocation;
        }

        public String getLabel() {
            return label;
        }

        public String getImageLocation() {
            return imageLocation;
        }
    }

    public ImageComboBox( final Item[] rampObjects ) {
        super( toLabelArray( rampObjects ) );
        setRenderer( new ComboBoxRenderer() );
        ImageIcon[] ii = toLabelArray( rampObjects );
        int maxWidth = 0;
        for( int i = 0; i < ii.length; i++ ) {
            ImageIcon icon = ii[i];
            JLabel label = new JLabel( icon.getDescription(), icon, JLabel.LEFT );
            if( label.getPreferredSize().width > maxWidth ) {
                maxWidth = label.getPreferredSize().width;
            }
        }
//        setPreferredSize( new Dimension( maxWidth, getPreferredSize().height ) );
    }

    public static ImageIcon[] toLabelArray( Item[] imageElements ) {
        ImageIcon[] lab = new ImageIcon[imageElements.length];
        for( int i = 0; i < lab.length; i++ ) {
            BufferedImage image = null;
            try {
                image = ImageLoader.loadBufferedImage( imageElements[i].getImageLocation() );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            if( image == null ) {
                image = new BufferedImage( 5, 5, BufferedImage.TYPE_INT_RGB );
            }
            lab[i] = new ImageIcon( image );
            lab[i].setDescription( imageElements[i].getLabel() );
        }
        return lab;
    }

    public static class ComboBoxRenderer extends JLabel implements ListCellRenderer {
        public ComboBoxRenderer() {
            setOpaque( true );
            setHorizontalAlignment( LEFT );
            setVerticalAlignment( CENTER );
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
            if( icon != null ) {
                setText( icon.getDescription() );
            }
            setIcon( icon );
            return this;
        }
    }
}
