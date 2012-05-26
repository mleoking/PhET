// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractions.util.RichVoidFunction1;

import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager.sendUserMessage;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet.parameterSet;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.pressed;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes.button;
import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.ParameterKeys.autoSpin;

/**
 * @author Sam Reid
 */
public class FractionsIntroSimSharing {
    public enum Components implements IUserComponent {
        numbersRadioButton,
        picturesRadioButton,
        matchingGameTab, equalityLabTab, introTab,
        maxSpinnerUpButton,
        maxSpinnerDownButton,
        numeratorSpinnerUpButton,
        numeratorSpinnerDownButton,
        denominatorSpinnerUpButton,
        denominatorSpinnerDownButton,

        sliceComponent,
        pieRadioButton,
        horizontalBarRadioButton,
        verticalBarRadioButton,
        waterGlassesRadioButton,
        numberLineRadioButton,
        cakeRadioButton,
        scaledUpFractionSpinnerRightButton,
        scaledUpFractionSpinnerLeftButton,
        lock,
        numberLineArrow, numberLineKnob,
        keepMatchButton,
        matchingGameFraction,

        tryAgainButton, showAnswerButton, checkAnswerButton,
        buildAFractionTab, menuButton
    }

    //For chaining with component types
    public static final String blue = "blue";
    public static final String green = "green";

    public enum ComponentTypes implements IUserComponentType {

        //Looks like a sprite but behaves like a check box
        spriteCheckBox
    }

    public enum ModelComponents implements IModelComponent {
        containerSetComponent
    }

    public enum ModelComponentTypes implements IModelComponentType {
        containerSetComponentType
    }

    public enum ModelActions implements IModelAction {
        changed
    }

    public enum ParameterKeys implements IParameterKey {
        max,
        numerator,
        denominator,
        containerSetKey,
        scale,
        autoSpin
    }

    public static RichVoidFunction1<Boolean> sendMessage( final IUserComponent component, final IUserComponentType type, final IUserAction action, final Function1<Boolean, ParameterSet> parameters ) {
        return new RichVoidFunction1<Boolean>() {
            public void apply( final Boolean autoSpin ) {
                sendUserMessage( component, type, action, parameters.apply( autoSpin ) );
            }
        };
    }

    public static VoidFunction1 sendMessageAndApply( Components component, ParameterKeys key, final IntegerProperty value, int delta ) {
        return sendMessage( component, button, pressed, newValue( key, value, delta ) ).andThen( value.add_( delta ) );
    }

    public static Function1<Boolean, ParameterSet> newValue( final ParameterKeys key, final IntegerProperty value, final int delta ) {
        return new Function1<Boolean, ParameterSet>() {
            public ParameterSet apply( Boolean doAutoSpin ) {
                return parameterSet( key, value.get() + delta ).with( autoSpin, doAutoSpin );
            }
        };
    }
}