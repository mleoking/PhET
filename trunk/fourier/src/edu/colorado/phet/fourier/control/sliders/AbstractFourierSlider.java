/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.control.sliders;

import java.awt.GridBagConstraints;
import java.text.MessageFormat;
import java.util.Dictionary;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.fourier.util.EasyGridBagLayout;


/**
 * AbstractFourierSlider combines a JSlider and JLabel into one panel that 
 * can be treated like a JSlider.  As the slider value is changed, the label 
 * automatically updates to reflect the new value.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractFourierSlider extends JPanel implements ChangeListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JSlider _slider;  // slider that the user moves
    private JLabel _label; // value that updates as the slider is moved
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
    public AbstractFourierSlider( String format ) {
        super();
        
        assert( format != null );
        
        setBorder( BorderFactory.createEtchedBorder() );

        _format = format;
        _listenerList = new EventListenerList();
        
        // UI components
        _slider = new JSlider();
        _slider.addChangeListener( this );
        _label = new JLabel();

        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.addAnchoredComponent( _label, 0, 0, GridBagConstraints.WEST );
        layout.addAnchoredComponent( _slider, 1, 0, GridBagConstraints.WEST );
        
        updateLabel();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the value.
     * 
     * @param value
     */
    public abstract void setValue( double value );
    
    /**
     * Gets the value.
     * 
     * @return the value
     */
    public abstract double getValue();
    
    /**
     * Provides access to the JSlider.
     * 
     * @return the JSlider
     */
    public JSlider getSlider() {
        return _slider;
    }
    
    /**
     * Gets the JLabel that displays the value.
     * 
     * @return the JLabel
     */
    protected JLabel getLabel() {
        return _label;
    }
    
    /**
     * Gets the format used to format the label.
     * See MessageFormat.
     * 
     * @return the format
     */
    protected String getFormat() {
        return _format;
    }

    /**
     * Updates the label when the slider value changes.
     */
    protected abstract void updateLabel();
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /**
     * ChangeListener implementation.
     * 
     * @param e the event
     */
    public void stateChanged( ChangeEvent e ) {
        updateLabel();
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
