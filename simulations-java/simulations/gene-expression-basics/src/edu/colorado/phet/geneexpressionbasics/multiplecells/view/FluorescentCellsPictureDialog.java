// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.Dimension;
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
    private static final Font ATTRIBUTION_FONT = new PhetFont( 12 );

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
        final JLabel picture = new JLabel( new ImageIcon( image ) );
        picture.setSize( image.getWidth(), image.getHeight() );

        // Caption
        JTextPane captionTextPane = new JTextPane() {{
            SimpleAttributeSet left = new SimpleAttributeSet();
            StyleConstants.setAlignment( left, StyleConstants.ALIGN_LEFT );
            getStyledDocument().setParagraphAttributes( 0, 0, left, false );
            setOpaque( false );
            setEditable( false );
            setPreferredSize( new Dimension( picture.getWidth(), getFontMetrics( CAPTION_FONT ).getHeight() * 3 + getFontMetrics( CAPTION_FONT ).getDescent() ) );
            setFont( CAPTION_FONT );
            setText( GeneExpressionBasicsResources.Strings.IMAGE_CAPTION );
        }};

        // Attribution
        JTextPane attributionTextPane = new JTextPane() {{
            SimpleAttributeSet center = new SimpleAttributeSet();
            StyleConstants.setAlignment( center, StyleConstants.ALIGN_CENTER );
            getStyledDocument().setParagraphAttributes( 0, 0, center, false );
            setOpaque( false );
            setEditable( false );
            setFont( ATTRIBUTION_FONT );
            // REVIEW: do they (or the translators) want this to be translatable? We could have {0} fill in with "Dennis Kunkel Microscopy, Inc.".
            setText( "Image Copyright Dennis Kunkel Microscopy, Inc." );
        }};

        // Close button
        JButton closeButton = new JButton( GeneExpressionBasicsResources.Strings.CLOSE );
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                FluorescentCellsPictureDialog.this.dispose();
            }
        } );

        // Panel
        JPanel panel = new JPanel( new GridBagLayout() );
        panel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) ); // top, left, bottom, right
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.top = 5;
        constraints.insets.bottom = 5;
        panel.add( picture, constraints );
        constraints.gridy++;
        panel.add( captionTextPane, constraints );
        constraints.gridy++;
        panel.add( attributionTextPane, constraints );
        constraints.gridy++;
        constraints.insets.top = 15;
        constraints.insets.bottom = 15;
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
