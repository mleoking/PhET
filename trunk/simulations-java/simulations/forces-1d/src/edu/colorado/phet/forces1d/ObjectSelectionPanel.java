package edu.colorado.phet.forces1d;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.forces1d.model.Force1dObject;
import edu.colorado.phet.forces1d.phetcommon.view.util.ImageLoader;

/**
 * User: Sam Reid
 * Date: Feb 4, 2005
 * Time: 8:12:57 AM
 */

public class ObjectSelectionPanel extends VerticalLayoutPanel {
    private Color SELECTION_COLOR = Color.yellow;

    public ObjectSelectionPanel( final Force1dObject[] imageElements, final Forces1DControlPanel simpleControlPanel ) {
        ButtonGroup buttonGroup = new ButtonGroup();
        final JRadioButton[] jRadioButtons = new JRadioButton[imageElements.length];
        for ( int i = 0; i < imageElements.length; i++ ) {
            final Force1dObject imageElement = imageElements[i];
            BufferedImage image = null;
            try {
                image = ImageLoader.loadBufferedImage( imageElements[i].getLocation() );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }

            image = BufferedImageUtils.multiScaleToHeight( image, 35 );
            ImageIcon icon = new ImageIcon( image );
            JRadioButton jRadioButton = new JRadioButton( imageElement.getName() + " (" + imageElement.getMass() + " kg)", icon );
            jRadioButtons[i] = jRadioButton;
            if ( i == 0 ) {
                jRadioButton.setSelected( true );
                jRadioButton.setBackground( SELECTION_COLOR );
            }
            else {
                jRadioButton.setBackground( Forces1DApplication.FORCES_1D_BACKGROUND_COLOR );
            }
            buttonGroup.add( jRadioButton );
            final int i1 = i;
            jRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    simpleControlPanel.setup( imageElement );
                    for ( int j = 0; j < jRadioButtons.length; j++ ) {
                        JRadioButton radioButton = jRadioButtons[j];
                        if ( j == i1 ) {
                            radioButton.setBackground( SELECTION_COLOR );
                        }
                        else {
                            radioButton.setBackground( Forces1DApplication.FORCES_1D_BACKGROUND_COLOR );
                        }
                    }
                }
            } );
            add( jRadioButton );

        }
        setBorder( Force1DUtil.createSmoothBorder( Force1DResources.get( "ObjectSelectionPanel.chooseObject" ) ) );
    }
}
