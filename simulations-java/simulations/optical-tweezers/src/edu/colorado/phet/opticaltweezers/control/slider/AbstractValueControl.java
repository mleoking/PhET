/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.slider;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Hashtable;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


/**
 * AbstractValueControl combines a slider and a text field into a single control. 
 * <p>
 * The slider supports double precision numbers, whereas JSlider only support integers.
 * As the slider value is changed, the text field automatically updates to reflect the 
 * new value.  The text field is optionally editable, and user input is validated.
 * <p>
 * The default "look" is to have labels at the min and max tick marks.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractValueControl extends JPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private double _value;
    private final double _min, _max;
    private double _tickSpacing;
    private double _delta;

    // View
    private AbstractSlider _slider;
    private JFormattedTextField _textField;
    private JLabel _valueLabel, _unitsLabel;

    // misc.
    private DecimalFormat _textFieldFormat;
    private DecimalFormat _tickFormat;
    private boolean _notifyWhileDragging; // if true, fire ChangeEvents while the slider is dragged
    private boolean _isAdjusting; // is the slider being adjusted (dragged) ?
    private Font _font;
    private EventListenerList _listenerList; // notification of slider changes
    private EventDispatcher _listener;

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
     * @throws IllegalArgumentException
     */
    public AbstractValueControl( AbstractSlider slider, String label, String textFieldPattern, String units ) {

        super();

        _slider = slider;

        _value = slider.getModelValue();
        _min = slider.getStrategy().getModelMin();
        _max = slider.getStrategy().getModelMax();
        _tickSpacing = _max - _min; // default is a tick mark at max and min
        _delta = _slider.getStrategy().sliderToModel( 1 );

        _textFieldFormat = new DecimalFormat( textFieldPattern );
        _tickFormat = new DecimalFormat( textFieldPattern ); // default to same precision as value display
        _notifyWhileDragging = true;
        _isAdjusting = false;
        _font = new JLabel().getFont();
        _listenerList = new EventListenerList();

        // Label
        JPanel valuePanel = new JPanel();
        {
            _valueLabel = new JLabel( label );
            _unitsLabel = new JLabel( units );

            _textField = new JFormattedTextField( _textFieldFormat );
            _textField.setValue( new Double( _value ) );
            _textField.setHorizontalAlignment( JTextField.RIGHT );
            _textField.setEditable( false );
            _textField.setColumns( textFieldPattern.length() );

            EasyGridBagLayout layout = new EasyGridBagLayout( valuePanel );
            valuePanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.addComponent( _valueLabel, 0, 0 );
            layout.addComponent( _textField, 0, 1 );
            layout.addComponent( _unitsLabel, 0, 2 );
        }

        // Layout
        {
            EasyGridBagLayout layout = new EasyGridBagLayout( this );
            setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setFill( GridBagConstraints.HORIZONTAL );
            layout.addComponent( valuePanel, 0, 0 );
            layout.addComponent( _slider, 1, 0 );
        }

        // Interactivity
        _listener = new EventDispatcher();
        _slider.addChangeListener( _listener );
        _textField.addActionListener( _listener );
        _textField.addFocusListener( _listener );
        _textField.addKeyListener( _listener );

        updateTickLabels();
        setValue( _value );
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
    public void setDelta( double delta ) {
        _delta = delta;
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
    public void setFonts( Font font ) {
        _font = font;
        this.setFont( font );
        _valueLabel.setFont( font );
        _textField.setFont( font );
        _unitsLabel.setFont( font );
        _slider.setFont( font );
        updateTickLabels();
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
    public JFormattedTextField getFormattedTextField() {
        return _textField;
    }

    //----------------------------------------------------------------------------
    // Value Display
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

    /**
     * Changes the label table to label only the min and max of the range.
     * 
     * @param minString
     * @param maxString
     */
    public void setTickLabels( String minString, String maxString ) {

        _slider.setPaintTicks( true );
        _slider.setPaintLabels( true );
        int sliderRange = _slider.getMaximum() - _slider.getMinimum();
        _slider.setMajorTickSpacing( sliderRange );
        _slider.setMinorTickSpacing( sliderRange );

        Hashtable labelTable = new Hashtable();
        Font font = _font;
        if ( font == null ) {
            font = new JLabel().getFont();
        }
        JLabel minLabel = new JLabel( minString );
        minLabel.setFont( font );
        labelTable.put( new Integer( _slider.getMinimum() ), minLabel );
        JLabel maxLabel = new JLabel( maxString );
        maxLabel.setFont( font );
        labelTable.put( new Integer( _slider.getMaximum() ), maxLabel );
        _slider.setLabelTable( labelTable );
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
     */
    private void updateTickLabels() {

        int sliderRange = _slider.getMaximum() - _slider.getMinimum();
        _slider.setMajorTickSpacing( sliderRange );
        int sliderTickSpacing = _slider.getStrategy().modelToSlider( _min + _tickSpacing );
        _slider.setMinorTickSpacing( sliderTickSpacing );
        _slider.setPaintTicks( true );
        _slider.setPaintLabels( true );

        Hashtable labelTable = new Hashtable();

        //  Major ticks
        JLabel label = new JLabel( _tickFormat.format( _min ) );
        label.setFont( _font );
        labelTable.put( new Integer( _slider.getMinimum() ), label );
        label = new JLabel( _tickFormat.format( _max ) );
        label.setFont( _font );
        labelTable.put( new Integer( _slider.getMaximum() ), label );

        // Minor ticks
        double value = _min + _tickSpacing;
        while ( value < _max ) {
            label = new JLabel( _tickFormat.format( value ) );
            label.setFont( _font );
            labelTable.put( new Integer( _slider.getStrategy().modelToSlider( value ) ), label );
            value += _tickSpacing;
        }

        getSlider().setLabelTable( labelTable );
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
                    final double value = getValue() + _delta;
                    if ( value <= _max ) {
                        setValue( value );
                    }
                }
                else if ( e.getKeyCode() == KeyEvent.VK_DOWN ) {
                    final double value = getValue() - _delta;
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
                boolean notify = ( _notifyWhileDragging || !_isAdjusting );
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
