/*  */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.theramp.model.RampObject;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * User: Sam Reid
 * Date: Feb 4, 2005
 * Time: 7:55:21 AM
 */

public class ObjectComboBox extends JComboBox {
    private AdvancedRampControlPanel controlPanel;
//    private static Font font = new Font( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 10 );

    public ObjectComboBox( final RampObject[] rampObjects, final AdvancedRampControlPanel controlPanel ) {
        super( toLabelArray( rampObjects, controlPanel ) );
        setRenderer( new ComboBoxRenderer() );
        this.controlPanel = controlPanel;
        //setBorder( createBorder( "Choose Object" ) );
        addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                int index = getSelectedIndex();
                controlPanel.setup( rampObjects[index] );
            }
        } );
//        setFont( font );
    }

    private Border createBorder( String s ) {
        return BorderFactory.createTitledBorder( s );
    }

    private static ImageIcon[] toLabelArray( RampObject[] imageElements, Component component ) {
        ImageIcon[] lab = new ImageIcon[imageElements.length];
        for( int i = 0; i < lab.length; i++ ) {
            try {
                BufferedImage image = ImageLoader.loadBufferedImage( imageElements[i].getLocation() );
                image = BufferedImageUtils.rescaleYMaintainAspectRatio( image, 35 );
                lab[i] = new ImageIcon( image );
                lab[i].setDescription( MessageFormat.format( TheRampStrings.getString( "readout.mass" ), new Object[]{imageElements[i].getName(), new Double( imageElements[i].getMass() )} ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        return lab;
    }

    public static class ComboBoxRenderer extends DefaultListCellRenderer {
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
            DefaultListCellRenderer component = (DefaultListCellRenderer)super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );

            ImageIcon icon = (ImageIcon)value;
            setText( icon.getDescription() );
            setIcon( icon );
//            setFont( font );
            return component;
        }
    }
}
