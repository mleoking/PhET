package edu.colorado.phet.acidbasesolutions.control;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Concentration control, slider and editable text field.
 * The slider is consider to be the ultimate source of the value.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationControlNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double X_SPACING = 3;
    
    private static final Font TEXTFIELD_FONT = new PhetFont( 14 );
    private static final String TEXTFIELD_PATTERN = "0.0000";
    private static final double DELTA = 0.0001;
    private static final DecimalFormat TEXTFIELD_FORMAT = new DecimalFormat( TEXTFIELD_PATTERN );
    private static final int TEXTFIELD_COLUMNS = TEXTFIELD_PATTERN.length() - 1;
    
    private static final Font UNITS_FONT = new PhetFont( 12 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ConcentrationSliderNode sliderNode;
    private final JFormattedTextField textField;
    private final ArrayList<ChangeListener> changeListeners;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ConcentrationControlNode( DoubleRange range ) {
        this( range.getMin(), range.getMax() );
    }
    
    public ConcentrationControlNode( double min, double max ) {
        super();
        
        changeListeners = new ArrayList<ChangeListener>();
        
        sliderNode = new ConcentrationSliderNode( min, max );
        addChild( sliderNode );
        sliderNode.addChangeListener( new SliderListener() );
        
        textField = new JFormattedTextField( TEXTFIELD_FORMAT );
        textField.setFont( TEXTFIELD_FONT );
        textField.setValue( new Double( sliderNode.getValue() ) );
        textField.setHorizontalAlignment( JTextField.RIGHT );
        textField.setColumns( TEXTFIELD_COLUMNS );
        TextFieldListener textFieldListener = new TextFieldListener();
        textField.addActionListener( textFieldListener );
        textField.addFocusListener( textFieldListener );
        textField.addKeyListener( textFieldListener );
        
        JLabel unitsLabel = new JLabel( ABSStrings.UNITS_MOLES_PER_LITER );
        unitsLabel.setFont( UNITS_FONT );
        
        JPanel panel = new JPanel();
        panel.setOpaque( false );
        panel.add( textField );
        panel.add( unitsLabel );
        
        PSwing panelWrapper = new PSwing( panel );
        addChild( panelWrapper );
        
        // slider
        double xOffset = 0;
        double yOffset = 0;
        sliderNode.setOffset( xOffset, yOffset );
        // text field to the right of slider, vertically centered
        xOffset = sliderNode.getFullBoundsReference().getMaxX() + X_SPACING;
        yOffset = sliderNode.getFullBoundsReference().getCenterY() - ( panelWrapper.getFullBoundsReference().getHeight() / 2 );
        panelWrapper.setOffset( xOffset, yOffset );
    }
    
    public void setEnabled( boolean enabled ) {
        sliderNode.setEnabled( enabled );
        textField.setEditable( enabled );
    }
    
    public static NumberFormat getFormat() {
        return TEXTFIELD_FORMAT;
    }
    
    public void setValue( double value ) {
        if ( value != sliderNode.getValue() ) {
            sliderNode.setValue( value );
        }
    }
    
    public double getValue() {
        return sliderNode.getValue();
    }
    
    public double getMin() {
        return sliderNode.getMin();
    }
    
    public double getMax() {
        return sliderNode.getMax();
    }
    
    public void setSliderVisible( boolean visible ) {
        sliderNode.setVisible( visible );
    }
    
    private double getTextFieldValue() {
        String text = textField.getText();
        double value = 0;
        try {
            value = Double.parseDouble( text );
        }
        catch ( NumberFormatException nfe ) {
            revertTextField();
        }
        return value;
    }
    
    public void addChangeListener( ChangeListener listener ) {
        changeListeners.add( listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        changeListeners.remove( listener );
    }
    
    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        Iterator<ChangeListener> i = changeListeners.iterator();
        while ( i.hasNext() ) {
            i.next().stateChanged( event );
        }
    }
    
    private void revertTextField() {
        Toolkit.getDefaultToolkit().beep();
        textField.setValue( new Double( sliderNode.getValue() ) );
    }
    
    /*
     * Handles events related to the slider.
     */
    private class SliderListener implements ChangeListener {
        // slider changed, update the text field
        public void stateChanged( ChangeEvent e ) {
            textField.setValue( new Double( sliderNode.getValue() ) );
            fireStateChanged();
        } 
    }

    /*
     * Handles events related to the text field.
     */
    private class TextFieldListener extends KeyAdapter implements ActionListener, FocusListener {
        
        // Use the up/down arrow keys to change the value.
        public void keyPressed( KeyEvent e ) {
            if ( e.getSource() == textField ) {
                if ( e.getKeyCode() == KeyEvent.VK_UP ) {
                    double concentration = getValue() + DELTA;
                    if ( concentration <= getMax() ) {
                        setValue( concentration );
                    }
                }
                else if ( e.getKeyCode() == KeyEvent.VK_DOWN ) {
                    double concentration = getValue() - DELTA;
                    if ( concentration >= getMin() ) {
                        setValue( concentration );
                    }
                }
            }
        }

        // User pressed enter in text field, update the slider.
        public void actionPerformed( ActionEvent e ) {
            if ( e.getSource() == textField ) {
                sync();
            }
        }

        // Selects the entire text field when it gains focus.
        public void focusGained( FocusEvent e ) {
            if ( e.getSource() == textField ) {
                textField.selectAll();
            }
        }

        // When the text field loses focus, commit the value and update the slider.
        public void focusLost( FocusEvent e ) {
            if ( e.getSource() == textField ) {
                try {
                    textField.commitEdit(); // throws ParseException
                    sync();
                }
                catch ( ParseException pe ) {
                    revertTextField();
                }
            }
        }
        
        // updates the slider to match the text field
        private void sync() {
            double value = getTextFieldValue();
            if ( value >= getMin() && value <= getMax() ) {
                setValue( value );
            }
            else {
                revertTextField();
            }
        }
    }
}
