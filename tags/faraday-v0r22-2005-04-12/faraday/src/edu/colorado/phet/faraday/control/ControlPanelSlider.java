/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control;

import java.awt.GridBagConstraints;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.faraday.util.EasyGridBagLayout;


/**
 * ControlPanelSlider combines a JSlider and JLabel into one panel that 
 * can be treated like a JSlider.  As the slider value is changed, the label 
 * automatically updates to reflect the new value.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ControlPanelSlider extends JPanel implements ChangeListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JSlider _slider;
    private JLabel _value;
    private String _format;
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param format format of the label used to display the value
     */
    public ControlPanelSlider( String format ) {
        super();
        
        assert( format != null );
        
        setBorder( BorderFactory.createEtchedBorder() );

        _format = format;
        _listenerList = new EventListenerList();
        
        // UI components
        _slider = new JSlider();
        _slider.addChangeListener( this );
        _value = new JLabel();

        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.addAnchoredComponent( _value, 0, 0, GridBagConstraints.WEST );
        layout.addAnchoredComponent( _slider, 1, 0, GridBagConstraints.WEST );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setValue( int value ) {
        _slider.setValue( value );
        Object[] args = { new Integer( value ) };
        String text = MessageFormat.format( _format, args );
        _value.setText( text );
    }
    
    public int getValue() {
        return _slider.getValue();
    }
    
    public void setMaximum( int maximum ) {
        _slider.setMaximum( maximum );
    }
    
    public void setMinimum( int minimum ) {
        _slider.setMinimum( minimum );
    }

    public void setMajorTickSpacing( int spacing ) { 
        _slider.setMajorTickSpacing( spacing );
        _slider.setPaintTicks( true );
        _slider.setPaintLabels( true );
    }
    
    public void setMinorTickSpacing( int spacing ) { 
        _slider.setMinorTickSpacing( spacing );
        _slider.setPaintTicks( true );
        _slider.setPaintLabels( true );
    }
    
    public void setSnapToTicks( boolean snap ) {
        _slider.setSnapToTicks( snap );
    }
  
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /**
     * ChangeListener implementation.
     * 
     * @param e the event
     */
    public void stateChanged( ChangeEvent e ) {
        int value = _slider.getValue();
        Object[] args = { new Integer( value ) };
        String text = MessageFormat.format( _format, args );
        _value.setText( text );
        fireChangeEvent( new ChangeEvent( this ) );
    }
 
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
                ( (ChangeListener) listeners[i + 1] ).stateChanged( event );
            }
        }
    }
}
