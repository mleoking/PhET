// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.fractions.common.view.SpinnerButtonPanel;

/**
 * Node that shows a single number (numerator or denominator) and a control to change the value within a limiting range.
 *
 * @author Sam Reid
 */
public class FractionNumberControlNode extends FractionNumberNode {

    private final int MAX_DENOMINATOR = 8;

    public FractionNumberControlNode( final IntegerProperty value ) {
        super( value );
        addChild( new SpinnerButtonPanel( new VoidFunction0() {
            public void apply() {
                value.set( value.get() + 1 );
            }
        }, value.lessThan( MAX_DENOMINATOR ), new VoidFunction0() {
            public void apply() {
                value.set( value.get() - 1 );
            }
        }, value.greaterThanOrEqualTo( 2 )
        ) {{
            setOffset( biggestNumber.getFullBounds().getMinX() - getFullBounds().getWidth(), biggestNumber.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }} );
    }
}