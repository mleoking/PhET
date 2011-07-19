// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.photonabsorption.view;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Class that defines a panel that has a radio button on the left and an
 * image on the right.  This is designed to be fairly general, and
 * therefore potentially reusable in other sims.
 *
 * @author John Blanco
 */
class SelectionPanelWithImage extends JPanel {

    private static final Font LABEL_FONT = new PhetFont( 14 );
    private static final GridBagConstraints selectorButtonConstraints = new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 );
    private static final GridBagConstraints imageConstraints = new GridBagConstraints( 1, 0, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 );

    private final JRadioButton radioButton = new JRadioButton();

    /**
     * Constructor.
     */
    public SelectionPanelWithImage( String moleculeNameAndSymbol, String toolTipText, BufferedImage image ) {
        setLayout( new GridBagLayout() );
        radioButton.setText( moleculeNameAndSymbol );
        radioButton.setFont( LABEL_FONT );
        radioButton.setToolTipText( toolTipText );
        add( radioButton, selectorButtonConstraints );
        ImageIcon imageIcon = new ImageIcon( image );
        JLabel icon = new JLabel( imageIcon );
        // Note: Had to put the icon on a panel of its own in order to get
        // it to be all the way to the right.  Not sure why this was
        // necessary, but we're talking about Java layout here, so it's
        // hard to say.
        JPanel iconPanel = new JPanel();
        iconPanel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
        iconPanel.add( icon );
        add( iconPanel, imageConstraints );

        // Add a listener to the image that essentially makes it so that
        // clicking on the image is the same as clicking on the button.
        iconPanel.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseReleased( MouseEvent e ) {
                radioButton.doClick();
            }
        } );
    }

    public JRadioButton getRadioButton() {
        return radioButton;
    }
}