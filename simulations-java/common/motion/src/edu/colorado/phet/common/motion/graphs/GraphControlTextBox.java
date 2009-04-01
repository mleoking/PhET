package edu.colorado.phet.common.motion.graphs;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.colorado.phet.common.motion.model.IVariable;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Author: Sam Reid
 * Jul 20, 2007, 11:45:49 AM
 */
public class GraphControlTextBox extends JPanel {
    private JTextField textField;
    private DecimalFormat decimalFormat = new DefaultDecimalFormat( "0.00" );
    private ControlGraphSeries series;

    public GraphControlTextBox( ControlGraphSeries series ) {
        this.series = series;
        Font labelFont = new PhetFont( Font.PLAIN, 24 );//Java 1.6 renders THETA as an empty box with Lucida Sans BOLD
        add( new ShadowJLabel( series.getAbbr(), series.getColor(), labelFont ) );

        JLabel equalsSign = new JLabel( " =" );
        equalsSign.setBackground( Color.white );
        equalsSign.setFont( labelFont );
        add( equalsSign );

        textField = new JTextField( "0.0", 5 );
        textField.setFont( new PhetFont( 20,true) );
        textField.setHorizontalAlignment( JTextField.RIGHT );
        add( textField );
        setBorder( BorderFactory.createLineBorder( Color.black ) );
        series.getTemporalVariable().addListener( new IVariable.Listener() {
            public void valueChanged() {
                update();
            }
        } );
        textField.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setSimValueFromTextField();
            }
        } );
        textField.addFocusListener( new FocusAdapter() {
            public void focusLost( FocusEvent e ) {
                setSimValueFromTextField();
            }

            public void focusGained( FocusEvent e ) {
                textField.setSelectionStart( 0 );
                textField.setSelectionEnd( textField.getText().length() );
            }
        } );
        update();
    }

    protected void setSimValueFromTextField() {
        double modelValue = getModelValue();
        series.getTemporalVariable().setValue( modelValue );
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).valueChanged( modelValue );
        }
    }

    ArrayList listeners = new ArrayList();

    public static interface Listener {
        void valueChanged( double newValue );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    protected double getModelValue() {
        return getTextFieldValue();
    }

    protected double getTextFieldValue() {
        return Double.parseDouble( textField.getText() );
    }

    protected void update() {
        textField.setText( decimalFormat.format( getDisplayValue() ) );
    }

    protected double getDisplayValue() {
        return getSimVarValue();
    }

    protected double getSimVarValue() {
        return series.getTemporalVariable().getValue();
    }

    public void setEditable( boolean editable ) {
        textField.setEditable( editable );
    }

    public JTextField getTextField() {
        return textField;
    }
}
