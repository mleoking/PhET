/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;

/**
 * MiscControlPanel contains miscellaneous controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MiscControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Glacier _glacier;
    private final Frame _dialogOwner;
    
    private final JButton _equilibriumButton;
    private final ResetAllButton _resetAllButton;
    private final JButton _helpButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MiscControlPanel( Glacier glacier, Frame dialogOwner ) {
        super();
        
        _dialogOwner = dialogOwner;
        
        _glacier = glacier;
        glacier.addGlacierListener( new GlacierAdapter() {
            public void steadyStateChanged() {
                setEquilibriumButtonEnabled( !_glacier.isSteadyState() );
            }
        } );
        
        _equilibriumButton = new JButton( GlaciersStrings.BUTTON_STEADY_STATE );
        _equilibriumButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _glacier.setSteadyState();
            }
        });
        
        _resetAllButton = new ResetAllButton( _dialogOwner );
        
        _helpButton = new JButton();
        // set button to maximum width
        _helpButton.setText( GlaciersStrings.BUTTON_HIDE_HELP );
        double hideWidth = _helpButton.getPreferredSize().getWidth();
        _helpButton.setText( GlaciersStrings.BUTTON_SHOW_HELP );
        double showWidth = _helpButton.getPreferredSize().getWidth();
        _helpButton.setPreferredSize( new Dimension( (int) Math.max( hideWidth, showWidth ), (int) _helpButton.getPreferredSize().getHeight() ) );
        _helpButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setHelpEnabled( !isHelpEnabled() );
            }
        });
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int column = 0;
        layout.addComponent( _equilibriumButton, 0, column++ );
        layout.addComponent( _resetAllButton, 0, column++ );
        layout.addComponent( _helpButton, 0, column++ );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setHelpEnabled( boolean enabled ) {
        _helpButton.setText( enabled ? GlaciersStrings.BUTTON_HIDE_HELP : GlaciersStrings.BUTTON_SHOW_HELP );
    }
    
    public boolean isHelpEnabled() {
        return _helpButton.getText().equals( GlaciersStrings.BUTTON_HIDE_HELP );
    }
    
    public void setEquilibriumButtonEnabled( boolean enabled ) {
        _equilibriumButton.setEnabled( enabled );
    }
    
    /**
     *  For attaching Help items.
     */
    public JComponent getEquilibriumButton() {
        return _equilibriumButton;
    }
    
    public ResetAllButton getResetAllButton() {
        return _resetAllButton;
    }
    
    public JButton getHelpButton() {
        return _helpButton;
    }
}
