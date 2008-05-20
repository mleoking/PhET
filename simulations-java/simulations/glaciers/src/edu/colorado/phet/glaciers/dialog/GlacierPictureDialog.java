/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.dialog;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.glaciers.GlaciersResources;
import edu.colorado.phet.glaciers.GlaciersStrings;


/**
 * GlacierPictureDialog shows an annotated picture of a real glacier.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlacierPictureDialog extends JDialog {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    /* Space for the "Java Application Window" label that Web Start puts on the bottom of dialogs. */
    private static final int JAVA_APP_WINDOW_HEIGHT = 50;
    
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
        
        setTitle( GlaciersStrings.TITLE_GLACIER_PICTURE );
        setResizable( false );

        // picture
        BufferedImage image = GlaciersResources.getImage( "glacierPicture.png" );
        JLabel picture = new JLabel( new ImageIcon( image ) );
        
        // text
        JTextArea text = new JTextArea( GlaciersStrings.TEXT_GLACIER_PICTURE );
        int columns = getTextColumns( image.getWidth(), text );
        text.setColumns( columns );
        text.setLineWrap( true );
        text.setWrapStyleWord( true );
        text.setEditable( false );
        text.setOpaque( false );
        JPanel textPanel = new JPanel();
        textPanel.setBorder( BorderFactory.createEmptyBorder( 15, 0, 0, 0 ) ); // top, left, bottom, right
        textPanel.add( text );
        
        // panel
        JPanel panel = new JPanel( new BorderLayout() );
        panel.setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) ); // top, left, bottom, right
        panel.add( picture, BorderLayout.CENTER );
        panel.add( textPanel, BorderLayout.SOUTH  );
        Dimension preferredSize = panel.getPreferredSize();

        // Add to the dialog
        getContentPane().add( panel );
        setSize( (int)preferredSize.getWidth(), (int)( preferredSize.getHeight() + JAVA_APP_WINDOW_HEIGHT ) );
    }
    
    /*
     * Uses font metrics to determine how to map pixels to text columns. 
     */
    private static int getTextColumns( int pixelWidth, JComponent component ) {
        FontMetrics fontMetrics = component.getFontMetrics( component.getFont() );
        int charWidth = fontMetrics.charWidth( 'W' );
        return pixelWidth / charWidth;
    }
}
