/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.control;

import java.awt.GridBagConstraints;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.fourier.util.EasyGridBagLayout;


/**
 * FourierSlider combines a JSlider and JLabel into one panel that 
 * can be treated like a JSlider.  As the slider value is changed, the label 
 * automatically updates to reflect the new value.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierSlider extends JPanel implements ChangeListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JSlider _slider;  // slider that the user moves
    private JLabel _value; // value that updates as the slider is moved
    private String _format; // format that specifies how the value is displayed
    private EventListenerList _listenerList; // notification of slider changes
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param format format of the label used to display the value
     */
    public FourierSlider( String format ) {
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
        
        update();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the slider's value.
     * 
     * @param value
     */
    public void setValue( int value ) {
        _slider.setValue( value );
        update();
    }
    
    /**
     * Gets the slider's value.
     * 
     * @return the value
     */
    public int getValue() {
        return _slider.getValue();
    }
    
    /**
     * Sets the slider's maximum value.
     * 
     * @param maximum
     */
    public void setMaximum( int maximum ) {
        _slider.setMaximum( maximum );
        update();
    }
    
    /**
     * Sets the sliders' minimum value.
     * 
     * @param minimum
     */
    public void setMinimum( int minimum ) {
        _slider.setMinimum( minimum );
        update();
    }

    /**
     * Sets the spacing between major tick marks.
     * 
     * @param spacing
     */
    public void setMajorTickSpacing( int spacing ) { 
        _slider.setMajorTickSpacing( spacing );
        _slider.setPaintTicks( true );
        _slider.setPaintLabels( true );
        update();
    }
    
    /**
     * Sets the spacing between minor tick marks.
     * 
     * @param spacing
     */
    public void setMinorTickSpacing( int spacing ) { 
        _slider.setMinorTickSpacing( spacing );
        _slider.setPaintTicks( true );
        _slider.setPaintLabels( true );
        update();
    }
    
    /**
     * Controls how the slider behaves when it is released.
     * Specifying true causes the slider to "snap" to the nearest tick mark.
     * 
     * @param snap true or false
     */
    public void setSnapToTicks( boolean snap ) {
        _slider.setSnapToTicks( snap );
        update();
    }
  
    /**
     * Provides access to the JSlider.
     * 
     * @return the JSlider
     */
    public JSlider getSlider() {
        return _slider;
    }
    
    private void update() {
        int value = _slider.getValue();
        Object[] args = { new Integer( value ) };
        String text = MessageFormat.format( _format, args );
        _value.setText( text );
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
        update();
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
