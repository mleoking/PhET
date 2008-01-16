/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.glaciers.GlaciersStrings;

/**
 * MiscControlPanel contains miscellaneous controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MiscControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JButton _equilibriumButton;
    private JButton _resetAllButton;
    private JButton _helpButton;
    
    private ArrayList _listeners; // list of MiscControlPanelListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MiscControlPanel() {
        super();
        
        _listeners = new ArrayList();
        
        _equilibriumButton = new JButton( GlaciersStrings.BUTTON_STEADY_STATE );
        _equilibriumButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyEquilibriumButtonPressed();
            }
        });
        
        _resetAllButton = new JButton( GlaciersStrings.BUTTON_RESET_ALL );
        _resetAllButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyResetAllButtonPressed();
            }
        });
        
        _helpButton = new JButton( GlaciersStrings.BUTTON_HELP );
        _helpButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
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
    // Listeners
    //----------------------------------------------------------------------------
    
    /**
     * Interface implemented by all listeners who are interested in events related to this control panel.
     */
    public interface MiscControlPanelListener {
        public void equilibriumButtonPressed();
        public void resetAllButtonPressed();
        public void helpButtonPressed();
    }
    
    public static class MiscControlPanelAdapter implements MiscControlPanelListener {
        public void equilibriumButtonPressed() {};
        public void resetAllButtonPressed() {};
        public void helpButtonPressed() {};
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
    
    private void notifyEquilibriumButtonPressed() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (MiscControlPanelListener) i.next() ).equilibriumButtonPressed();
        }
    }

    private void notifyResetAllButtonPressed() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (MiscControlPanelListener) i.next() ).resetAllButtonPressed();
        }
    }
    
    private void notifyHelpButtonPressed() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (MiscControlPanelListener) i.next() ).helpButtonPressed();
        }
    }
}
