package edu.colorado.phet.acidbasesolutions.dialog;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


public class EquilibriumExpressionsDialog extends PaintImmediateDialog {
    
    //TODO localize
    private static final String TITLE = "Equilibrium Expressions";
    
    public EquilibriumExpressionsDialog( Frame owner ) {
        super( owner, TITLE );
        setResizable( true );
        
        JLabel label = new JLabel( "Equilibrium Expressions live here" );//XXX
        
        // layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 50, 10, 50, 10 ) );//XXX
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( label, row, column);
        
        getContentPane().add( panel );
        pack();
    }
    
    public static void main( String[] args ) {
        JDialog dialog = new EquilibriumExpressionsDialog( null );
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
