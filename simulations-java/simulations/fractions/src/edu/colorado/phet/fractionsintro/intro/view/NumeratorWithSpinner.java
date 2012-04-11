// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.fractions.view.SpinnerButtonPanelVBox;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.ParameterKeys;

import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components.numeratorSpinnerDownButton;
import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components.numeratorSpinnerUpButton;
import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.sendMessageAndApply;

/**
 * Node that shows a single number (numerator or denominator) and a control to change the value within a limiting range.
 *
 * @author Sam Reid
 */
public class NumeratorWithSpinner extends FractionNumberNode {
    //Allow numerator to go to 0
    private static final int MIN_VALUE = 0;

    public NumeratorWithSpinner( final IntegerProperty numerator, IntegerProperty denominator, IntegerProperty max ) {
        super( numerator );

//        Max amount of things will be MAX
//        Max numerator will be a function of the denominator
//        n / d <= MAX , so n<=MAX * d

        //Limit the numerator based on the denominator so that the entire number is less than or equal to 6
        addChild( new SpinnerButtonPanelVBox( sendMessageAndApply( numeratorSpinnerUpButton, ParameterKeys.numerator, numerator, +1 ), numerator.lessThan( denominator.times( max ) ),
                                              sendMessageAndApply( numeratorSpinnerDownButton, ParameterKeys.numerator, numerator, -1 ), numerator.greaterThanOrEqualTo( MIN_VALUE + 1 )
        ) {{
            setOffset( biggestNumber.getFullBounds().getMinX() - getFullBounds().getWidth() - DenominatorWithSpinner.OFFSET, biggestNumber.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }} );
    }
}