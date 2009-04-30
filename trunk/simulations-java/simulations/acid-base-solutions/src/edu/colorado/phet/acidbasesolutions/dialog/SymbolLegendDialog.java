package edu.colorado.phet.acidbasesolutions.dialog;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;


public class SymbolLegendDialog extends PaintImmediateDialog {
    
    //TODO localize
    private static final String TITLE = "Symbol Legend";
    private static final String HA_DESCRIPTION = 
        "<html>" + 
        "A generic acid. The symbol <i>A</i> refers to <br>" + 
        "the part of the acid that remains after <br>" +
        " dissociation. For example, <i>A</i> refers to <br>" + 
        "the chlorine atom in the acid HCl." + 
        "</html>";
    private static final String B_DESCRIPTION = 
        "<html>" +
        "A generic weak base. The symbol <i>B</i> <br>" + 
        "refers to the part of the molecule that <br>" + 
        "can accept a hydrogen atom. For example, <br>" + 
        "<i>B</i> refers to the ammonium molecule, NH<sub>3</sub>, <br>" + 
        "which becomes NH<sub>4</sub><sup>+</sup> (or <i>B</i>H<sup>+</sup>) in water." +
        "</html>";
    private static final String MOH_DESCRIPTION = 
        "<html>" +
        "A generic strong base, or metal hydroxide. <br>" +
        "The symbol <i>M</i> refers to the metal part <br>" + 
        "of the base that remains after dissociation <br>" + 
        "in water. For example, in NaOH, the <i>M</i> <br>" + 
        "refers to the sodium atom." +
        "</html>";
    
    public static final double MOLECULE_IMAGE_SCALE = 0.25; //TODO scale image files so that this is 1.0
    public static final Font MOLECULE_FONT = new PhetFont( Font.PLAIN, 14 );
    public static final Font DESCRIPTION_FONT = new PhetFont();
    
    public SymbolLegendDialog( Frame owner ) {
        super( owner, TITLE );
        setResizable( false );
        
        JPanel panel = new SymbolLegendPanel();
        panel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
        getContentPane().add( panel );
        
        pack();
    }
    
    private static class SymbolLegendPanel extends JPanel {
        
        public SymbolLegendPanel() {
            super();
            
            JLabel labelHA = new MoleculeLabel( ABSSymbols.HA, ABSImages.HA_MOLECULE );
            JLabel descriptionHA = new DescriptionLabel( HA_DESCRIPTION );
            
            JLabel labelB = new MoleculeLabel( ABSSymbols.B, ABSImages.B_MOLECULE );
            JLabel descriptionB = new DescriptionLabel( B_DESCRIPTION );
            
            JLabel labelMOH = new MoleculeLabel( ABSSymbols.MOH, ABSImages.MOH_MOLECULE );
            JLabel descriptionMOH = new DescriptionLabel( MOH_DESCRIPTION );
            
            // layout
            
            EasyGridBagLayout layout = new EasyGridBagLayout( this );
            layout.setInsets( new Insets( 2, 10, 2, 10 ) ); // top, left, bottom, right
            layout.setAnchor( GridBagConstraints.NORTHWEST );
            setLayout( layout );
            int row = 0;
            int column = 0;
            layout.addComponent( labelHA, row, column++ );
            layout.addComponent( descriptionHA, row++, column );
            column = 0;
            layout.addFilledComponent( new JSeparator(), row++, column, 2, 1, GridBagConstraints.HORIZONTAL );
            column = 0;
            layout.addComponent( labelB, row, column++ );
            layout.addComponent( descriptionB, row++, column );
            column = 0;
            layout.addFilledComponent( new JSeparator(), row++, column, 2, 1, GridBagConstraints.HORIZONTAL );
            column = 0;
            layout.addComponent( labelMOH, row, column++ );
            layout.addComponent( descriptionMOH, row++, column );
        }
    }
    
    private static class MoleculeLabel extends JLabel {
        
        public MoleculeLabel( String text, BufferedImage image ) {
            super( HTMLUtils.toHTMLString( text ) );
            setFont( MOLECULE_FONT );
            BufferedImage scaledImage = BufferedImageUtils.multiScale( image, MOLECULE_IMAGE_SCALE );
            setIcon( new ImageIcon( scaledImage ) );
            setVerticalTextPosition( JLabel.BOTTOM );
            setHorizontalTextPosition( JLabel.CENTER );
        }

        public void setText( String text ) {
            super.setText( HTMLUtils.toHTMLString( text ) );
        }
    }
    
    private static class DescriptionLabel extends JLabel {
        
        public DescriptionLabel( String text ) {
            super( HTMLUtils.toHTMLString( text ) );
            setFont( DESCRIPTION_FONT );
        }

        public void setText( String text ) {
            super.setText( HTMLUtils.toHTMLString( text ) );
        }
    }
    
    public static void main( String[] args ) {
        JDialog dialog = new SymbolLegendDialog( null );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit( 0 );
            }
            public void windowClosed(WindowEvent e) {
                System.exit( 0 );
            }
        } );
        dialog.setVisible( true );
    }
}
