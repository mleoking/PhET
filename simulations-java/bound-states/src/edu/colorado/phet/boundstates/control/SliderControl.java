/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.control;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;


/**
 * SliderControl combines a JSlider and JFormattedTextField into one panel that 
 * can be treated like a JSlider.  
 * <p>
 * The slider supports double precision numbers, whereas JSlider only support integers.
 * As the slider value is changed, the text field automatically updates to reflect the 
 * new value.  The text field is optionally editable, and user input is validated.
 * <p>
 * The default "look" is to have labels at the min and max tick marks.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SliderControl extends JPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model
    private double _value;
    private double _min, _max;
    private double _tickSpacing;
    
    // View
    private JSlider _slider;
    private JFormattedTextField _valueTextField;
    private JLabel _valueLabel, _unitsLabel;
    
    // misc.
    private double _multiplier;
    private DecimalFormat _tickNumberFormat;
    private DecimalFormat _valueNumberFormat;
    private EventListenerList _listenerList; // notification of slider changes
    private boolean _notifyWhileDragging; // if true, fire ChangeEvents while the slider is dragged
    private boolean _isDragging; // is the slider currently being dragged?
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
 
    /**
     * Constructor.
     * 
     * @param value initial value (model coordinates)
     * @param min minimum value (model coordinates)
     * @param max maximum value (model coordinates)
     * @param tickSpacing space between tick marks (model coordinates)
     * @param tickDecimalPlaces number of decimal places in tick marks
     * @param valueDecimalPlaces number of decimal places in selected value display
     * @param label label that appears to the left of the value
     * @param units units that appear to the right of the value
     * @param columns columns in the text field
     * @param insets insets
     * @throws IllegalArgumentException
     */
    public SliderControl(
            double value,
            double min, 
            double max, 
            double tickSpacing,
            int tickDecimalPlaces, 
            int valueDecimalPlaces,
            String label,
            String units,
            int columns,
            Insets insets ) {
        
        super();
        
        // Validate arguments
        if ( value < min || value > max ) {
            throw new IllegalArgumentException( "value is out of range: " + value );
        }
        if ( min > max ) {
            throw new IllegalArgumentException( "min > max" );
        }
        if ( tickDecimalPlaces < 0 ) {
            throw new IllegalArgumentException( "tickPrecision must be >= 0" );
        }
        if ( valueDecimalPlaces < 0 ) {
            throw new IllegalArgumentException( "valuePrecision must be >= 0" );
        }

        _value = value;
        _min = min;
        _max = max;
        _tickSpacing = tickSpacing;

        _multiplier = Math.pow( 10, valueDecimalPlaces );

        _tickNumberFormat = createFormat( tickDecimalPlaces );
        _valueNumberFormat = createFormat( valueDecimalPlaces );

        _notifyWhileDragging = true;
        _isDragging = false;
        
        _listenerList = new EventListenerList();
        
        // Label
        JPanel valuePanel = new JPanel();
        {
            _valueLabel = new JLabel( label );
            _unitsLabel = new JLabel( units );
            
            _valueTextField = new JFormattedTextField( _valueNumberFormat );
            _valueTextField.setValue( new Double(value) );
            _valueTextField.setHorizontalAlignment( JTextField.RIGHT );
            _valueTextField.setEditable( false );
            _valueTextField.setColumns( columns );
            
            EasyGridBagLayout layout = new EasyGridBagLayout( valuePanel );
            valuePanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.addComponent( _valueLabel, 0, 0 );
            layout.addComponent( _valueTextField, 0, 1 );
            layout.addComponent( _unitsLabel, 0, 2 );
        }
        
        // Slider
        {
            _slider = new JSlider();
            _slider.setMinimum( (int) ( _min * _multiplier ) );
            _slider.setMaximum( (int) ( _max * _multiplier ) );
            _slider.setValue( (int) ( value * _multiplier ) );

            // Ticks
            _slider.setMajorTickSpacing( (int) ( ( _max - _min ) * _multiplier ) );
            _slider.setMinorTickSpacing( (int) ( _tickSpacing * _multiplier ) );
            _slider.setPaintTicks( true );
            _slider.setPaintLabels( true );
            updateTickLabels();
        }
        
        // Layout
        {
            EasyGridBagLayout layout = new EasyGridBagLayout( this );
            if ( insets != null ) {
                layout.setInsets( insets );
            }
            setLayout( layout );
            layout.addAnchoredComponent( valuePanel, 0, 0, GridBagConstraints.WEST );
            layout.addAnchoredComponent( _slider, 1, 0, GridBagConstraints.WEST );
        }
        
        // Interactivity
        EventDispatcher listener = new EventDispatcher();
        _valueTextField.addActionListener( listener );
        _valueTextField.addFocusListener( listener );
        _valueTextField.addKeyListener( listener );
        _slider.addChangeListener( listener );
        
        setValue( value );
    }
    
    /**
     * Constructor. Same as other constructor, but uses default insets.
     * 
     * @param value
     * @param min
     * @param max
     * @param tickSpacing
     * @param tickDecimalPlaces
     * @param valueDecimalPlaces
     * @param label
     * @param units
     * @param columns
     */
    public SliderControl( 
            double value,
            double min, 
            double max, 
            double tickSpacing,
            int tickDecimalPlaces,
            int valueDecimalPlaces,
            String label,
            String units,
            int columns ) {
        this( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns, null /* insets */ );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the value.
     * 
     * @param value
     */
    public void setValue( double value ) {
        setValue( value, true );
    }
    
    /*
     * Sets the value and optionally fires a ChangeEvent.
     * @param value
     * @param notify
     */
    private void setValue( double value, boolean notify ) {
        if ( isValid( value ) ) {
            _value = value;
            updateView();
            if ( notify ) {
                fireChangeEvent( new ChangeEvent( this ) );
            }
        }
        else {
            warnUser();
            System.out.println( "SliderControl.setValue: invalid value for slider labeled \"" + _valueLabel.getText() + "\", " + "range is " + _min + " to " + _max + ", tried to set " + value );
            updateView(); // revert
        }
    }
    
    /**
     * Gets the value.
     * 
     * @return the value
     */
    public double getValue() {
        return _value;
    }
    
    /**
     * Enables or disables all of this controls subcomponents.
     * 
     * @param enabled
     */
    public void setEnabled( boolean enabled ) {
        _valueLabel.setEnabled( enabled );
        _valueTextField.setEnabled( enabled );
        _unitsLabel.setEnabled( enabled );
        _slider.setEnabled( enabled );
    }
    
    /**
     * Is this control enabled?
     * 
     * @return true or false
     */
    public boolean isEnabled() {
        return _slider.isEnabled();
    }
   
    /**
     * Inverts the slider.
     * <p>
     * You may want to invert the slider if the distance between your lowest two 
     * ticks is smaller than the distance between all other ticks.  In order
     * to pull this off with a JSlider, we need to invert the slider
     * and make use of both major and minor tick spacing.
     * 
     * @param inverted true or false
     */
    public void setInverted( boolean inverted ) {
        if ( isInverted() != inverted ) {
            double value = getValue();
            _slider.setInverted( inverted );
            setValue( value );
            updateTickLabels();
        }
    }
    
    /**
     * Is the slider inverted?
     * 
     * @return true or false
     */
    public boolean isInverted() {
        return _slider.getInverted();
    }
    
    /**
     * Gets a reference to the JSlider.
     * 
     * @return the JSlider
     */
    public JSlider getSlider() {
        return _slider;
    }
    
    /**
     * Gets a reference to the JFormattedTextField.
     * 
     * @return the JFormattedTextField
     */
    public JFormattedTextField getFormattedTextField() {
        return _valueTextField;
    }

    /**
     * Makes the text editable.
     * 
     * @param editable
     */
    public void setTextEditable( boolean editable ) {
        _valueTextField.setEditable( editable );
    }
    
    /**
     * Determines whether the slider value is being dragged.
     * Use this instead of getSlider().getValueIsAdjusting().
     * 
     * @return true or false
     */
    public boolean isDragging() {
        return _isDragging;
    }
    
    /**
     * Sets the slider's maximum, in model coordinates.
     * 
     * @param max
     */
    public void setMaximum( double max ) {
        _max = max;
        _slider.setMaximum( (int) ( _max * _multiplier ) );
        _slider.setMajorTickSpacing( (int) ( ( _max - _min ) * _multiplier ) );
        updateTickLabels();
    }
    
    /**
     * Gets the slider's maximumum, in model coordinates.
     * 
     * @return max
     */
    public double getMaximum() {
        return _max;
    }
    
    /**
     * Sets the slider's minimum, in model coordinates.
     * 
     * @param min
     */
    public void setMinimum( double min ) {
        _min = min;
        _slider.setMinimum( (int) ( _min * _multiplier ) );
        _slider.setMajorTickSpacing( (int) ( ( _max - _min ) * _multiplier ) );
        updateTickLabels();
    }
    
    /**
     * Gets the slider's minimum, in model coordinates.
     * 
     * @return min
     */
    public double getMinimum() {
        return _min;
    }
    
    /**
     * Determines whether ChangeEvents are fired while the slider is dragged.
     * If this is set to false, then ChangeEvents are fired only when the 
     * slider is released.
     * 
     * @param b true or false
     */
    public void setNotifyWhileDragging( boolean b ) {
        _notifyWhileDragging = b;
    }
    
    /**
     * Determines whether the slider fires ChangeEvents while it is 
     * being dragged.
     * 
     * @return true or false
     */
    public boolean getNotifyWhileDragging() {
        return _notifyWhileDragging;
    }
    
    /*
     * Gets the multiplier used to convert the JSlider's integer values 
     * to double model values. For use by subclasses.
     */
    protected double getMultiplier() {
        return _multiplier;
    }
    
    //----------------------------------------------------------------------------
    // Private methods
    //----------------------------------------------------------------------------
    
    /*
     * Gets the value from the slider.
     */
    private double getSliderValue() {
        int sliderValue = getSlider().getValue();
        double value = 0;
        if ( isInverted() ) {
            value = ( ( (_max + _min) * _multiplier ) - sliderValue ) / _multiplier;
            if ( value > _max ) {
                value = _max; // adjust for rounding error
            }
        }
        else {
            value = sliderValue / _multiplier;
        }
        return value;
    }
    
    /*
     * Gets the value from the text field.
     */
    private double getTextFieldValue() {
        return ( (Number) _valueTextField.getValue() ).doubleValue();
    }
    
    /*
     * Updates the view to match the model.
     */
    private void updateView() {
        updateTextField();
        updateSlider();
    }
    
    /*
     * Updates the text field to match the model.
     */
    private void updateTextField() {
        String text = _valueNumberFormat.format( _value );
        _valueTextField.setText( text );
    }
     
    /*
     * Updates the slider to match the model.
     */
    private void updateSlider() {
        int sliderValue = 0;
        if ( isInverted() ) {
            sliderValue = (int) ( ( ( _max + _min ) * _multiplier ) - ( _value * _multiplier ) );
        }
        else {
            sliderValue = (int) ( _value * _multiplier );
        }
        _slider.setValue( sliderValue );
    }
    
    /*
     * Is a specified value valid?
     * 
     * @param value
     * @return true or false
     */
    private boolean isValid( double value ) {
        return ( value >= _min && value <= _max  );
    }
    
    /*
     * Updates tick labels.
     */
    private void updateTickLabels() {
        Hashtable labelTable = new Hashtable();
        if ( isInverted() ) {
            //  Major ticks
            labelTable.put( new Integer( (int) ( _max * _multiplier ) ), new JLabel( _tickNumberFormat.format( _min ) ) );
            labelTable.put( new Integer( (int) ( _min * _multiplier ) ), new JLabel( _tickNumberFormat.format( _max ) ) );

            // Minor ticks
            double value1 = _max - _tickSpacing;
            double value2 = _min + _tickSpacing;
            while ( value1 > _min ) {
                labelTable.put( new Integer( (int) ( value2 * _multiplier ) ), new JLabel( _tickNumberFormat.format( value1 ) ) );
                value1 -= _tickSpacing;
                value2 += _tickSpacing;
            }
        }
        else {
            //  Major ticks
            labelTable.put( new Integer( (int) ( _max * _multiplier ) ), new JLabel( _tickNumberFormat.format( _max ) ) );
            labelTable.put( new Integer( (int) ( _min * _multiplier ) ), new JLabel( _tickNumberFormat.format( _min ) ) );

            // Minor ticks
            double value = _min + _tickSpacing;
            while ( value < _max ) {
                labelTable.put( new Integer( (int) ( value * _multiplier ) ), new JLabel( _tickNumberFormat.format( value ) ) );
                value += _tickSpacing;
            }
        }
        getSlider().setLabelTable( labelTable );
    }
    
    /*
     * Creates a DecimalFormat with a specified number of decimal places.
     * 
     * @param precision number of decimal places
     */
    private DecimalFormat createFormat( int decimalPlaces ) {
        String format = "0";
        for ( int i = 0; i < decimalPlaces; i++ ) {
            if ( i == 0 ) {
                format += ".";
            }
            format += "0";
        }
        return new DecimalFormat( format );
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
    
    /*
     * Dispatches events to the proper handler.
     */
    private class EventDispatcher extends KeyAdapter implements ActionListener, ChangeListener, FocusListener {

        /**
         * Use the up/down arrow keys to change the value.
         */
        public void keyPressed( KeyEvent e ) {
            if ( e.getSource() == _valueTextField ) {
                if ( e.getKeyCode() == KeyEvent.VK_UP ) {
                    final double value = getValue() + ( 1 / _multiplier );
                    setValue( value );
                }
                else if ( e.getKeyCode() == KeyEvent.VK_DOWN ) {
                    final double value = getValue() - ( 1 / _multiplier );
                    setValue( value );
                }
            }
        }
        
        /**
         * User pressed enter in text field.
         */
        public void actionPerformed( ActionEvent e ) {
            if ( e.getSource() == _valueTextField ) {
                setValue( getTextFieldValue() );
            }
        }
        
        /**
         * Slider was changed.
         * 
         * @param e the event
         */
        public void stateChanged( ChangeEvent e ) {
            if ( e.getSource() == _slider ) {
                _isDragging = _slider.getValueIsAdjusting();
                boolean notify = ( _notifyWhileDragging || !_isDragging );
                setValue( getSliderValue(), notify );
            }
        }
        
        /**
         * Selects the entire value text field when it gains focus.
         */
        public void focusGained( FocusEvent e ) {
            if ( e.getSource() == _valueTextField ) {
                _valueTextField.selectAll();
            }
        }
        
        /**
         * Processes the contents of the value text field when it loses focus.
         */
        public void focusLost( FocusEvent e ) {
            if ( e.getSource() == _valueTextField ) {
                try {
                    _valueTextField.commitEdit();
                    setValue( getTextFieldValue() );
                }
                catch ( ParseException pe ) {
                    warnUser();
                    updateView(); // revert
                }
            }
        }
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
