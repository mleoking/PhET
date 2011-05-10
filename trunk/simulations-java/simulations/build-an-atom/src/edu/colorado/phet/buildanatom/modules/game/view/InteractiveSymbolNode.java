// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

import javax.swing.event.ChangeListener;

import edu.colorado.phet.buildanatom.model.AtomIdentifier;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.SignedIntegerFormat;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
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
    private static final double SPINNER_HEIGHT = WIDTH * 0.175;

    private final Property<Integer> massProperty;
    private final Property<Integer> protonCountProperty;
    private final Property<Integer> chargeProperty;

    // Controls the interactivity of this node, and modifications to it are
    // monitored by the node itself in order to change its appearance.
    private final Property<Boolean> interactiveProtonCountProperty;
    private final Property<Boolean> interactiveMassProperty;
    private final Property<Boolean> interactiveChargeProperty;

    /**
     * Primary constructor.
     * @param atomValue - Atom that is being portrayed.
     * @param interactiveProtonCount
     * @param interactiveMass
     * @param interactiveCharge
     */
    public InteractiveSymbolNode( ImmutableAtom atomValue, boolean interactiveProtonCount, boolean interactiveMass, boolean interactiveCharge ) {

        interactiveProtonCountProperty = new Property<Boolean>( interactiveProtonCount );
        interactiveMassProperty = new Property<Boolean>( interactiveMass );
        interactiveChargeProperty = new Property<Boolean>( interactiveCharge );

        // For each property in this node, set the value to zero if it is
        // interactive or to the correct value if it is not.
        protonCountProperty = new Property<Integer>( interactiveProtonCount ? 0 : atomValue.getNumProtons() );
        massProperty = new Property<Integer>( interactiveMass ? 0 : atomValue.getMassNumber() );
        chargeProperty = new Property<Integer>( interactiveCharge ? 0 :atomValue.getCharge() );

        final PhetPPath boundingBox = new PhetPPath( new Rectangle2D.Double( 0, 0, WIDTH, WIDTH ), Color.white,
                new BasicStroke( 3 ), Color.black );
        addChild( boundingBox );
        final PText symbol = new PText() {{
                setFont( SYMBOL_FONT );
        }};
        addChild( symbol );

        final PText elementName = new PText() {{
                setFont( ELEMENT_NAME_FONT );
                setTextPaint( PhetColorScheme.RED_COLORBLIND );
        }};
        addChild( elementName );

        ValueNode protonValueNode = new ValueNode( protonCountProperty, 0, 20, 1, interactiveProtonCountProperty, ValueNode.DEFAULT_NUMBER_FORMAT, new Function0.Constant<Color>( PhetColorScheme.RED_COLORBLIND ) );
        protonValueNode.setScale( SPINNER_HEIGHT / protonValueNode.getFullBoundsReference().height );
        protonValueNode.setOffset( SPINNER_EDGE_OFFSET, WIDTH - protonValueNode.getFullBoundsReference().height - SPINNER_EDGE_OFFSET );
        addChild( protonValueNode );

        // Listen to the proton property value and update the symbol and element name accordingly.
        protonCountProperty.addObserver( new SimpleObserver() {
            public void update() {
                symbol.setText( protonCountProperty.get() == 0 ? "-" : AtomIdentifier.getSymbol( protonCountProperty.get() ) );
                symbol.setOffset( WIDTH / 2 - symbol.getFullBoundsReference().width / 2, WIDTH / 2 - symbol.getFullBoundsReference().height / 2 );
                elementName.setText( AtomIdentifier.getName( protonCountProperty.get() ) );
                elementName.setOffset(
                        boundingBox.getFullBoundsReference().getCenterX() - elementName.getFullBoundsReference().width / 2,
                        boundingBox.getFullBoundsReference().getMaxY() + 5 );
                elementName.setVisible( protonCountProperty.get() != 0 ); // Only show up for real elements.
            }
        } );

        final ValueNode massValueNode = new ValueNode( massProperty, 0, 50, 1, interactiveMassProperty,
                ValueNode.DEFAULT_NUMBER_FORMAT, new Function0.Constant<Color>( Color.black ) );
        massValueNode.setScale( SPINNER_HEIGHT / massValueNode.getFullBoundsReference().height );
        massValueNode.setOffset( SPINNER_EDGE_OFFSET, SPINNER_EDGE_OFFSET );
        addChild( massValueNode );

        ValueNode chargeValueNode = new ValueNode( chargeProperty, -20, 20, 1, interactiveChargeProperty, new SignedIntegerFormat(), new Function0<Color>() {
                public Color apply() {
                //Set the color based on the value
                //Positive numbers are red and appear with a + sign
                //Negative numbers are blue with a - sign
                //Zero appears as 0 in black
                int v = chargeProperty.get();
                Color color;
                if ( v > 0 ) {
                    color = PhetColorScheme.RED_COLORBLIND;
                }
                else if ( v < 0 ) {
                    color = Color.blue;
                }
                else {//v==0
                    color = Color.black;
                }
                return color;
                }
                } )
        {
            {
                setScale( SPINNER_HEIGHT / getFullBoundsReference().height );
                double xOffset = WIDTH - getFullBoundsReference().width - SPINNER_EDGE_OFFSET;
                if ( xOffset < massValueNode.getFullBoundsReference().getMaxX()){
                    // Prevent overlap of the value nodes by shifting this one
                    // to the right.  Note that this could potentially push
                    // this node partially outside of the bounding box.
                    xOffset = massValueNode.getFullBoundsReference().getMaxX() + SPINNER_EDGE_OFFSET;
                }
                setOffset( xOffset, SPINNER_EDGE_OFFSET );
            }
        };
        addChild( chargeValueNode );
    }

    /**
     * Determines the particle counts given the proton count, mass number and charge.
     * @return the guess
     */
    public ImmutableAtom getGuess() {
        final int protons = protonCountProperty.get();
        final int massNumber = massProperty.get();
        final int charge = chargeProperty.get();
        return new ImmutableAtom( protons, massNumber - protons, protons - charge );
    }

    public void displayAnswer( ImmutableAtom answer ) {
        interactiveProtonCountProperty.set( false );
        interactiveMassProperty.set( false );
        interactiveChargeProperty.set( false );
        protonCountProperty.set( answer.getNumProtons() );
        massProperty.set( answer.getMassNumber() );
        chargeProperty.set( answer.getCharge() );
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
