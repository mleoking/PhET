/*  */
package edu.colorado.phet.forces1d;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common_force1d.view.util.BufferedImageUtils;
import edu.colorado.phet.common_force1d.view.util.ImageLoader;
import edu.colorado.phet.forces1d.model.Force1dObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Feb 4, 2005
 * Time: 8:12:57 AM
 */

public class ObjectSelectionPanel extends JPanel {
    private Font selectedFont = new Font( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 13 );
    private Font normalFont = new Font( PhetDefaultFont.LUCIDA_SANS, Font.PLAIN, 13 );

    public ObjectSelectionPanel( final Force1dObject[] imageElements, final SimpleControlPanel simpleControlPanel ) {
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        ButtonGroup bg = new ButtonGroup();
        final JRadioButton[] jRadioButtons = new JRadioButton[imageElements.length];
        for( int i = 0; i < imageElements.length; i++ ) {
            final Force1dObject imageElement = imageElements[i];
            BufferedImage image = null;
            try {
                image = ImageLoader.loadBufferedImage( imageElements[i].getLocation() );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            image = BufferedImageUtils.rescaleYMaintainAspectRatio( simpleControlPanel, image, 35 );
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
                    simpleControlPanel.setup( imageElement );
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
        setBorder( Force1DUtil.createSmoothBorder( SimStrings.get( "ObjectSelectionPanel.chooseObject" ) ) );
    }
}
