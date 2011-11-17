// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.kit.BackButton;
import edu.colorado.phet.common.piccolophet.nodes.kit.ForwardButton;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;

/**
 * Node that shows a single number (numerator or denominator) and a control to change the value within a limiting range.
 *
 * @author Sam Reid
 */
public class FractionNumberControlNode extends FractionNumberNode {

    public FractionNumberControlNode( final Property<Integer> value ) {
        super( value );
        final BackButton backButton = new BackButton( Color.orange ) {{
            setEnabled( true );
            rotate( Math.PI * 3 / 2 );
            scale( 2.0 );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    value.set( value.get() - 1 );
                }
            } );
            value.addObserver( new VoidFunction1<Integer>() {
                public void apply( Integer integer ) {
                    setEnabled( integer > 1 );
                }
            } );
        }};
        final ForwardButton forwardButton = new ForwardButton( Color.orange ) {{
            setEnabled( true );
            rotate( Math.PI * 3 / 2 );
            scale( 2.0 );

            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    value.set( value.get() + 1 );
                }
            } );

            value.addObserver( new VoidFunction1<Integer>() {
                public void apply( Integer integer ) {
                    setEnabled( integer <= 11 );
                }
            } );
        }};
        addChild( new VBox( 0, forwardButton, backButton ) {{
            setOffset( biggestNumber.getFullBounds().getMinX() - getFullBounds().getWidth(), biggestNumber.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }} );
    }
}