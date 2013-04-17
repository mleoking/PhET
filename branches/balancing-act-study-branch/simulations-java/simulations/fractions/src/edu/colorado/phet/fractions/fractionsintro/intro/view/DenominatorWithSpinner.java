// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.CompositeBooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.fractions.common.view.SpinnerButtonPanelVBox;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.ParameterKeys;

import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components.denominatorSpinnerDownButton;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components.denominatorSpinnerUpButton;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.sendMessageAndApply;

/**
 * Node that shows a single number (numerator or denominator) and a control to change the value within a limiting range.
 *
 * @author Sam Reid
 */
class DenominatorWithSpinner extends FractionNumberNode {

    public static final int OFFSET = 5;

    @SuppressWarnings("unchecked") public DenominatorWithSpinner( final IntegerProperty numerator, final IntegerProperty denominator, int maxDenominator, final IntegerProperty maxValue ) {
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

        addChild( new SpinnerButtonPanelVBox( sendMessageAndApply( denominatorSpinnerUpButton, ParameterKeys.denominator, denominator, +1 ), denominator.lessThan( maxDenominator ), //unchecked warning
                                              sendMessageAndApply( denominatorSpinnerDownButton, ParameterKeys.denominator, denominator, -1 ), decrementAllowed ) {{ //unchecked warning
            setOffset( biggestNumber.getFullBounds().getMinX() - getFullBounds().getWidth() - OFFSET, biggestNumber.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }} );
    }
}