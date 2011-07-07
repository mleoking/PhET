// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.control;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.view.controls.IntensitySlider;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * IntensityControl is an intensity control, with range from 0-100 %.
 * It combines a ColorIntensitySlider and an editable text field.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class IntensityControl extends JPanel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private IntensitySlider _slider;
    private JFormattedTextField _formattedTextField;
    private JLabel _units;
    private EventListenerList _listenerList;
    private Object _eventSource;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param size
     * @param font
     */
    public IntensityControl( Dimension size, Font font ) {
        super();
        
        _listenerList = new EventListenerList();
        _eventSource = this;
        EventHandler listener = new EventHandler();
        
        _slider = new IntensitySlider( Color.RED, IntensitySlider.HORIZONTAL, size );
        _slider.addChangeListener( listener );
        
        _formattedTextField = new JFormattedTextField();
        _formattedTextField.setFont( font );
        _formattedTextField.setColumns( 3 );
        _formattedTextField.setHorizontalAlignment( JTextField.RIGHT );
        _formattedTextField.addActionListener( listener );
        _formattedTextField.addFocusListener( listener );
        _formattedTextField.addKeyListener( listener );
        
        _units = new JLabel( "%" );
        _units.setFont( font );
        
        // Opacity
        setOpaque( false );
        _slider.setOpaque( false );
        _units.setOpaque( false );
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        layout.setInsets( new Insets( 0, 0, 0, 3 ) ); // top, left, bottom, right
        this.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int col = 0;
        layout.addComponent( _slider, row, col++ );
        layout.addComponent( _formattedTextField, row, col++ );
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
    
    public Color getColor() {
        return _slider.getColor();
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
    
    /*
     * Updates the slider to match the text field.
     * If the text field contains a bogus value, we warn the user and reset 
     * the text field value.
     */
    private void updateSlider() {
        boolean error = false;
        
        String s = _formattedTextField.getText();
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
    
    /*
     * Updates the text field to match the slider value.
     */
    private void updateTextField() {
        final int value = _slider.getValue();
        String s = "" + value;
        _formattedTextField.setText( s );
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
            if ( e.getSource() == _formattedTextField ) {
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
            if ( e.getSource() == _formattedTextField ) {
                updateSlider();
                fireChangeEvent( new ChangeEvent( _eventSource ) );
            }
        }
        
        /* Slider was moved. */
        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _slider ) {
                updateTextField();
                fireChangeEvent( new ChangeEvent( _eventSource ) );
            }
        }

        /**
         * Selects the entire value text field when it gains focus.
         */
        public void focusGained( FocusEvent e ) {
            if ( e.getSource() == _formattedTextField ) {
                _formattedTextField.selectAll();
            }
        }
        
        /**
         * Processes the contents of the value text field when it loses focus.
         */
        public void focusLost( FocusEvent e ) {
            if ( e.getSource() == _formattedTextField ) {
                try {
                    _formattedTextField.commitEdit();
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
