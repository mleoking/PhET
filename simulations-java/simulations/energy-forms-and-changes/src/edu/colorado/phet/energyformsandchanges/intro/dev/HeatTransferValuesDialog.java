// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.dev;

import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Developer control dialog used for experimenting with heat transfer values.
 *
 * @author John Blanco
 */
public class HeatTransferValuesDialog extends PaintImmediateDialog {

    private ParameterEntry brickIronValue = new ParameterEntry( "Brick-Iron:" );

    public HeatTransferValuesDialog( Frame frame ) {

        setTitle( "Heat Transfer Values" );
        setLayout( new GridLayout( 1, 2 ) );

        add( brickIronValue.getLabel() );
        add( brickIronValue.getField() );

        pack();
    }

    /**
     * A class used to simplify the creation of and interaction with the
     * parameter data.
     */
    private static class ParameterEntry {

        private static final Font LABEL_FONT = new PhetFont( 14 );

        private final JLabel label;
        private final JTextField field;

        public ParameterEntry( String label ) {
            this.label = new JLabel( label );
            this.label.setFont( LABEL_FONT );
            this.field = new JTextField();
            this.field.setFont( LABEL_FONT );
        }

        protected JLabel getLabel() {
            return label;
        }

        protected JTextField getField() {
            return field;
        }

        protected void setValueText( String valueText ) {
            field.setText( valueText );
        }

        protected double getValue() {
            return Double.parseDouble( field.getText() );
        }
    }
}
