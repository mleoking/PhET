/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


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
 */
public class SliderControl extends JPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private boolean _initialized;
    
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
    private boolean _isAdjusting; // is the slider being adjusted (dragged) ?
    private Font _font;
    private final boolean _logarithmic;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
 
    /**
     * Constructor.
     * 
     * @param range (model coordinates)
     * @param label label that appears to the left of the value
     * @param valuePattern pattern used to format the displayed value
     * @param units units that appear to the right of the value
     * @param logarithmic is the slider logarithmic?
     * @throws IllegalArgumentException
     */
    public SliderControl(
            DoubleRange range,
            String label,
            String valuePattern,
            String units,
            boolean logarithmic ) {
        
        super();
        
        _value = range.getDefault();
        _min = range.getMin();
        _max = range.getMax();
        _tickSpacing = _max - _min;  // default is a tick mark at max and min

        _multiplier = Math.pow( 10, range.getSignificantDecimalPlaces() );

        _valueNumberFormat = new DecimalFormat( valuePattern );
        _tickNumberFormat = new DecimalFormat( valuePattern ); // default to same precision as value display

        _notifyWhileDragging = true;
        _isAdjusting = false;
        _logarithmic = logarithmic;
        
        _listenerList = new EventListenerList();
        
        // Label
        JPanel valuePanel = new JPanel();
        {
            _valueLabel = new JLabel( label );
            _unitsLabel = new JLabel( units );
            
            _valueTextField = new JFormattedTextField( _valueNumberFormat );
            _valueTextField.setValue( new Double(_value) );
            _valueTextField.setHorizontalAlignment( JTextField.RIGHT );
            _valueTextField.setEditable( false );
            _valueTextField.setColumns( valuePattern.length() );
            
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
            _slider.setValue( (int) ( _value * _multiplier ) );

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
        
        setValue( _value );
        
        _initialized = true;
    }
    
    /**
     * Constructor for a linear slider.
     * 
     * @param range
     * @param label
     * @param valuePattern
     * @param units
     */
    public SliderControl( 
            DoubleRange range,
            String label,
            String valuePattern,
            String units ) {
        this( range, label, valuePattern, units, false /* logarithmic */ );
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
            Toolkit.getDefaultToolkit().beep();
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
     * Makes the text field editable.
     * 
     * @param editable
     */
    public void setTextFieldEditable( boolean editable ) {
        _valueTextField.setEditable( editable );
    }
    
    /**
     * Changes visibility of text field.
     * 
     * @param visible true or false
     */
    public void setTextFieldVisible( boolean visible ) {
        _valueTextField.setVisible( visible );
    }
    
    /**
     * Determines whether the slider value is being dragged.
     * Use this instead of getSlider().getValueIsAdjusting().
     * 
     * @return true or false
     */
    public boolean isAdjusting() {
        return _isAdjusting;
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
    
    /**
     * Changes the label table to label only the min and max of the range.
     * 
     * @param minString
     * @param maxString
     */
    public void setMinMaxLabels( String minString, String maxString ) {
        Hashtable labelTable = new Hashtable();
        Font font = _font;
        if ( font == null ) {
            font = new JLabel().getFont();
        }
        JLabel minLabel = new JLabel( minString );
        minLabel.setFont( font  );
        labelTable.put( new Integer( _slider.getMinimum() ), minLabel );
        JLabel maxLabel = new JLabel( maxString );
        maxLabel.setFont( font );
        labelTable.put( new Integer( _slider.getMaximum() ), maxLabel );
        _slider.setLabelTable( labelTable );
    }
    
    /**
     * Sets the width of the slider.
     * 
     * @param width
     */
    public void setSliderWidth( int width ) {
        Dimension currentSize = _slider.getPreferredSize();
        _slider.setPreferredSize( new Dimension( width, currentSize.height ) );
    }
    
    /**
     * Sets the font for all components that are part of this control.
     * 
     * @param font
     */
    public void setFont( Font font ) {
        super.setFont( font );
        if ( _initialized ) {
            _font = font;
            _valueLabel.setFont( font );
            _valueTextField.setFont( font );
            _unitsLabel.setFont( font );
            _slider.setFont( font );
            updateTickLabels();
        }
    }
    
    public void setTickNumberPattern( String pattern ) {
        _tickNumberFormat = new DecimalFormat( pattern );
        updateTickLabels();
    }
    
    public void setValueNumberPattern( String pattern ) {
        _valueNumberFormat = new DecimalFormat( pattern );
        updateTextField();
    }
    
    public boolean isLogarithmic() {
        return _logarithmic;
    }
    
    /**
     * Sets the tick spacing.
     * 
     * @param tickSpacing in model coordinates
     */
    public void setTickSpacing( double tickSpacing ) {
        if ( tickSpacing != _tickSpacing ) {
            _tickSpacing = tickSpacing;
            updateTickLabels();
        }
    }
    
    //----------------------------------------------------------------------------
    // Private methods
    //----------------------------------------------------------------------------
    
    private double getModelValue() {
        double value = 0;
        if ( _logarithmic ) {
            value = getModelValueLog();
        }
        else {
            value = getModelValueLinear();
        }
        return value;
    }
    
    /*
     * Gets the model value from the slider.
     */
    private double getModelValueLinear() {
        final int sliderValue = _slider.getValue();
        double modelValue = 0;
        if ( isInverted() ) {
            modelValue = ( ( (_max + _min) * _multiplier ) - sliderValue ) / _multiplier;
            if ( modelValue > _max ) {
                modelValue = _max; // adjust for rounding error
            }
        }
        else {
            modelValue = sliderValue / _multiplier;
        }
        return modelValue;
    }
    
    private double getModelValueLog() {
        return getModelValueLinear(); //XXX log not implemented yet
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
        Font font = _font;
        if ( font == null ) {
            font = new JLabel().getFont();
        }
        JLabel label = null;
        if ( isInverted() ) {
            //  Major ticks
            label = new JLabel( _tickNumberFormat.format( _min ) );
            label.setFont( font );
            labelTable.put( new Integer( (int) ( _max * _multiplier ) ), label );
            label = new JLabel( _tickNumberFormat.format( _max ) );
            label.setFont( font );
            labelTable.put( new Integer( (int) ( _min * _multiplier ) ), label );

            // Minor ticks
            double value1 = _max - _tickSpacing;
            double value2 = _min + _tickSpacing;
            while ( value1 > _min ) {
                label = new JLabel( _tickNumberFormat.format( value1 ) );
                label.setFont( font );
                labelTable.put( new Integer( (int) ( value2 * _multiplier ) ), label );
                value1 -= _tickSpacing;
                value2 += _tickSpacing;
            }
        }
        else {
            //  Major ticks
            label = new JLabel( _tickNumberFormat.format( _max ) );
            label.setFont( font );
            labelTable.put( new Integer( (int) ( _max * _multiplier ) ), label );
            label = new JLabel( _tickNumberFormat.format( _min ) );
            label.setFont( font );
            labelTable.put( new Integer( (int) ( _min * _multiplier ) ), label );

            // Minor ticks
            double value = _min + _tickSpacing;
            while ( value < _max ) {
                label = new JLabel( _tickNumberFormat.format( value ) );
                label.setFont( font );
                labelTable.put( new Integer( (int) ( value * _multiplier ) ), label );
                value += _tickSpacing;
            }
        }
        getSlider().setLabelTable( labelTable );
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
                double value = getTextFieldValue();
                if ( value < _min ) {
                    value = _min;
                    Toolkit.getDefaultToolkit().beep();
                }
                else if ( value > _max ) {
                    value = _max;
                    Toolkit.getDefaultToolkit().beep();
                }
                setValue( value );
            }
        }
        
        /**
         * Slider was changed.
         * 
         * @param e the event
         */
        public void stateChanged( ChangeEvent e ) {
            if ( e.getSource() == _slider ) {
                _isAdjusting = _slider.getValueIsAdjusting();
                boolean notify = ( _notifyWhileDragging || !_isAdjusting );
                setValue( getModelValue(), notify );
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
                    Toolkit.getDefaultToolkit().beep();
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
