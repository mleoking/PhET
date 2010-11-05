package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.event.ChangeListener;

import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.view.PeriodicTableNode;
import edu.colorado.phet.buildanatom.view.SignedIntegerFormat;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

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

    private static final Font SYMBOL_FONT = new PhetFont( 90, true );
    private static final Font ELEMENT_NAME_FONT = new PhetFont( 30, false );
    private static final double WIDTH = 200;
    private static final double SPINNER_EDGE_OFFSET = 5;

    private final Property<Integer> massProperty=new Property<Integer>( 0 );
    private final Property<Integer> protonCountProperty=new Property<Integer>( 0 );
    private final Property<Integer> chargeProperty=new Property<Integer>( 0 );

    // Controls the interactivity of this node, and modifications to it are
    // monitored by the node itself in order to change its appearance.
    private final Property<Boolean> interactiveProperty;

    public InteractiveSymbolNode( boolean interactive, final boolean showCharge ) {
        interactiveProperty = new Property<Boolean>( interactive );

        final PhetPPath boundingBox = new PhetPPath( new Rectangle2D.Double( 0, 0, WIDTH, WIDTH ), Color.white,
                new BasicStroke( 3 ), Color.black );
        addChild( boundingBox );
        final PText symbol = new PText() {{
                setFont( SYMBOL_FONT );
        }};
        addChild( symbol );

        final PText elementName = new PText(){{
            setFont( ELEMENT_NAME_FONT );
            setTextPaint( Color.red );
        }};
        addChild( elementName );

        ValueNode protonValueNode = new ValueNode( protonCountProperty, 0, 20, 1, ValueNode.DEFAULT_SPINNER_FONT, ValueNode.DEFAULT_NUMBER_FONT, interactiveProperty, ValueNode.DEFAULT_NUMBER_FORMAT, new Function0.Constant<Color>(Color.red ));
        protonValueNode.setOffset( SPINNER_EDGE_OFFSET, WIDTH - protonValueNode.getFullBoundsReference().height - SPINNER_EDGE_OFFSET );
        addChild( protonValueNode );

        // Listen to the proton property value and update the symbol and element name accordingly.
        protonCountProperty.addObserver( new SimpleObserver() {
            public void update() {
                symbol.setText( protonCountProperty.getValue() == 0 ? "-" : PeriodicTableNode.getElementAbbreviation( protonCountProperty.getValue() ) );
                symbol.setOffset( WIDTH / 2 - symbol.getFullBoundsReference().width / 2, WIDTH / 2 - symbol.getFullBoundsReference().height / 2 );
                elementName.setText( Atom.getName( protonCountProperty.getValue() ) );
                elementName.setOffset(
                        boundingBox.getFullBoundsReference().getCenterX() - elementName.getFullBoundsReference().width / 2,
                        boundingBox.getFullBoundsReference().getMaxY() + 5);
                elementName.setVisible( protonCountProperty.getValue() != 0 ); // Only show up for real elements.
            }
        } );

        final ValueNode massValueNode=new ValueNode( massProperty, 0, 30, 1, ValueNode.DEFAULT_SPINNER_FONT,
                ValueNode.DEFAULT_NUMBER_FONT, interactiveProperty,ValueNode.DEFAULT_NUMBER_FORMAT, new Function0.Constant<Color>(Color.black ));
        massValueNode.setOffset( SPINNER_EDGE_OFFSET, SPINNER_EDGE_OFFSET );
        addChild( massValueNode );

        ValueNode chargeValueNode = new ValueNode( chargeProperty, -20, 20, 1, ValueNode.DEFAULT_SPINNER_FONT,
                ValueNode.DEFAULT_NUMBER_FONT, interactiveProperty, new SignedIntegerFormat(),new Function0<Color>() {
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
                double width = Math.max( WIDTH, getFullBounds().getWidth() + massValueNode.getFullBounds().getWidth() + SPINNER_EDGE_OFFSET * 3 );
                setOffset( width - getFullBoundsReference().width - SPINNER_EDGE_OFFSET, SPINNER_EDGE_OFFSET );
            }
        };
        addChild( chargeValueNode );
        chargeValueNode.setVisible( showCharge );

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

    public void addChangeListener( final ChangeListener changeListener ) {
        final SimpleObserver observer = new SimpleObserver() {
            public void update() {
                changeListener.stateChanged( null );
            }
        };
        massProperty.addObserver( observer, false );
        protonCountProperty.addObserver( observer, false );
        chargeProperty.addObserver( observer, false );
    }
}
