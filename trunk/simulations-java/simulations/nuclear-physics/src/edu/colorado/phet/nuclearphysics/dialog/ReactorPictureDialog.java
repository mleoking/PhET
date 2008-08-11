/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.dialog;

import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;


/**
 * Shows a picture of the interior of a real nuclear reactor.
 *
 */
public class ReactorPictureDialog extends JDialog {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    private static final String IMAGE_FILE_NAME = "reactor_core.gif";
    private static final Font CAPTION_FONT = new PhetFont(12);
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param owner
     */
    public ReactorPictureDialog( Frame parentFrame ) {
        
        super( parentFrame, true );

        setResizable( false );

        // picture
        BufferedImage image = NuclearPhysicsResources.getImage( IMAGE_FILE_NAME );
        JLabel picture = new JLabel( new ImageIcon( image ) );
        picture.setSize( (int)image.getWidth(), (int)image.getHeight() );
        
        // text
        JTextArea text = new JTextArea( NuclearPhysicsStrings.REACTOR_PICTURE_CAPTION );
        text.setFont( CAPTION_FONT );
        text.setColumns( 30 );
        text.setLineWrap( true );
        text.setWrapStyleWord( true );
        text.setEditable( false );
        text.setOpaque( false );
        
        // close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                ReactorPictureDialog.this.dispose();
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
        layout.addComponent( text, 1, 0 );
        layout.addComponent( closeButton, 2, 0 );

        // Add to the dialog
        getContentPane().add( panel );
        pack();
    }
    
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( visible ) {
            pack(); // pack after making visible because this dialog contains a JTextArea
        }
    }
}
