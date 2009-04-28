package edu.colorado.phet.acidbasesolutions.view.reactionequations;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


public class ReactionEquationsDialog extends PaintImmediateDialog {
    
    //TODO localize
    private static final String TITLE = "Reaction Equations";
    private static final String ON = "on";
    private static final String OFF = "off";
    private static final String SYMBOL_SIZES_CHANGE = "<html>Symbol sizes change<br>with concentration:";
    
    private final JRadioButton _scaleOnButton, _scaleOffButton;
    
    public ReactionEquationsDialog( Frame owner ) {
        super( owner, TITLE );
        setResizable( true );
        
        JLabel label = new JLabel( SYMBOL_SIZES_CHANGE );
        _scaleOnButton = new JRadioButton( ON );
        _scaleOffButton = new JRadioButton( OFF );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _scaleOnButton );
        buttonGroup.add( _scaleOffButton );
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout( new BoxLayout( controlPanel, BoxLayout.X_AXIS ) );
        controlPanel.add( label );
        controlPanel.add( _scaleOnButton );
        controlPanel.add( _scaleOffButton );
        _scaleOffButton.setSelected( true );
        
        // layout
        JPanel panel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( controlPanel, row++, column);
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        
        getContentPane().add( panel );
        pack();
    }
    
    public static void main( String[] args ) {
        JDialog dialog = new ReactionEquationsDialog( null );
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
