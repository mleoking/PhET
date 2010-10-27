package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.view.PeriodicTableNode;
import edu.colorado.phet.buildanatom.view.SignedIntegerFormat;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class defines a PNode that represents an interactive symbol for an
 * atom, i.e. one in which the number of protons, neutrons, and electrons can
 * be changed.  The format of the symbol is intended to look much like an
 * entry in the periodic table.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class InteractiveSymbolNode extends PNode {

    private static final Font SYMBOL_FONT = new PhetFont( 60, true );
    private static final Font NUMBER_FONT = new PhetFont( 30, true );
    private static final double WIDTH = 200;
    private static final double SPINNER_EDGE_OFFSET = 5;

    private final ValueNode protonValueNode;
    private final ValueNode massValueNode;
    private final ValueNode chargeValueNode;
    private final Property<Integer> massProperty;
    private final JSpinner chargeSpinner = new JSpinner( new SpinnerNumberModel( 0, -15, 15, 1 ) ) {
        {

            DefaultEditor numberEditor = (DefaultEditor) getEditor();
            final NumberFormatter formatter = new NumberFormatter( new SignedIntegerFormat() );
            formatter.setValueClass( Integer.class );
            numberEditor.getTextField().setFormatterFactory( new DefaultFormatterFactory( formatter ) );
            setEditor( numberEditor );
        }

        @Override
        public void setValue( Object value ) {
            super.setValue( value );

            try {
                int v = ( (Integer) value ).intValue();

                //Set the format and colors based on the value
                //Positive numbers are red and appear with a + sign
                //Negative numbers are blue with a - sign
                //Zero appears as 0 in black
                Color color;
                if ( v > 0 ) {
                    color = Color.red;
                }
                else if ( v < 0 ) {
                    color = Color.blue;
                }
                else {//v==0
                    color = Color.black;
                }
                ( (JSpinner.DefaultEditor) getEditor() ).getTextField().setForeground( color );
            }
            catch ( Exception e ) {
                System.out.println( "ignoring = " + e );
            }
        }
    };
    private final Property<Boolean> interactiveProperty;
    private final Property<Integer> protonCountProperty;
    private final Property<Integer> chargeProperty;

    public InteractiveSymbolNode( boolean interactive, final boolean showCharge ) {

        interactiveProperty = new Property<Boolean>( interactive );

        PhetPPath boundingBox = new PhetPPath( new Rectangle2D.Double( 0, 0, WIDTH, WIDTH ), Color.white,
                new BasicStroke( 3 ), Color.black );
        addChild( boundingBox );
        final PText symbol = new PText() {
            {
                setFont( SYMBOL_FONT );
            }
        };
        addChild( symbol );

        protonCountProperty = new Property<Integer>( 0 );
        protonValueNode = new ValueNode( protonCountProperty, 0, 30, 1, Color.RED, NUMBER_FONT, interactiveProperty );
        protonValueNode.setOffset( SPINNER_EDGE_OFFSET, WIDTH - protonValueNode.getFullBoundsReference().height - SPINNER_EDGE_OFFSET );
        addChild( protonValueNode );
        // Listen to the proton property value and update the symbol accordingly.
        protonCountProperty.addObserver( new SimpleObserver() {
            public void update() {
                symbol.setText( protonCountProperty.getValue() == 0 ? "-" : PeriodicTableNode.getElementAbbreviation( protonCountProperty.getValue() ) );
                symbol.setOffset( WIDTH / 2 - symbol.getFullBoundsReference().width / 2, WIDTH / 2 - symbol.getFullBoundsReference().height / 2 );
            }
        } );

        massProperty = new Property<Integer>( 0 );
        massValueNode = new ValueNode( massProperty, 0, 30, 1, Color.BLACK, NUMBER_FONT, interactiveProperty );
        massValueNode.setOffset( SPINNER_EDGE_OFFSET, SPINNER_EDGE_OFFSET );
        addChild( massValueNode );

        chargeProperty = new Property<Integer>( 0 );
        chargeValueNode = new ValueNode( chargeProperty, -20, 20, 1, Color.BLACK, NUMBER_FONT, interactiveProperty ){{
            DefaultEditor numberEditor = (DefaultEditor)getSpinnerEditor();
            final NumberFormatter formatter = new NumberFormatter( new SignedIntegerFormat() );
            formatter.setValueClass( Integer.class );
            numberEditor.getTextField().setFormatterFactory( new DefaultFormatterFactory( formatter ) );
            setSpinnerEditor( numberEditor );
            double width = Math.max( WIDTH, getFullBounds().getWidth() + massValueNode.getFullBounds().getWidth() + SPINNER_EDGE_OFFSET * 3 );
            setOffset( width - getFullBoundsReference().width - SPINNER_EDGE_OFFSET, SPINNER_EDGE_OFFSET );
        }};
        addChild( chargeValueNode );
        chargeValueNode.setVisible( showCharge );

        /*
        if ( showCharge ){
            PSwing chargeSpinnerPSwing = new PSwing( chargeSpinner );
            chargeSpinnerPSwing.scale( SPINNER_SCALE_FACTOR );
            boundingBox.setPathTo( new Rectangle2D.Double( 0, 0, width,width) );
            chargeSpinnerPSwing.setOffset(
                    width - chargeSpinnerPSwing.getFullBoundsReference().width - SPINNER_EDGE_OFFSET,
                    SPINNER_EDGE_OFFSET );
            addChild( chargeSpinnerPSwing );
        }
        */
    }
    /**
     * Determines the particle counts given the proton count, mass number and charge.
     * @return the guess
     */
    public AtomValue getGuess() {
        final int protons = protonCountProperty.getValue();
        final int massNumber = massProperty.getValue();
        final int charge = chargeProperty.getValue();
        return new AtomValue( protons, massNumber - protons, protons - charge );
    }

    public void displayAnswer( AtomValue answer ) {
        interactiveProperty.setValue( false );
        protonCountProperty.setValue( answer.getProtons() );
        massProperty.setValue( answer.getMassNumber() );
        chargeProperty.setValue( answer.getCharge() );
    }

    /**
     * This node is a combination of a spinner and a piece of text, and is
     * settable to display either one of the other.
     */
    private static class ValueNode extends PNode {
        private final JSpinner spinner;
        private final PText text = new PText( "0" );

        /**
         * Constructor.
         */
        public ValueNode( final Property<Integer> property, int minimum, int maximum, int stepSize, final Color textColor, final Font textFont, final Property<Boolean> showEditable ) {
            spinner = new JSpinner( new SpinnerNumberModel( property.getValue().intValue(), minimum, maximum, stepSize ) ) {
                {
                    setFont( textFont );
                    addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            property.setValue( (Integer) getValue() );
                        }
                    } );
                    try {
                        //Try to set the text color to red for protons, but be prepared to fail due to type unsafety
                        ( (JSpinner.DefaultEditor) getEditor() ).getTextField().setForeground( textColor );
                    }
                    catch ( Exception e ) {
                        System.out.println( "ignoring = " + e );
                    }
                }
            };

            // If the property changes external to the spinner (such as when we show the answer to a problem), the
            // text and the spinner will both need to be updated.
            property.addObserver( new SimpleObserver() {
                public void update() {
                    text.setText( property.getValue().toString() );
                    spinner.setValue( property.getValue() );
                }
            } );

            final PSwing spinnerPSwing = new PSwing( spinner );

            text.setTextPaint( textColor );
            text.setFont( textFont );
            text.setOffset( spinnerPSwing.getFullBoundsReference().getCenterX() - text.getFullBoundsReference().width / 2,
                    spinnerPSwing.getFullBoundsReference().getCenterY() - text.getFullBoundsReference().height / 2 );

            // Listen to the property that controls whether or not the
            // editable version is shown or the fixed text is shown.
            showEditable.addObserver( new SimpleObserver() {
                public void update() {
                    removeAllChildren();
                    if ( showEditable.getValue() ) {
                        addChild( spinnerPSwing );
                    }
                    else {
                        addChild( text );
                    }
                }
            } );

        }

        public void setSpinnerEditor(JComponent editor){
            spinner.setEditor( editor );
        }

        public JComponent getSpinnerEditor(){
            return spinner.getEditor();
        }
    }
}
