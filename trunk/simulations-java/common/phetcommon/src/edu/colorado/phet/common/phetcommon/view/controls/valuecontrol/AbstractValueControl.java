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
 * AbstractValueControl combines a slider and a text field into a single control.
 * This class deals with the user's interaction with the slider and text field,
 * and the synchronization of the slider and text field.  The specifics of the 
 * layout is handled by an ILayoutStrategy. 
 * <p>
 * The slider supports double precision numbers, whereas JSlider only support integers.
 * As the slider value is changed, the text field automatically updates to reflect the 
 * new value.  The text field is editable by default, and user input is validated.
 * <p>
 * The default "look" is to have numeric labels at the min and max tick marks,
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
    private double _majorTickSpacing; // spacing of major tick marks
    private double _minorTickSpacing; // spacing of minor tick marks
    private double _upDownArrowDelta; // delta applied when you press the up/down arrow keys

    // View
    private AbstractSlider _slider; // slider that supports model coordinates
    private JFormattedTextField _textField;
    private JLabel _valueLabel, _unitsLabel;
    private Font _font; // font used for all components
    private DecimalFormat _textFieldFormat; // format for the text field
    private DecimalFormat _tickFormat; // format for the tick mark labels
    private boolean _majorTickLabelsVisible; // are major tick labels visible?
    private boolean _minorTickLabelsVisible; // are minor tick labels visible?
    private Hashtable _labelTable; // label table, to hold labels added via addTickLabel
    
    // misc.
    private boolean _notifyWhileAdjusting; // if true, fire ChangeEvents while the slider is adjusting
    private boolean _isAdjusting; // is the slider being adjusted (dragged) ?
    private EventListenerList _listenerList; // notification of slider changes
    private TextFieldListener _textFieldListener; // handles events related to textfield
    private SliderListener _sliderListener; // handles events related to the slider
    private boolean _initialized; // true when the constructor has completed

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
        _majorTickSpacing = _max - _min; // default is major tick marks at min and max
        _minorTickSpacing = 0;
        _upDownArrowDelta = ( _max - _min ) / 100;

        _textFieldFormat = new DecimalFormat( textFieldPattern );
        _tickFormat = new DecimalFormat( textFieldPattern ); // use same format for ticks and textfield
        _majorTickLabelsVisible = true; // major tick labels visible
        _minorTickLabelsVisible = false; // minor tick labels are typically not visible
        _labelTable = null; // instantiated when addTickLabel is called
        
        _notifyWhileAdjusting = true; // provide change notification while slider is dragging
        _isAdjusting = false;
        _font = new JLabel().getFont();
        _listenerList = new EventListenerList();

        // Labels
        _valueLabel = new JLabel( label );
        _unitsLabel = new JLabel( units );

        // Textfield
        _textField = new JFormattedTextField( _textFieldFormat );
        _textField.setValue( new Double( _value ) );
        _textField.setHorizontalAlignment( JTextField.RIGHT );
        _textField.setColumns( textFieldPattern.length() );
        
        // Layout the components
        layoutStrategy.doLayout( this );

        // Listeners
        _sliderListener = new SliderListener();
        _slider.addChangeListener( _sliderListener );
        _textFieldListener = new TextFieldListener();
        _textField.addActionListener( _textFieldListener );
        _textField.addFocusListener( _textFieldListener );
        _textField.addKeyListener( _textFieldListener );

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
     * Sets the delta used when pressing the up and down arrow keys.
     * 
     * @param delta
     */
    public void setUpDownArrowDelta( double delta ) {
        _upDownArrowDelta = delta;
    }

    /**
     * Enables/disables this control and all of its components.
     * 
     * @param enabled true or false
     */
    public void setEnabled( boolean enabled ) {
        this.setEnabled( enabled );
        _valueLabel.setEnabled( enabled );
        _textField.setEnabled( enabled );
        _unitsLabel.setEnabled( enabled );
        _slider.setEnabled( enabled );
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
    // TextField
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

    /*
     * Gets the double value from the text field.
     */
    private double getTextFieldValue() {
        String text = _textField.getText();
        return Double.parseDouble( text );
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
     * If this is set to false, then ChangeEvents are fired only when the slider is released.
     * 
     * @param b true or false
     */
    public void setNotifyWhileAdjusting( boolean b ) {
        _notifyWhileAdjusting = b;
    }

    /**
     * Determines whether the slider fires ChangeEvents while it is being dragged.
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
     * The same format is used for both major and minor tick labels.
     * 
     * @param pattern see DecimalFormat
     */
    public void setTickPattern( String pattern ) {
        _tickFormat = new DecimalFormat( pattern );
        updateTickLabels();
    }
    
    /**
     * Sets the spacing between major tick marks.
     * 
     * @param tickSpacing in model coordinates
     */
    public void setMajorTickSpacing( double tickSpacing ) {
        if ( tickSpacing != _minorTickSpacing ) {
            _majorTickSpacing = tickSpacing;
            updateTickLabels();
        }
    }

    /**
     * Sets the spacing between minor tick marks.
     * 
     * @param tickSpacing in model coordinates
     */
    public void setMinorTickSpacing( double tickSpacing ) {
        if ( tickSpacing != _minorTickSpacing ) {
            _minorTickSpacing = tickSpacing;
            updateTickLabels();
        }
    }
   
    /**
     * Controls the visibility of major tick labels.
     * 
     * @param visible true or false
     */
    public void setMajorTickLabelsVisible( boolean visible ) {
        if ( visible != _majorTickLabelsVisible ) {
            _majorTickLabelsVisible = visible;
            updateTickLabels();
        }
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
    
    /**
     * Labels a specified value with a string.
     * 
     * @param value
     * @param string
     */
    public void addTickLabel( double value, String string ) {
        JLabel label = new JLabel( string );
        label.setFont( _font );
        addTickLabel( value, label );
    }
    
    /**
     * Labels a specified tick value with an arbitrary object.
     * 
     * @param value
     * @param label
     */
    public void addTickLabel( double value, Object label ) {
        if ( _labelTable == null ) {
            _labelTable = new Hashtable();
        }
        _labelTable.put( new Integer( _slider.modelToSlider( value ) ), label );
        updateTickLabels();
    }
    
    /**
     * Clears all tick labels that were specified via addTickLabel.
     */
    public void clearTickLabels() {
        _labelTable = null;
        updateTickLabels();
    }
    
    /**
     * Determines whether the slider snaps to tick marks.
     * 
     * @param b true or false
     */
    public void setSnapToTicks( boolean b ) {
        _slider.setSnapToTicks( b );
    }

    //----------------------------------------------------------------------------
    // Private methods
    //----------------------------------------------------------------------------

    /*
     * Updates the view to match the model.
     */
    private void updateView() {

        _slider.removeChangeListener( _sliderListener );
        _slider.setModelValue( _value );
        _slider.addChangeListener( _sliderListener );

        _textField.removeActionListener( _textFieldListener );
        String text = _textFieldFormat.format( _value );
        _textField.setText( text );
        _textField.addActionListener( _textFieldListener );
    }

    /*
     * Updates tick labels.
     * If labels were specified via addTickLabel, use them.
     * Otherwise, generate numberic labels for the major and minor tick marks.
     */
    private void updateTickLabels() {

        // Slider properties related to ticks
        _slider.setMajorTickSpacing( _slider.modelToSlider( _min + _majorTickSpacing ) );
        if ( _minorTickSpacing > 0 ) {
            _slider.setMinorTickSpacing( _slider.modelToSlider( _min + _minorTickSpacing ) );
        }
        _slider.setPaintTicks( true );
        _slider.setPaintLabels( true );
        
        if ( _labelTable != null ) {
            // Use the labels provided via addTickLabel.
            _slider.setLabelTable( _labelTable );
        }
        else {
            // Generate numeric labels for major and minor tick marks.
            Hashtable labelTable = new Hashtable();

            // Major ticks
            if ( _majorTickLabelsVisible ) {
                double value = _min;
                while ( value <= _max ) {
                    JLabel label = new JLabel( _tickFormat.format( value ) );
                    label.setFont( _font );
                    labelTable.put( new Integer( _slider.modelToSlider( value ) ), label );
                    value += _majorTickSpacing;
                }
            }

            // Minor ticks
            if ( _minorTickLabelsVisible && _minorTickSpacing > 0 ) {
                double value = _min + _minorTickSpacing;
                while ( value < _max ) {
                    JLabel label = new JLabel( _tickFormat.format( value ) );
                    label.setFont( _font );
                    labelTable.put( new Integer( _slider.modelToSlider( value ) ), label );
                    value += _minorTickSpacing;
                }
            }

            _slider.setLabelTable( labelTable );
        }
    }

    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /*
     * Handles events related to the slider.
     */
    private class SliderListener implements ChangeListener {
        /*
         * Slider was changed.
         */
        public void stateChanged( ChangeEvent e ) {
            if ( e.getSource() == _slider ) {
                _isAdjusting = _slider.getValueIsAdjusting();
                boolean notify = ( _notifyWhileAdjusting || !_isAdjusting );
                double modelValue = _slider.getModelValue();
                System.out.println( "AbstractValueControl.stateChanged slider=" + _slider.getValue() + " model=" + modelValue );//XXX
                setValue( modelValue, notify );
            }
        }
    }
    
    /*
     * Handles events related to the textfield.
     */
    private class TextFieldListener extends KeyAdapter implements ActionListener, FocusListener {

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
