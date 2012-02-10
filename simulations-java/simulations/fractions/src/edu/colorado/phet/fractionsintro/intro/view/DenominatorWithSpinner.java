// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.CompositeBooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.fractions.common.view.SpinnerButtonPanel;

/**
 * Node that shows a single number (numerator or denominator) and a control to change the value within a limiting range.
 *
 * @author Sam Reid
 */
public class DenominatorWithSpinner extends FractionNumberNode {

    private final int MAX_DENOMINATOR = 8;

    public DenominatorWithSpinner( final IntegerProperty numerator, final IntegerProperty denominator ) {
        super( denominator );

        //        n / d <= 6 , so n<=6d
        //or d >= n/6

        CompositeBooleanProperty decrementAllowed = new CompositeBooleanProperty( new Function0<Boolean>() {
            public Boolean apply() {
                final double newDenominator = denominator.get() - 1.0;
                final double newValue = numerator.get() / newDenominator;
                return newValue <= 6 && newDenominator > 0;
            }
        }, numerator, denominator );

        addChild( new SpinnerButtonPanel( new VoidFunction0() {
            public void apply() {
                denominator.set( denominator.get() + 1 );
            }
        }, denominator.lessThan( MAX_DENOMINATOR ), new VoidFunction0() {
            public void apply() {
                denominator.set( denominator.get() - 1 );
            }
        }, decrementAllowed
        ) {{
            setOffset( biggestNumber.getFullBounds().getMinX() - getFullBounds().getWidth(), biggestNumber.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }} );
    }
}