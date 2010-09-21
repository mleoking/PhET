/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock.ConstantDtClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock.ConstantDtClockEvent;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.OTClock;

/**
 * SimulationSpeedControlPanel contains all the controls for simulation speed (dt).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimulationSpeedControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String VALUE_PATTERN = "0.0E00";
    private static final DecimalFormat VALUE_FORMAT = new DecimalFormat( VALUE_PATTERN );
    private static final int VALUE_COLUMNS = VALUE_PATTERN.length();
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private OTClock _clock;
    private SimulationSpeedSlider _slider;
    private JFormattedTextField _textField;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SimulationSpeedControlPanel( Font titleFont, Font controlFont, OTClock clock ) {
        super();
        
        _clock = clock;
        _clock.addConstantDtClockListener( new ConstantDtClockAdapter() {
            public void dtChanged( ConstantDtClockEvent event ) {
                //XXX handle dt changes that are performed by someone else
            };
        });
        
        // Title
        JLabel titleLabel = new JLabel( OTResources.getString( "label.simulationSpeed" ) );
        titleLabel.setFont( titleFont );
        
        // Slider
        _slider = new SimulationSpeedSlider( clock.getSlowRange(), clock.getFastRange(), clock.getDt() );
        _slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setSimulationSpeed( _slider.getValue() );
            }
        });
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setBackground( getBackground() );
        canvas.setBorder( null );
        canvas.getLayer().addChild( _slider );
        int margin = 2;
        int xOffset = (int) -_slider.getFullBounds().getX() + margin;
        int yOffset = (int) -_slider.getFullBounds().getY() + margin;
        _slider.setOffset( xOffset, yOffset );
        int w = (int) _slider.getFullBounds().getWidth() + ( 2 * margin );
        int h = (int) _slider.getFullBounds().getHeight() + ( 2 * margin );
        canvas.setPreferredSize( new Dimension( w, h ) );
        
        // Textfield
        _textField = new JFormattedTextField( VALUE_FORMAT );
        _textField.setFont( controlFont );
        _textField.setValue( new Double( _slider.getValue() ) );
        _textField.setHorizontalAlignment( JTextField.RIGHT );
        _textField.setColumns( VALUE_COLUMNS );
        TextFieldListener textFieldListener = new TextFieldListener();
        _textField.addActionListener( textFieldListener );
        _textField.addFocusListener( textFieldListener );
        
        // Units
        JLabel unitsLabel = new JLabel( OTResources.getString( "units.time" ) );
        unitsLabel.setFont( controlFont );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        layout.setInsets( OTConstants.SUB_PANEL_INSETS );
        int row = 0;
        int column = 0;
        layout.addComponent( titleLabel, row, column++ );
        layout.addComponent( _textField, row, column++ );
        layout.addComponent( unitsLabel, row, column++ );
        row++;
        column = 0;
        layout.addComponent( canvas, row, column, 3, 1 );
        
        setSimulationSpeed( clock.getDt() );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setSimulationSpeed( double value ) {
        
        // validate the value
        if ( value < _slider.getSlowRange().getMin() ) {
            value = _slider.getSlowRange().getMin();
            Toolkit.getDefaultToolkit().beep();
        }
        else if ( value > _slider.getFastRange().getMax() ) {
            value = _slider.getFastRange().getMax();
            Toolkit.getDefaultToolkit().beep();
        }
        
        // sync the slider and text field
        _slider.setValue( value );
        _textField.setValue( new Double( _slider.getValue() ) );
        
        // Don't update the clock for values between the slow and fast ranges.
        // model behavior is undefined for this range.
        if ( !_slider.isInBetween() ) {
            _clock.setDt( _slider.getValue() );
        }
    }
    
    public double getSimulationSpeed() {
        return _slider.getValue();
    }
    
    private double getTextFieldValue() {
        String text = _textField.getText();
        double value = 0;
        try {
            value = Double.parseDouble( text );
        }
        catch ( NumberFormatException nfe ) {
            Toolkit.getDefaultToolkit().beep();
            value = _clock.getDt();
            _textField.setValue( new Double( value ) );
        }
        return value;
    }
    
    /*
     * Handles events related to the textfield.
     */
    private class TextFieldListener implements ActionListener, FocusListener {

        /*
         * User pressed enter in text field.
         */
        public void actionPerformed( ActionEvent e ) {
            if ( e.getSource() == _textField ) {
                setSimulationSpeed( getTextFieldValue() );
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
                    setSimulationSpeed( getTextFieldValue() );
                }
                catch ( ParseException pe ) {
                    Toolkit.getDefaultToolkit().beep();
                    setSimulationSpeed( _slider.getValue() );
                }
            }
        }
    }

}
