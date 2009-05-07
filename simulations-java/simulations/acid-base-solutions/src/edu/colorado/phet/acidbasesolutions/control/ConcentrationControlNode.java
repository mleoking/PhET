package edu.colorado.phet.acidbasesolutions.control;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
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
    
    //TODO localize
    private static final String UNITS_MOLES_PER_LITER = "mol/L";
    private static final String CONCENTRATION = "Concentration:";
    
    private static final int DEFAULT_FONT_SIZE = 12;
    private static final double X_SPACING = 3;
    private static final double Y_SPACING = 3;
    
    private static final Font LABEL_FONT = new PhetFont( DEFAULT_FONT_SIZE + 2 );
    
    private static final Font TEXTFIELD_FONT = new PhetFont( DEFAULT_FONT_SIZE );
    private static final String TEXTFIELD_PATTERN = "0.000";
    private static final DecimalFormat TEXTFIELD_FORMAT = new DecimalFormat( TEXTFIELD_PATTERN );
    private static final int TEXTFIELD_COLUMNS = TEXTFIELD_PATTERN.length();
    
    private static final Font UNITS_FONT = new PhetFont( DEFAULT_FONT_SIZE );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ConcentrationSliderNode sliderNode;
    private final JFormattedTextField textField;
    private final ArrayList changeListeners;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ConcentrationControlNode( double min, double max ) {
        super();
        
        changeListeners = new ArrayList();
        
        JLabel concentrationLabel = new JLabel( CONCENTRATION );
        concentrationLabel.setFont( LABEL_FONT );
        PSwing concentrationLabelWrapper = new PSwing( concentrationLabel );
        addChild( concentrationLabelWrapper );
        
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
        
        JLabel unitsLabel = new JLabel( UNITS_MOLES_PER_LITER );
        unitsLabel.setFont( UNITS_FONT );
        
        JPanel panel = new JPanel();
        panel.setOpaque( false );
        panel.add( textField );
        panel.add( unitsLabel );
        
        PSwing panelWrapper = new PSwing( panel );
        addChild( panelWrapper );
        
        // layout
        concentrationLabelWrapper.setOffset( 0, 0 );
        // slider below label, left justified
        double xOffset = concentrationLabelWrapper.getXOffset();
        double yOffset = concentrationLabelWrapper.getFullBoundsReference().getMaxY() + Y_SPACING - ( sliderNode.getFullBoundsReference().getY() - sliderNode.getYOffset() );
        sliderNode.setOffset( xOffset, yOffset );
        // text field to the right of slider, vertically centered
        xOffset = sliderNode.getFullBoundsReference().getMaxX() + X_SPACING;
        yOffset = sliderNode.getFullBoundsReference().getCenterY() - ( panelWrapper.getFullBoundsReference().getHeight() / 2 );
        panelWrapper.setOffset( xOffset, yOffset );
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
        Iterator i = changeListeners.iterator();
        while ( i.hasNext() ) {
            ( (ChangeListener) i.next() ).stateChanged( event );
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
    private class TextFieldListener implements ActionListener, FocusListener {

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

    // test
    public static void main( String[] args ) {

        Dimension canvasSize = new Dimension( 600, 300 );
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( canvasSize );

        final ConcentrationControlNode controlNode = new ConcentrationControlNode( 1E-3, 1 );
        canvas.getLayer().addChild( controlNode );
        controlNode.setOffset( 100, 100 );
        controlNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                System.out.println( "stateChanged value=" + controlNode.getValue() );
            }
        });

        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( canvas, BorderLayout.CENTER );

        JFrame frame = new JFrame();
        frame.setContentPane( panel );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
