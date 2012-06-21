// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.ComponentOrientation;
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
import javax.swing.JTextArea;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
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
        JTextArea captionPane = new JTextArea( GeneExpressionBasicsResources.Strings.IMAGE_CAPTION + " " + GeneExpressionBasicsResources.Strings.IMAGE_ATTRIBUTION + " Dennis Kunkel Microscopy, Inc." );
//        JScrollPane scrollPane = new JScrollPane();
//        JTextPane captionPane = new JTextPane();
//        captionPane.setPreferredSize( new Dimension( 100, 100 ) );
//        scrollPane.add( captionPane );
        captionPane.setText( GeneExpressionBasicsResources.Strings.IMAGE_CAPTION + "\n\n" + GeneExpressionBasicsResources.Strings.IMAGE_ATTRIBUTION + " Dennis Kunkel Microscopy, Inc." );
        captionPane.setFont( CAPTION_FONT );
        captionPane.setEditable( false );
        captionPane.setOpaque( false );
        captionPane.setColumns( 60 );
//        StyledDocument doc = captionPane.getStyledDocument();
//        SimpleAttributeSet center = new SimpleAttributeSet();
//        StyleConstants.setAlignment( center, StyleConstants.ALIGN_CENTER );
//        doc.setParagraphAttributes( 0, doc.getLength(), center, false );

        // Workaround for issue where line wrapping doesn't seem to work
        // correctly for right-to-left languages, see Unfuddle #2448 for more
        // information.
        if ( ComponentOrientation.getOrientation( PhetResources.readLocale() ).isLeftToRight() ) {
            captionPane.setLineWrap( true );
            captionPane.setWrapStyleWord( true );
        }

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
        panel.add( captionPane, constraints );
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
