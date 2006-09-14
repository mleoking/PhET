/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.event.*;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.hydrogenatom.HAConstants;


public class IntensityControl extends JPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ColorIntensitySlider _slider;
    private JFormattedTextField _textField;
    private JLabel _units;
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public IntensityControl( Dimension size ) {
        super();
        
        _listenerList = new EventListenerList();
        EventHandler listener = new EventHandler();
        
        _slider = new ColorIntensitySlider( Color.RED, ColorIntensitySlider.HORIZONTAL, size );
        _slider.addChangeListener( listener );
        
        _textField = new JFormattedTextField();
        _textField.setColumns( 2 );
        _textField.addActionListener( listener );
        _textField.addFocusListener( listener );
        _textField.addKeyListener( listener );
        
        _units = new JLabel( "%" );
        _units.setFont( HAConstants.CONTROL_FONT );
        
        // Fonts
        _textField.setFont( HAConstants.CONTROL_FONT );
        
        // Opacity
        setOpaque( false );
        _slider.setOpaque( false );
        _units.setOpaque( false );
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int col = 0;
        layout.addComponent( _slider, row, col++ );
        layout.addComponent( _textField, row, col++ );
        layout.addComponent( _units, row, col++ );
        
        // Default state
        setValue( 0 );
    }
    
    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------
    
    public void setColor( Color color ) {
        _slider.setColor( color );
    }
    
    public void setValue( int value ) {
        _slider.setValue( value );
        updateTextField();
        fireChangeEvent( new ChangeEvent( this ) );
    }
    
    public int getValue() {
        return _slider.getValue();
    }
    
    public void setUnitsForeground( Color color ) {
        _units.setForeground( color );
    }
    
    //----------------------------------------------------------------------------
    // Private
    //----------------------------------------------------------------------------
    
    private void updateSlider() {
        boolean error = false;
        
        String s = _textField.getText();
        try {
            int i = Integer.parseInt( s );
            if ( i >= _slider.getMinimum() && i <= _slider.getMaximum() ) {
                _slider.setValue( i );
            }
            else {
                error = true;
            }
        }
        catch ( NumberFormatException e ) {
            error = true;
        }
        
        if ( error ) {
            warnUser();
            updateTextField();
        }
    }
    
    private void updateTextField() {
        final int value = _slider.getValue();
        String s = "" + value;
        _textField.setText( s );
    }
    
    /*
     * Produces an audible beep, used to indicate invalid text entry.
     */
    private void warnUser() {
        Toolkit.getDefaultToolkit().beep();
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /**
     * Adds a ChangeListener.
     *
     * @param listener the listener
     */
    public void addChangeListener( ChangeListener listener ) {
        _listenerList.add( ChangeListener.class, listener );
    }

    /**
     * Removes a ChangeListener.
     *
     * @param listener the listener
     */
    public void removeChangeListener( ChangeListener listener ) {
        _listenerList.remove( ChangeListener.class, listener );
    }

    /**
     * Fires a ChangeEvent.
     *
     * @param event the event
     */
    private void fireChangeEvent( ChangeEvent event ) {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ChangeListener.class ) {
                ( (ChangeListener)listeners[i + 1] ).stateChanged( event );
            }
        }
    }
    
    private class EventHandler extends KeyAdapter implements ActionListener, ChangeListener, FocusListener {
        
        /* Use the up/down arrow keys to change the value. */
        public void keyPressed( KeyEvent e ) {
            if ( e.getSource() == _textField ) {
                if ( e.getKeyCode() == KeyEvent.VK_UP ) {
                    final int value = _slider.getValue() + 1;
                    setValue( value );
                }
                else if ( e.getKeyCode() == KeyEvent.VK_DOWN ) {
                    final int value = _slider.getValue() - 1;
                    setValue( value );
                }
            }
        }
        
        /* User pressed enter in text field. */
        public void actionPerformed( ActionEvent e ) {
            if ( e.getSource() == _textField ) {
                updateSlider();
                fireChangeEvent( new ChangeEvent( this ) );
            }
        }
        
        /* Slider was moved. */
        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _slider ) {
                updateTextField();
                fireChangeEvent( new ChangeEvent( this ) );
            }
        }

        /**
         * Selects the entire value text field when it gains focus.
         */
        public void focusGained( FocusEvent e ) {
            if ( e.getSource() == _textField ) {
                _textField.selectAll();
            }
        }
        
        /**
         * Processes the contents of the value text field when it loses focus.
         */
        public void focusLost( FocusEvent e ) {
            if ( e.getSource() == _textField ) {
                try {
                    _textField.commitEdit();
                    updateSlider();
                }
                catch ( ParseException pe ) {
                    warnUser();
                    updateTextField(); // revert
                }
            }
        }
    }
}
