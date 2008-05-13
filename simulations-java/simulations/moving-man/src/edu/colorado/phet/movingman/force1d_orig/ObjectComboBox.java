/*  */
package edu.colorado.phet.movingman.force1d_orig;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.movingman.motion.ramps.Force1DObjectConfig;

/**
 * User: Sam Reid
 * Date: Feb 4, 2005
 * Time: 7:55:21 AM
 */

public class ObjectComboBox extends JComboBox {
    private static Font font = new PhetFont( Font.BOLD, 10 );

    public ObjectComboBox( final Force1DApplication module, final Force1DObjectConfig[] imageElements ) {
        super( toLabelArray( imageElements ) );
        setRenderer( new ComboBoxRenderer() );
        if ( Toolkit.getDefaultToolkit().getScreenSize().width >= 1280 ) {
            setBorder( Force1DUtil.createSmoothBorder( SimStrings.get( "ObjectComboBox.chooseObject" ) ) );
        }
        addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
//                Object sel = getSelectedItem();
                int index = getSelectedIndex();
//                controlPanel.setup( imageElements[index] );
                module.setObject( imageElements[index] );
            }
        } );
        setFont( font );
    }

    private static ImageIcon[] toLabelArray( Force1DObjectConfig[] imageElements ) {
        ImageIcon[] lab = new ImageIcon[imageElements.length];
        for ( int i = 0; i < lab.length; i++ ) {
            try {
                BufferedImage image = ImageLoader.loadBufferedImage( imageElements[i].getImageURL() );
                image = BufferedImageUtils.rescaleYMaintainAspectRatio( image, 35 );
                ImageIcon icon = new ImageIcon( image );
                icon.setDescription( imageElements[i].getName() + " (" + imageElements[i].getMass() + " " + SimStrings.get( "ObjectComboBox.kg" ) + ")" );

                lab[i] = icon;
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
        }

        public Component getListCellRendererComponent( JList list,
                                                       Object value,
                                                       int index,
                                                       boolean isSelected,
                                                       boolean cellHasFocus ) {
            if ( isSelected ) {
                setBackground( list.getSelectionBackground() );
                setForeground( list.getSelectionForeground() );
            }
            else {
                setBackground( list.getBackground() );
                setForeground( list.getForeground() );
            }

            ImageIcon icon = (ImageIcon) value;
            setText( icon.getDescription() );
            setIcon( icon );
            setFont( font );
            return this;
        }
    }
}
