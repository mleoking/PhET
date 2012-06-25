// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsResources;


/**
 * Shows a picture of real cells containing fluorescent protein.
 */
public class FluorescentCellsPictureDialog extends PaintImmediateDialog {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final Font CAPTION_FONT = new PhetFont( 14 );

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public FluorescentCellsPictureDialog( Frame parentFrame ) {

        super( parentFrame, true );

        setResizable( false );

        // Picture
        BufferedImage image = GeneExpressionBasicsResources.Images.ECOLI;
        JLabel picture = new JLabel( new ImageIcon( image ) );
        picture.setSize( image.getWidth(), image.getHeight() );

        // Caption
        JTextPane textPane = new JTextPane();
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment( center, StyleConstants.ALIGN_CENTER );
        doc.setParagraphAttributes( 0, doc.getLength(), center, false );
        textPane.setOpaque( false );
        textPane.setFont( CAPTION_FONT );
        textPane.setText( GeneExpressionBasicsResources.Strings.IMAGE_CAPTION + "\n\n" + GeneExpressionBasicsResources.Strings.IMAGE_ATTRIBUTION + " Dennis Kunkel Microscopy, Inc." );

        // Close button
        JButton closeButton = new JButton( "Close" );
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                FluorescentCellsPictureDialog.this.dispose();
            }
        } );

        // Panel
        JPanel panel = new JPanel( new GridBagLayout() );
        panel.setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) ); // top, left, bottom, right
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.top = 5;
        constraints.insets.bottom = 5;
        panel.add( picture, constraints );
        constraints.gridy++;
        constraints.insets.top = 15;
        constraints.insets.bottom = 15;
        panel.add( textPane, constraints );
        constraints.gridy++;
        panel.add( closeButton, constraints );

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
