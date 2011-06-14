// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.controls.IntensitySlider;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;

/**
 * LaserPowerControl is a control for laser power.
 * It combines a ColorIntensitySlider and an editable text field.
 * <p>
 * NOTE! This control does not handle layout of its components.
 * Layout is handled in LaserControlPanel, so that we can deal 
 * with issues related to Piccolo dragging.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LaserPowerControl  {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private JLabel _label;
    private IntensitySlider _intensitySlider;
    private JFormattedTextField _formattedTextField;
    private JLabel _units;
    
    private double _minPower, _maxPower;
    private double _power;
    
    private DecimalFormat _formatter;
    private EventHandler _listener;
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param powerRange
     * @param label
     * @param units
     * @param pattern
     * @param columns
     * @param wavelength
     * @param sliderSize
     * @param font
     */
    public LaserPowerControl( DoubleRange powerRange, String label, String units, String pattern, int columns,
            double wavelength, Dimension sliderSize, Font font ) {
        super();
        
        _minPower = powerRange.getMin();
        _maxPower = powerRange.getMax();
      
        _listener = new EventHandler();
        _listenerList = new EventListenerList();
        
        Color color = VisibleColor.wavelengthToColor( wavelength );
        _intensitySlider = new IntensitySlider( color, IntensitySlider.HORIZONTAL, sliderSize );
        _intensitySlider.addChangeListener( _listener );
        
        _label = new JLabel( label );
        _label.setFont( font );
        
        _formattedTextField = new JFormattedTextField();
        _formattedTextField.setFont( font );
        _formattedTextField.setColumns( columns );
        _formattedTextField.setHorizontalAlignment( JTextField.RIGHT );
        _formattedTextField.addActionListener( _listener );
        _formattedTextField.addFocusListener( _listener );
        _formattedTextField.addKeyListener( _listener );
        
        _formatter = new DecimalFormat( pattern );
        
        _units = new JLabel( units );
        _units.setFont( font );
        
        // Opacity
        _intensitySlider.setOpaque( false );
        _units.setOpaque( false );
        
        // Default state
        _power = powerRange.getMin() - 1; // force an update
        setPower( powerRange.getDefault() );
    }
    
    //----------------------------------------------------------------------------
    // Accessors and mutators
    //----------------------------------------------------------------------------
    
    public JComponent getLabel() {
        return _label;
    }
    
    public JComponent getSlider() {
        return _intensitySlider;
    }
    
    public JComponent getTextField() {
        return _formattedTextField;
    }
    
    public JComponent getUnitsLabel() {
        return _units;
    }
    
    public void setWavelength( double wavelength ) {
        Color color = VisibleColor.wavelengthToColor( wavelength );
        _intensitySlider.setColor( color );
    }
    
    public void setPower( double power ) {
        if ( power < _minPower || power > _maxPower ) {
            throw new IllegalArgumentException( "power out of range: " + power );
        }
        if ( power != _power ) {
            _power = power;
            int intensity = powerToIntensity( power );
            _intensitySlider.setValue( intensity );
            String s = _formatter.format( power );
            _formattedTextField.setText( s );
            fireChangeEvent( new ChangeEvent( this ) );
        }
    }
    
    public double getPower() {
        return _power;
    }
    
    public double getMinPower() {
        return _minPower;
    }
    
    public double getMaxPower() {
        return _maxPower;
    }
    
    public void setLabelForeground( Color color ) {
        _label.setForeground( color );
    }
    
    public void setUnitsForeground( Color color ) {
        _units.setForeground( color );
    }
    
    //----------------------------------------------------------------------------
    // Conversions between power and intensity
    //----------------------------------------------------------------------------
    
    private int powerToIntensity( double power ) {
        assert( power >= _minPower );
        assert( power <= _maxPower );
        return (int)( 100 * ( power - _minPower ) / ( _maxPower - _minPower ) );
    }
    
    private double intensityToPower( int intensity ) {
        assert( intensity >= 0 );
        assert( intensity <= 100 );
        return _minPower + ( ( intensity / 100.0 ) * ( _maxPower - _minPower ) );
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
                    if ( _power < _maxPower ) {
                        _intensitySlider.removeChangeListener( _listener );
                        setPower( _power + 1 );
                        _intensitySlider.addChangeListener( _listener );
                    }
                }
                else if ( e.getKeyCode() == KeyEvent.VK_DOWN ) {
                    if ( _power > _minPower ) {
                        _intensitySlider.removeChangeListener( _listener );
                        setPower( _power - 1 );
                        _intensitySlider.addChangeListener( _listener );
                    }
                }
            }
        }
        
        /* User pressed enter in text field. */
        public void actionPerformed( ActionEvent e ) {
            if ( e.getSource() == _formattedTextField ) {
                handleTextFieldChanged();
            }
        }
        
        /* Slider was moved. */
        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _intensitySlider ) {
                handleSliderChanged();
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
                    handleTextFieldChanged();
                }
                catch ( ParseException pe ) {
                    Toolkit.getDefaultToolkit().beep();
                    revertTextField();
                }
            }
        }
    }
    
    private void handleTextFieldChanged() {
        
        // Get the value from the textfield
        String s = _formattedTextField.getText();
        double power = 0;
        try {
            power = Integer.parseInt( s );
        }
        catch ( NumberFormatException e ) {
            Toolkit.getDefaultToolkit().beep();
            revertTextField();
        }

        boolean textFieldDirty = false;
        if ( power < _minPower ) {
            Toolkit.getDefaultToolkit().beep();
            power = _minPower;
            textFieldDirty = true;
        }
        else if ( power > _maxPower ) {
            Toolkit.getDefaultToolkit().beep();
            power = _maxPower;
            textFieldDirty = true;
        }
        
        // Set the power
        _intensitySlider.removeChangeListener( _listener );
        setPower( power );
        _intensitySlider.addChangeListener( _listener );
        
        if ( textFieldDirty ) {
            revertTextField();
        }
    }
    
    private void handleSliderChanged() {
        int intensity = _intensitySlider.getValue();
        double power = intensityToPower( intensity );
        _intensitySlider.removeChangeListener( _listener );
        setPower( power );
        _intensitySlider.addChangeListener( _listener );
    }
    
    private void revertTextField() {
        String s = _formatter.format( _power );
        _formattedTextField.setText( s ); // revert
    }
}
