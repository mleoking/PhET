// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.dev;

import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.energyformsandchanges.intro.model.HeatTransferConstants;

/**
 * Developer control dialog used for experimenting with heat transfer values.
 *
 * @author John Blanco
 */
public class HeatTransferValuesDialog extends PaintImmediateDialog {


    public HeatTransferValuesDialog( Frame frame ) {
        super( frame );

        setTitle( "Heat Transfer Values" );
        setLayout( new GridLayout( 8, 2 ) );

        // Create a list of the parameters that can be changes.
        final List<ParameterEntryField> parameterEntryFields = new ArrayList<ParameterEntryField>() {{
            add( new ParameterEntryField( "Brick-Iron:", HeatTransferConstants.BRICK_IRON_HEAT_TRANSFER_FACTOR ) );
            add( new ParameterEntryField( "Brick-Water:", HeatTransferConstants.BRICK_WATER_HEAT_TRANSFER_FACTOR ) );
            add( new ParameterEntryField( "Brick-Air:", HeatTransferConstants.BRICK_AIR_HEAT_TRANSFER_FACTOR ) );
            add( new ParameterEntryField( "Iron-Water:", HeatTransferConstants.IRON_WATER_HEAT_TRANSFER_FACTOR ) );
            add( new ParameterEntryField( "Iron-Air:", HeatTransferConstants.IRON_AIR_HEAT_TRANSFER_FACTOR ) );
            add( new ParameterEntryField( "Water-Air:", HeatTransferConstants.WATER_AIR_HEAT_TRANSFER_FACTOR ) );
            add( new ParameterEntryField( "Air-Surrounding Air:", HeatTransferConstants.AIR_TO_SURROUNDING_AIR_HEAT_TRANSFER_FACTOR ) );
        }};

        // Add the parameters to the dialog.
        for ( ParameterEntryField parameterEntryField : parameterEntryFields ) {
            add( parameterEntryField );
        }

        // Add a button for resetting to default values.
        final JButton resetButton = new JButton( "Reset" );
        resetButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                for ( ParameterEntryField parameterEntryField : parameterEntryFields ) {
                    parameterEntryField.getProperty().reset();
                }
            }
        } );
        add( resetButton );

        pack();
    }

    private void add( ParameterEntryField parameterEntryField ) {
        add( parameterEntryField.getLabel() );
        add( parameterEntryField.getTextField() );
    }

    // Convenience class for linking parameter entry to a property.
    private static class ParameterEntryField {

        private static final Font LABEL_FONT = new PhetFont( 14 );
        private static final Format FORMATTER = new DecimalFormat( "###.0#" );

        private final JLabel label;
        private final JTextField textField;

        private final Property<Double> property;

        public ParameterEntryField( String labelText, final Property<Double> property ) {
            label = new JLabel( labelText );
            label.setFont( LABEL_FONT );
            this.property = property;
            textField = new JTextField();
            textField.setFont( LABEL_FONT );
            property.addObserver( new VoidFunction1<Double>() {
                public void apply( Double value ) {
                    textField.setText( FORMATTER.format( value ) );
                }
            } );
            textField.setText( FORMATTER.format( property.get() ) );
            textField.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    System.out.println( "Action Performed in HeatTransferValuesDialog" );
                    property.set( Double.parseDouble( textField.getText() ) );
                }
            } );
        }

        protected JLabel getLabel() {
            return label;
        }

        protected JTextField getTextField() {
            return textField;
        }

        public Property<Double> getProperty() {
            return property;
        }
    }
}
