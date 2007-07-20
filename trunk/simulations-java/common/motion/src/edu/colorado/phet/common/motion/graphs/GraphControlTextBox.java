package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.motion.model.ISimulationVariable;

import javax.swing.*;
import java.text.DecimalFormat;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Author: Sam Reid
* Jul 20, 2007, 11:45:49 AM
*/
public class GraphControlTextBox extends JPanel {
    private JTextField textField;
    private DecimalFormat decimalFormat = new DefaultDecimalFormat( "0.00" );
    private ISimulationVariable simulationVariable;

    public GraphControlTextBox( String valueAbbreviation, final ISimulationVariable simulationVariable, Color color ) {
        this.simulationVariable = simulationVariable;

        Font labelFont = new Font( "Lucida Sans", Font.BOLD, 18);
        add( new ShadowJLabel( valueAbbreviation, color, labelFont ) );

        JLabel equalsSign = new JLabel( " =" );
        equalsSign.setBackground( Color.white );
        equalsSign.setFont( labelFont );
        add( equalsSign );

        textField = new JTextField( "0.0", 6 );
        textField.setHorizontalAlignment( JTextField.RIGHT );
        add( textField );
        setBorder( BorderFactory.createLineBorder( Color.black ) );
        simulationVariable.addListener( new ISimulationVariable.Listener() {
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

    private void setSimValueFromTextField() {
        simulationVariable.setValue( getTextFieldValue() );
    }

    private double getTextFieldValue() {
        return Double.parseDouble( textField.getText() );
    }

    private void update() {
        textField.setText( decimalFormat.format( simulationVariable.getData().getValue() ) );
    }

    public void setEditable( boolean editable ) {
        textField.setEditable( editable );
    }

    public JTextField getTextField() {
        return textField;
    }
}
