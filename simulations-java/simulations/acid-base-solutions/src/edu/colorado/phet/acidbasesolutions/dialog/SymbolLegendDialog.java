package edu.colorado.phet.acidbasesolutions.dialog;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;


public class SymbolLegendDialog extends PaintImmediateDialog {
    
    //TODO localize
    private static final String TITLE = "Symbol Legend";
    private static final String HA_DESCRIPTION = 
        "<html>" + 
        "A generic acid. The symbol \"<i>A</i>\" refers to <br>" + 
        "the part of the acid that remains after <br>" +
        " dissociation. For example, \"<i>A</i>\" refers to <br>" + 
        "the chlorine atom in the acid HCl." + 
        "</html>";
    private static final String B_DESCRIPTION = 
        "<html>" +
        "A generic weak base. The symbol \"<i>B</i>\" <br>" + 
        "refers to the part of the molecule that <br>" + 
        "can accept a hydrogen atom. For example, <br>" + 
        "<i>B</i> refers to the ammonium molecule, NH<sub>3</sub>, <br>" + 
        "which becomes NH<sub>4</sub><sup>+</sup> (or <i>B</i>H<sup>+</sup>) in water." +
        "</html>";
    private static final String MOH_DESCRIPTION = 
        "<html>" +
        "A generic strong base, or metal hydroxide. <br>" +
        "The symbol \"<i>M</i>\" refers to the metal part <br>" + 
        "of the base that remains after dissociation <br>" + 
        "in water. For example, in NaOH, the \"<i>M</i>\" <br>" + 
        "refers to the sodium atom." +
        "</html>";
    
    public SymbolLegendDialog( Frame owner ) {
        super( owner, TITLE );
        setResizable( false );
        
        JLabel labelHA = new JLabel( HTMLUtils.toHTMLString( ABSSymbols.HA_EMPHASIS ) );
        JLabel descriptionHA = new JLabel( HTMLUtils.toHTMLString( HA_DESCRIPTION ) );
        
        JLabel labelB = new JLabel( HTMLUtils.toHTMLString( ABSSymbols.B_EMPHASIS ) );
        JLabel descriptionB = new JLabel( HTMLUtils.toHTMLString( B_DESCRIPTION ) );
        
        JLabel labelMOH = new JLabel( HTMLUtils.toHTMLString( ABSSymbols.MOH_EMPHASIS ) );
        JLabel descriptionMOH = new JLabel( HTMLUtils.toHTMLString( MOH_DESCRIPTION ) );
        
        // layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 2, 5, 2, 5 ) ); // top, left, bottom, right
        layout.setAnchor( GridBagConstraints.NORTHWEST );
        panel.setLayout( layout );
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
        
        getContentPane().add( panel );
        pack();
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
