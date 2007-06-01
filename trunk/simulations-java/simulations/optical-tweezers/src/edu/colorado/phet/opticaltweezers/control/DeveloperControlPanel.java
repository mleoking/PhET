/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.dialog.DeveloperDialog;
import edu.colorado.phet.opticaltweezers.model.Bead;


public class DeveloperControlPanel extends JPanel {

    private Frame _parentFrame;
    private Bead _bead;
    private JCheckBox _developerControlsCheckBox;
    private DeveloperDialog _developerControlsDialog;
    
    public DeveloperControlPanel( Font titleFont, Font controlFont, Frame parentFrame, Bead bead ) {
        super();
        
        _parentFrame = parentFrame;
        _bead = bead;
        
        _developerControlsCheckBox = new JCheckBox( "Show Developer Controls" );
        _developerControlsCheckBox.setFont( controlFont );
        _developerControlsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleDeveloperControlsCheckBox();
            }
        });
        
        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        layout.setMinimumWidth( 0, 0 );
        int row = 0;
        layout.addComponent( _developerControlsCheckBox, row++, 1 );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // Default state
        _developerControlsCheckBox.setSelected( false );
    }
    
    public void setDeveloperControlsSelected( boolean b ) {
        _developerControlsCheckBox.setSelected( b );
        handleDeveloperControlsCheckBox();
    }
    
    public boolean isDeveloperControlsSelected() {
        return _developerControlsCheckBox.isSelected();
    }
    
    private void handleDeveloperControlsCheckBox() {
        
        final boolean selected = _developerControlsCheckBox.isSelected();
        
        if ( !selected ) {
            if ( _developerControlsDialog != null ) {
                _developerControlsDialog.dispose();
                _developerControlsDialog = null;
            }
        }
        else {
            _developerControlsDialog = new DeveloperDialog( _parentFrame, _bead );
            _developerControlsDialog.addWindowListener( new WindowAdapter() {

                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    _developerControlsDialog.dispose();
                }

                // called by JDialog.dispose
                public void windowClosed( WindowEvent e ) {
                    _developerControlsDialog = null;
                    _developerControlsCheckBox.setSelected( false );
                }
            } );
            _developerControlsDialog.show();
        }
    }
}
