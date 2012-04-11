// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function0.Constant;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.fractions.util.RichVoidFunction0;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.pressed;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes.button;

/**
 * @author Sam Reid
 */
public class FractionsIntroSimSharing {
    public enum Components implements IUserComponent {
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
        cakeRadioButton
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
    }

    public static RichVoidFunction0 sendMessage( final IUserComponent component, final IUserComponentType type, final IUserAction action ) {
        return sendMessage( component, type, action, new Constant<ParameterSet>( new ParameterSet() ) );
    }

    public static RichVoidFunction0 sendMessage( final IUserComponent component, final IUserComponentType type, final IUserAction action, final Function0<ParameterSet> parameters ) {
        return new RichVoidFunction0() {
            @Override public void apply() {
                SimSharingManager.sendUserMessage( component, type, action, parameters.apply() );
            }
        };
    }

    public static VoidFunction0 sendMessageAndApply( Components component, ParameterKeys key, final IntegerProperty value, int delta ) {
        return sendMessage( component, button, pressed, newValue( value, delta, key ) ).andThen( value.add_( delta ) );
    }

    public static Function0<ParameterSet> newValue( final IntegerProperty value, final int delta, final ParameterKeys key ) {
        return new Function0<ParameterSet>() {
            @Override public ParameterSet apply() {
                return ParameterSet.parameterSet( key, value.get() + delta );
            }
        };
    }
}