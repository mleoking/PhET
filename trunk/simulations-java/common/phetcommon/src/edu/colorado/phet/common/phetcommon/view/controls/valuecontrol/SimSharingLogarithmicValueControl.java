// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.view.controls.valuecontrol;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterValue;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;

import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager.sendUserMessage;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter.param;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.value;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.*;

/**
 * Logarithmic value control that sends sim-sharing messages.
 *
 * @author Sam Reid
 */
public class SimSharingLogarithmicValueControl extends LogarithmicValueControl {
    private final IUserComponent userComponent;

    public SimSharingLogarithmicValueControl( IUserComponent userComponent, double min, double max, String label, String textFieldPattern, String units ) {
        super( min, max, label, textFieldPattern, units );
        this.userComponent = userComponent;
    }

    public SimSharingLogarithmicValueControl( IUserComponent userComponent, double min, double max, String label, String textFieldPattern, String units, ILayoutStrategy layoutStrategy ) {
        super( min, max, label, textFieldPattern, units, layoutStrategy );
        this.userComponent = userComponent;
    }

    @Override protected void sliderStartDrag( double modelValue ) {
        sendUserMessage( userComponent, UserComponentTypes.slider, startDrag, param( value, modelValue ) );
        super.sliderStartDrag( modelValue );
    }

    @Override protected void sliderEndDrag( double modelValue ) {
        sendUserMessage( userComponent, UserComponentTypes.slider, endDrag, param( value, modelValue ) );
        super.sliderEndDrag( modelValue );
    }

    @Override protected void sliderDrag( double modelValue ) {
        sendUserMessage( userComponent, UserComponentTypes.slider, drag, param( value, modelValue ) );
        super.sliderDrag( modelValue );
    }

    @Override protected void textFieldCommitted( IParameterValue commitAction, double value ) {
        sendUserMessage( userComponent, UserComponentTypes.textField, textFieldCommitted, param( ParameterKeys.commitAction, commitAction ).param( ParameterKeys.value, value ) );
        super.textFieldCommitted( commitAction, value );
    }

    //TODO this should probably be a system or model message
    @Override protected void textFieldCorrected( IParameterValue errorType, String value, double correctedValue ) {
        sendUserMessage( userComponent, UserComponentTypes.textField, textFieldCorrected, param( ParameterKeys.errorType, errorType ).param( ParameterKeys.value, value ).param( ParameterKeys.correctedValue, correctedValue ) );
        super.textFieldCorrected( errorType, value, correctedValue );
    }
}
