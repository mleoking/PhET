package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

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

    private final Property<Integer> massProperty=new Property<Integer>( 0 );
    private final Property<Integer> protonCountProperty=new Property<Integer>( 0 );
    private final Property<Integer> chargeProperty=new Property<Integer>( 0 );

    private final Property<Boolean> interactiveProperty;

    public InteractiveSymbolNode( boolean interactive, final boolean showCharge ) {
        interactiveProperty = new Property<Boolean>( interactive );

        PhetPPath boundingBox = new PhetPPath( new Rectangle2D.Double( 0, 0, WIDTH, WIDTH ), Color.white,
                new BasicStroke( 3 ), Color.black );
        addChild( boundingBox );
        final PText symbol = new PText() {{
                setFont( SYMBOL_FONT );
        }};
        addChild( symbol );

        ValueNode protonValueNode = new ValueNode( protonCountProperty, 0, 30, 1, NUMBER_FONT, interactiveProperty, new Function0<Color>() {
            public Color apply() {
                return Color.red;
            }
        } );
        protonValueNode.setOffset( SPINNER_EDGE_OFFSET, WIDTH - protonValueNode.getFullBoundsReference().height - SPINNER_EDGE_OFFSET );
        addChild( protonValueNode );
        // Listen to the proton property value and update the symbol accordingly.
        protonCountProperty.addObserver( new SimpleObserver() {
            public void update() {
                symbol.setText( protonCountProperty.getValue() == 0 ? "-" : PeriodicTableNode.getElementAbbreviation( protonCountProperty.getValue() ) );
                symbol.setOffset( WIDTH / 2 - symbol.getFullBoundsReference().width / 2, WIDTH / 2 - symbol.getFullBoundsReference().height / 2 );
            }
        } );

        final ValueNode massValueNode=new ValueNode( massProperty, 0, 30, 1, NUMBER_FONT, interactiveProperty,new Function0<Color>() {public Color apply() {return Color.black;}} );
        massValueNode.setOffset( SPINNER_EDGE_OFFSET, SPINNER_EDGE_OFFSET );
        addChild( massValueNode );


        ValueNode chargeValueNode = new ValueNode( chargeProperty, -20, 20, 1, NUMBER_FONT, interactiveProperty, new Function0<Color>() {
            public Color apply() {
                //Set the color based on the value
                //Positive numbers are red and appear with a + sign
                //Negative numbers are blue with a - sign
                //Zero appears as 0 in black
                int v = chargeProperty.getValue();
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
                return color;
            }
        } ) {
            {
                setNumberFormat( new SignedIntegerFormat() );
                double width = Math.max( WIDTH, getFullBounds().getWidth() + massValueNode.getFullBounds().getWidth() + SPINNER_EDGE_OFFSET * 3 );
                setOffset( width - getFullBoundsReference().width - SPINNER_EDGE_OFFSET, SPINNER_EDGE_OFFSET );
            }
        };
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

    public static interface Function0<T> {
        T apply();
    }

    /**
     * This node is a combination of a spinner and a piece of text, and is
     * settable to display either one of the other.
     */
    private static class ValueNode extends PNode {
        private final PText text = new PText( "0" );
        private final JSpinner spinner;
        private NumberFormat numberFormat=new DecimalFormat( "0" );
        private SimpleObserver updateReadouts;

        /**
         * Constructor.
         */
        public ValueNode( final Property<Integer> numericProperty, int minimum, int maximum, int stepSize, final Font textFont, final Property<Boolean> showEditable, final Function0<Color> colorFunction) {
            spinner = new JSpinner( new SpinnerNumberModel( numericProperty.getValue().intValue(), minimum, maximum, stepSize ) ) {
                {
                    setFont( textFont );
                    addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            numericProperty.setValue( (Integer) getValue() );
                        }
                    } );
                }
            };

            // If the numericProperty changes external to the spinner (such as when we show the answer to a problem), the
            // text and the spinner will both need to be updated.
            updateReadouts = new SimpleObserver() {
                public void update() {
                    spinner.setValue( numericProperty.getValue() );
                    try {
                        //Try to set the text color to red for protons, but be prepared to fail due to type unsafety
                        ( (DefaultEditor) spinner.getEditor() ).getTextField().setForeground( colorFunction.apply() );
                    }
                    catch ( Exception e ) {
                        System.out.println( "ignoring = " + e );
                    }
                    text.setTextPaint( colorFunction.apply() );
                    text.setText( numberFormat.format( numericProperty.getValue() ) );
                }
            };
            numericProperty.addObserver( updateReadouts );

            final PSwing spinnerPSwing = new PSwing( spinner );
            text.setFont( textFont );
            text.setOffset( spinnerPSwing.getFullBoundsReference().getCenterX() - text.getFullBoundsReference().width / 2,
                    spinnerPSwing.getFullBoundsReference().getCenterY() - text.getFullBoundsReference().height / 2 );

            // Listen to the numericProperty that controls whether or not the
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

        public void setNumberFormat( NumberFormat format ) {
            this.numberFormat = format;
            DefaultEditor numberEditor = (DefaultEditor) getSpinnerEditor();
            final NumberFormatter formatter = new NumberFormatter( format );
            formatter.setValueClass( Integer.class );
            numberEditor.getTextField().setFormatterFactory( new DefaultFormatterFactory( formatter ) );
            spinner.setEditor( numberEditor );
            updateReadouts.update();
        }

        public JComponent getSpinnerEditor(){
            return spinner.getEditor();
        }
    }
}
