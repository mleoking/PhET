/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.dialog;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.glaciers.GlaciersResources;
import edu.colorado.phet.glaciers.GlaciersStrings;


/**
 * GlacierPictureDialog shows an annotated picture of a real glacier.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlacierPictureDialog extends JDialog {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param owner
     */
    public GlacierPictureDialog( Frame owner ) {
        super( owner, false /* nonmodal */);
        
        setResizable( false );

        // picture
        BufferedImage image = GlaciersResources.getImage( "glacierPicture.jpg" );
        JLabel picture = new JLabel( new ImageIcon( image ) );
        picture.setSize( (int)image.getWidth(), (int)image.getHeight() );
        
        // text
        JTextArea text = new JTextArea( GlaciersStrings.TEXT_GLACIER_PICTURE );
        text.setColumns( 50 );
        text.setLineWrap( true );
        text.setWrapStyleWord( true );
        text.setEditable( false );
        text.setOpaque( false );

        // panel
        JPanel panel = new JPanel();
        panel.setBorder( BorderFactory.createEmptyBorder( 10, 15, 10, 15 ) ); // top, left, bottom, right
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 5, 0, 5, 0 ) ); // top, left, bottom, right
        layout.setAnchor( GridBagConstraints.CENTER );
        panel.setLayout( layout );
        layout.addComponent( picture, 0, 0 );
        layout.addComponent( text, 1, 0 );

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
