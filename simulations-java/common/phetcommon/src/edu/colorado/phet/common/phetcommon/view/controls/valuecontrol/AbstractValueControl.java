/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.controls.valuecontrol;

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

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


/**
 * AbstractValueControl combines a slider and a text field into a single control,
 * with a specific layout and "look". 
 * <p>
 * The slider supports double precision numbers, whereas JSlider only support integers.
 * As the slider value is changed, the text field automatically updates to reflect the 
 * new value.  The text field is editable by default, and user input is validated.
 * <p>
 * The default "look" is to have major tick marks at the min and max tick marks,
 * and no minor tick marks.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractValueControl extends JPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private double _value; // the current value
    private final double _min, _max; // for convenience, could get these from _slider
    private double _tickSpacing; // spacing of minor tick marks
    private double _upDownArrowDelta; // delta applied when you press the up/down arrow keys

    // View
    private AbstractSlider _slider; // slider that supports model coordinates
    private JFormattedTextField _textField;
    private JLabel _valueLabel, _unitsLabel;
    private DecimalFormat _textFieldFormat; // format for the text field
    private DecimalFormat _tickFormat; // format for the tick mark labels
    private Font _font; // font used for all components
    private String _minTickString, _maxTickString; //optional strings used to label min/max ticks
    private boolean _minorTickLabelsVisible; // are minor tick labels visible?
    
    // misc.
    private boolean _notifyWhileAdjusting; // if true, fire ChangeEvents while the slider is adjusting
    private boolean _isAdjusting; // is the slider being adjusted (dragged) ?
    private EventListenerList _listenerList; // notification of slider changes
    private EventDispatcher _listener;
    private boolean _initialized;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param slider
     * @param label label that appears to the left of the value
     * @param textFieldPattern pattern used to format the text field
     * @param units units that appear to the right of the value
     * @param horizontalAlignment GridBagConstraints.WEST, CENTER or EAST
     * @throws IllegalArgumentException
     */
    public AbstractValueControl( AbstractSlider slider, String label, String textFieldPattern, String units, ILayoutStrategy layoutStrategy ) {
        super();
        
        _slider = slider;

        _value = slider.getModelValue();
        _min = slider.getModelMin();
        _max = slider.getModelMax();
        _tickSpacing = _max - _min; // default is major tick marks at min and max
        _upDownArrowDelta = _slider.sliderToModel( 1 );

        _textFieldFormat = new DecimalFormat( textFieldPattern );
        _tickFormat = new DecimalFormat( textFieldPattern ); // default to same format as text field
        _notifyWhileAdjusting = true;
        _isAdjusting = false;
        _font = new JLabel().getFont();
        _listenerList = new EventListenerList();
        _minTickString = _maxTickString = null;
        _minorTickLabelsVisible = true;

        // Labels
        _valueLabel = new JLabel( label );
        _unitsLabel = new JLabel( units );

        // Textfield
        _textField = new JFormattedTextField( _textFieldFormat );
        _textField.setValue( new Double( _value ) );
        _textField.setHorizontalAlignment( JTextField.RIGHT );
        _textField.setColumns( textFieldPattern.length() );
        
        layoutStrategy.doLayout( this );

        // Interactivity
        _listener = new EventDispatcher();
        _slider.addChangeListener( _listener );
        _textField.addActionListener( _listener );
        _textField.addFocusListener( _listener );
        _textField.addKeyListener( _listener );

        updateTickLabels();
        setValue( _value );
        
        _initialized = true;
    }
    
    //----------------------------------------------------------------------------
    // Access to components
    //----------------------------------------------------------------------------

    /**
     * Gets a reference to the slider component.
     * 
     * @return the slider
     */
    public AbstractSlider getSlider() {
        return _slider;
    }

    /**
     * Gets a reference to the text field component.
     * 
     * @return the text field
     */
    public JFormattedTextField getTextField() {
        return _textField;
    }
    
    /**
     * Gets a reference to the value label.
     * 
     * @return JLabel
     */
    public JLabel getValueLabel() {
        return _valueLabel;
    }
    
    /**
     * Gets a reference to the units label.
     * 
     * @return JLabel
     */
    public JLabel getUnitsLabel() {
        return _unitsLabel;
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
     * 
     * @param value
     * @param notify
     */
    private void setValue( double value, boolean notify ) {
        if ( value >= _min && value <= _max ) {
            _value = value;
            updateView();
            if ( notify ) {
                fireChangeEvent( new ChangeEvent( this ) );
            }
        }
        else {
            Toolkit.getDefaultToolkit().beep();
            System.out.println( getClass().getName() + ".setValue: invalid value for slider labeled \"" + 
                    _valueLabel.getText() + "\", " + "range is " + _min + " to " + _max + ", tried to set " + value );
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
     * Gets maximum value.
     * 
     * @return max
     */
    public double getMaximum() {
        return _max;
    }

    /**
     * Gets minimum value.
     * 
     * @return min
     */
    public double getMinimum() {
        return _min;
    }

    /**
     * Sets the delta used when pressing the up and down arrows.
     * 
     * @param delta
     */
    public void setUpDownArrowDelta( double delta ) {
        _upDownArrowDelta = delta;
    }

    /**
     * Enables or disables all of this controls subcomponents.
     * 
     * @param enabled
     */
    public void setEnabled( boolean enabled ) {
        _valueLabel.setEnabled( enabled );
        _textField.setEnabled( enabled );
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
     * Sets the fonts for all components that are part of this control.
     * 
     * @param font
     */
    public void setFont( Font font ) {
        super.setFont( font );
        if ( _initialized ) {
            _font = font;
            _valueLabel.setFont( font );
            _textField.setFont( font );
            _unitsLabel.setFont( font );
            _slider.setFont( font );
            updateTickLabels();
        }
    }

    //----------------------------------------------------------------------------
    // Text Field
    //----------------------------------------------------------------------------

    /**
     * Makes the text field editable.
     * 
     * @param editable
     */
    public void setTextFieldEditable( boolean editable ) {
        _textField.setEditable( editable );
    }

    /**
     * Changes visibility of the text field.
     * 
     * @param visible true or false
     */
    public void setTextFieldVisible( boolean visible ) {
        _textField.setVisible( visible );
    }

    /**
     * Sets the pattern used to format the text field.
     * 
     * @param pattern see DecimalFormat
     */
    public void setTextFieldPattern( String pattern ) {
        _textFieldFormat = new DecimalFormat( pattern );
        _textField.setColumns( pattern.length() );
        updateView();
    }

    /**
     * Sets the columns width of the text field.
     * The default is determined by the width of the text field pattern.
     * 
     * @param columns
     */
    public void setTextFieldColumns( int columns ) {
        _textField.setColumns( columns );
    }

    //----------------------------------------------------------------------------
    // Slider
    //----------------------------------------------------------------------------

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
     * Determines whether the slider value is being dragged.
     * 
     * @return true or false
     */
    public boolean isAdjusting() {
        return _isAdjusting;
    }

    /**
     * Determines whether ChangeEvents are fired while the slider is dragged.
     * If this is set to false, then ChangeEvents are fired only when the 
     * slider is released.
     * 
     * @param b true or false
     */
    public void setNotifyWhileAdjusting( boolean b ) {
        _notifyWhileAdjusting = b;
    }

    /**
     * Determines whether the slider fires ChangeEvents while it is 
     * being dragged.
     * 
     * @return true or false
     */
    public boolean getNotifyWhileAdjusting() {
        return _notifyWhileAdjusting;
    }

    //----------------------------------------------------------------------------
    // Tick marks
    //----------------------------------------------------------------------------

    /**
     * Sets the pattern used to format tick labels.
     * 
     * @param pattern see DecimalFormat
     */
    public void setTickPattern( String pattern ) {
        _tickFormat = new DecimalFormat( pattern );
        updateTickLabels();
    }

    /**
     * Sets the spacing between minor tick marks.
     * 
     * @param tickSpacing in model coordinates
     */
    public void setTickSpacing( double tickSpacing ) {
        if ( tickSpacing != _tickSpacing ) {
            _tickSpacing = tickSpacing;
            updateTickLabels();
        }
    }

    /**
     * Sets the labels used for min and max ticks.
     * 
     * @param minTickString
     * @param maxTickString
     */
    public void setMinMaxTickLabels( String minTickString, String maxTickString ) {
        _minTickString = minTickString;
        _maxTickString = maxTickString;
        updateTickLabels();
    }
    
    /**
     * Controls the visibility of minor tick labels.
     * 
     * @param visible true or false
     */
    public void setMinorTickLabelsVisible( boolean visible ) {
        if ( visible != _minorTickLabelsVisible ) {
            _minorTickLabelsVisible = visible;
            updateTickLabels();
        }
    }

    //----------------------------------------------------------------------------
    // Private methods
    //----------------------------------------------------------------------------

    /*
     * Updates the view to match the model.
     */
    private void updateView() {

        _slider.removeChangeListener( _listener );
        _slider.setModelValue( _value );
        _slider.addChangeListener( _listener );

        _textField.removeActionListener( _listener );
        String text = _textFieldFormat.format( _value );
        _textField.setText( text );
        _textField.addActionListener( _listener );
    }

    /*
     * Updates tick labels.
     * Major tick marks are used for the min and max.
     * Minor ticks are used for all other tick marks.
     */
    private void updateTickLabels() {

        // Slider properties related to ticks
        int sliderRange = _slider.getMaximum() - _slider.getMinimum();
        _slider.setMajorTickSpacing( sliderRange );
        int sliderTickSpacing = _slider.modelToSlider( _min + _tickSpacing );
        _slider.setMinorTickSpacing( sliderTickSpacing );
        _slider.setPaintTicks( true );
        _slider.setPaintLabels( true );

        Hashtable labelTable = new Hashtable();

        // Min tick
        String labelString = _minTickString;
        if ( labelString == null ) {
            labelString = _tickFormat.format( _min );
        }
        JLabel label = new JLabel( labelString );
        label.setFont( _font );
        labelTable.put( new Integer( _slider.getMinimum() ), label );
        
        // Max tick
        labelString = _maxTickString;
        if ( labelString == null ) {
            labelString = _tickFormat.format( _max );
        }
        label = new JLabel( labelString );
        label.setFont( _font );
        labelTable.put( new Integer( _slider.getMaximum() ), label );

        // Minor ticks
        if ( _minorTickLabelsVisible ) {
            double value = _min + _tickSpacing;
            while ( value < _max ) {
                label = new JLabel( _tickFormat.format( value ) );
                label.setFont( _font );
                labelTable.put( new Integer( _slider.modelToSlider( value ) ), label );
                value += _tickSpacing;
            }
        }

        _slider.setLabelTable( labelTable );
    }

    /*
     * Gets the value from the text field.
     */
    private double getTextFieldValue() {
        String s = _textField.getText();
        double d = Double.parseDouble( s );
        return d;
    }

    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /*
     * Handles events for this control's components.
     */
    private class EventDispatcher extends KeyAdapter implements ActionListener, ChangeListener, FocusListener {

        /*
         * Use the up/down arrow keys to change the value.
         */
        public void keyPressed( KeyEvent e ) {
            if ( e.getSource() == _textField ) {
                if ( e.getKeyCode() == KeyEvent.VK_UP ) {
                    final double value = getValue() + _upDownArrowDelta;
                    if ( value <= _max ) {
                        setValue( value );
                    }
                }
                else if ( e.getKeyCode() == KeyEvent.VK_DOWN ) {
                    final double value = getValue() - _upDownArrowDelta;
                    if ( value >= _min ) {
                        setValue( value );
                    }
                }
            }
        }

        /*
         * User pressed enter in text field.
         */
        public void actionPerformed( ActionEvent e ) {
            if ( e.getSource() == _textField ) {
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

        /*
         * Slider was changed.
         */
        public void stateChanged( ChangeEvent e ) {
            if ( e.getSource() == _slider ) {
                _isAdjusting = _slider.getValueIsAdjusting();
                boolean notify = ( _notifyWhileAdjusting || !_isAdjusting );
                double value = _slider.getModelValue();
                setValue( value, notify );
            }
        }

        /*
         * Selects the entire value text field when it gains focus.
         */
        public void focusGained( FocusEvent e ) {
            if ( e.getSource() == _textField ) {
                _textField.selectAll();
            }
        }

        /*
         * Processes the contents of the value text field when it loses focus.
         */
        public void focusLost( FocusEvent e ) {
            if ( e.getSource() == _textField ) {
                try {
                    _textField.commitEdit();
                    double value = getTextFieldValue();
                    setValue( value );
                }
                catch ( ParseException pe ) {
                    Toolkit.getDefaultToolkit().beep();
                    updateView(); // revert
                }
            }
        }
    }

    //----------------------------------------------------------------------------
    // ChangeListener management
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
        for ( int i = 0; i < listeners.length; i += 2 ) {
            if ( listeners[i] == ChangeListener.class ) {
                ( (ChangeListener) listeners[i + 1] ).stateChanged( event );
            }
        }
    }
}
