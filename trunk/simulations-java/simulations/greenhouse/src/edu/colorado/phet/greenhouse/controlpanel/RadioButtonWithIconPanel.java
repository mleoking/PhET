/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.greenhouse.controlpanel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * This class combines an icon and a radio button on to a panel in the way
 * that is needed on the control panels for this simulation.  The initial
 * motivation for its creation was the need to put different molecules on
 * the control panel, but it may have other applications.
 *
 * @author John Blanco
 */
class RadioButtonWithIconPanel extends HorizontalLayoutPanel {

    // Font to use for labels.
    private static final Font LABEL_FONT = new PhetFont(14);

    // Fixed height for the panels.
    private static final int PREFERRED_HEIGHT = 42;  // In pixels.

    private final JRadioButton button;

    /**
     * Constructor with default scaling factor.
     */
    public RadioButtonWithIconPanel(String buttonText, String toolTipText, BufferedImage image ){
        this( buttonText, toolTipText, image, 1);
    }

    /**
     * Constructor.
     *
     * @param buttonText
     * @param toolTipText
     * @param imageName
     */
    public RadioButtonWithIconPanel(String buttonText, String toolTipText, BufferedImage image,
            double imageScalingFactor){

        // Create and add the button.
        button = new JRadioButton(buttonText);
        button.setFont(LABEL_FONT);
        button.setToolTipText( toolTipText );
        add(button);

        setPreferredSize( new Dimension(getPreferredSize().width, PREFERRED_HEIGHT ) );

        // Create and add the image.
        BufferedImage scaledImage = BufferedImageUtils.multiScale( image, imageScalingFactor );
        ImageIcon imageIcon = new ImageIcon( scaledImage );
        JLabel iconImageLabel = new JLabel( imageIcon );
        iconImageLabel.setToolTipText( toolTipText );
        add( iconImageLabel );

        // Add a listener to the image that essentially makes it so that
        // clicking on the image is the same as clicking on the button.
        iconImageLabel.addMouseListener( new MouseAdapter(){
            @Override
            public void mouseReleased(MouseEvent e) {
                button.doClick();
            }
        });
    }

    public JRadioButton getButton(){
        return button;
    }
}