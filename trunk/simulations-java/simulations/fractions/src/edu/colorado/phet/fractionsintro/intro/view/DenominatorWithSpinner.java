// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.CompositeBooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.fractions.view.SpinnerButtonPanelVBox;

/**
 * Node that shows a single number (numerator or denominator) and a control to change the value within a limiting range.
 *
 * @author Sam Reid
 */
public class DenominatorWithSpinner extends FractionNumberNode {

    public DenominatorWithSpinner( final IntegerProperty numerator, final IntegerProperty denominator, int maxDenominator, final IntegerProperty maxValue ) {
        super( denominator );

        //n / d <= 6 , so n<=6d
        //or d >= n/6

        CompositeBooleanProperty decrementAllowed = new CompositeBooleanProperty( new Function0<Boolean>() {
            public Boolean apply() {
                final double newDenominator = denominator.get() - 1.0;
                final double newValue = numerator.get() / newDenominator;
                return newValue <= maxValue.get() && newDenominator > 0;
            }
        }, numerator, denominator, maxValue );

        final VoidFunction0 increment = new VoidFunction0() {
            public void apply() {
                denominator.set( denominator.get() + 1 );
            }
        };
        final VoidFunction0 decrement = new VoidFunction0() {
            public void apply() {
                denominator.set( denominator.get() - 1 );
            }
        };
        addChild( new SpinnerButtonPanelVBox( increment, denominator.lessThan( maxDenominator ), decrement, decrementAllowed ) {{
            setOffset( biggestNumber.getFullBounds().getMinX() - getFullBounds().getWidth() - 5, biggestNumber.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }} );
    }
}