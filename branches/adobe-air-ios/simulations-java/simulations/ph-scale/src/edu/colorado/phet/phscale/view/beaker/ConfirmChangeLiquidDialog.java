// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.phscale.view.beaker;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.phscale.PHScaleStrings;

/**
 * ConfirmChangeLiquidDialog is a dialog used to confirm whether the user wants
 * to switch liquids.  It has a "Don't ask me again" option to turn off this 
 * annoying "feature".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConfirmChangeLiquidDialog extends PaintImmediateDialog {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final JCheckBox _dontAskAgainCheckBox;
    private boolean _confirmed;
    private JButton _yesButton, _noButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ConfirmChangeLiquidDialog( Frame parent ) {
        super( parent );
        setModal( true );
        setResizable( false );
        
        _confirmed = false;
        
        JLabel messageLabel = new JLabel( PHScaleStrings.CONFIRM_CHANGE_LIQUID );
        _dontAskAgainCheckBox = new JCheckBox( PHScaleStrings.CHECK_BOX_DONT_ASK_AGAIN );
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout inputPanelLayout = new EasyGridBagLayout( inputPanel );
        inputPanel.setLayout( inputPanelLayout );
        int row = 0;
        int col = 0;
        inputPanelLayout.setInsets( new Insets( 5, 5, 5, 5 ) );
        inputPanelLayout.addComponent( messageLabel, row++, col );
        inputPanelLayout.addComponent( _dontAskAgainCheckBox, row++, col );
        
        _yesButton = new JButton( PHScaleStrings.BUTTON_YES );
        _yesButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _confirmed = true;
                dispose();
            }
        });
        
        _noButton = new JButton( PHScaleStrings.BUTTON_NO );
        _noButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _confirmed = false;
                dispose();
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( _yesButton );
        buttonPanel.add( _noButton );
        
        JPanel panel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        row = 0;
        col = 0;
        layout.addComponent( inputPanel, row++, col );
        layout.addFilledComponent( new JSeparator(), row++, col, GridBagConstraints.HORIZONTAL );
        layout.addAnchoredComponent( buttonPanel, row++, col, GridBagConstraints.CENTER );
        
        getContentPane().add( panel );
        pack();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public boolean isConfirmed() {
        return _confirmed;
    }
    
    public boolean getDontAskAgain() {
        return _dontAskAgainCheckBox.isSelected();
    }
}
