/* Copyright 2008, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.greenhouse.GreenhouseResources;


/**
 * This class defines a dialog window that shows a representation of the
 * electomagnetic spectrum.
 *
 */
public class SpectrumDialog extends PaintImmediateDialog {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    private static final Font CAPTION_FONT = new PhetFont(12);

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param owner
     */
    public SpectrumDialog( Frame parentFrame ) {

        super( parentFrame, true );

        setResizable( false );

        // picture
        BufferedImage image = GreenhouseResources.getImage( "spectrum-sketch-for-molecules-and-light.png" );
        JLabel picture = new JLabel( new ImageIcon( image ) );
        picture.setSize( image.getWidth(), image.getHeight() );

        // close button
        // TODO: i18n
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                SpectrumDialog.this.dispose();
            }
        });

        // panel
        JPanel panel = new JPanel();
        panel.setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) ); // top, left, bottom, right
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 10, 0, 10, 0 ) ); // top, left, bottom, right
        layout.setAnchor( GridBagConstraints.CENTER );
        panel.setLayout( layout );
        layout.addComponent( picture, 0, 0 );
        layout.addComponent( closeButton, 2, 0 );

        // Add to the dialog
        getContentPane().add( panel );
        pack();
    }

    @Override
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( visible ) {
            pack(); // pack after making visible because this dialog contains a JTextArea
        }
    }
}
