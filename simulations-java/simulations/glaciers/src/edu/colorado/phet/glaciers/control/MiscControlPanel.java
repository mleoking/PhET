/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.DialogUtils;
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
    
    private final JButton _equilibriumButton;
    private final JButton _resetAllButton;
    private final JButton _helpButton;
    private final ArrayList _listeners; // list of MiscControlPanelListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MiscControlPanel( Glacier glacier ) {
        super();
        
        _glacier = glacier;
        glacier.addGlacierListener( new GlacierAdapter() {
            public void steadyStateChanged() {
                setEquilibriumButtonEnabled( !_glacier.isSteadyState() );
            }
        } );
        
        _listeners = new ArrayList();
        
        _equilibriumButton = new JButton( GlaciersStrings.BUTTON_STEADY_STATE );
        _equilibriumButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                _glacier.setSteadyState();
            }
        });
        
        _resetAllButton = new JButton( GlaciersStrings.BUTTON_RESET_ALL );
        _resetAllButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Frame frame = PhetApplication.instance().getPhetFrame();
                String message = PhetCommonResources.getInstance().getLocalizedString( "ControlPanel.message.confirmResetAll" );
                int option = DialogUtils.showConfirmDialog( frame, message, JOptionPane.YES_NO_OPTION );
                if ( option == JOptionPane.YES_OPTION ) {
                    notifyResetAllButtonPressed();
                }
            }
        });
        
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
                notifyHelpButtonPressed();
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
    
    /**
     *  For attaching Help items.
     */
    public JComponent getResetAllButton() {
        return _resetAllButton;
    }
    
    /**
     *  For attaching Help items.
     */
    public JComponent getHelpButton() {
        return _helpButton;
    }
    
    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------
    
    /**
     * Interface implemented by all listeners who are interested in events related to this control panel.
     */
    public interface MiscControlPanelListener {
        public void resetAllButtonPressed();
        public void setHelpEnabled( boolean enabled );
    }
    
    public static class MiscControlPanelAdapter implements MiscControlPanelListener {
        public void resetAllButtonPressed() {};
        public void setHelpEnabled( boolean enabled ) {};
    }
    
    public void addMiscControlPanelListener( MiscControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeMiscControlPanelListener( MiscControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    //----------------------------------------------------------------------------
    // Notification
    //----------------------------------------------------------------------------
    
    private void notifyResetAllButtonPressed() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (MiscControlPanelListener) i.next() ).resetAllButtonPressed();
        }
    }
    
    private void notifyHelpButtonPressed() {
        boolean enabled =  isHelpEnabled();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (MiscControlPanelListener) i.next() ).setHelpEnabled( enabled );
        }
    }
}
