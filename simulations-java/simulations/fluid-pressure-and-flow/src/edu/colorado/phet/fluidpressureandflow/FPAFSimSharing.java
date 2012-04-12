// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.fluidpressureandflow.pressure.view.RichVoidFunction0;

import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager.sendUserMessage;

/**
 * Sim-sharing enums specific to this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FPAFSimSharing {

    public enum UserComponents implements IUserComponent {
        waterTowerFaucet, fluidDensityControl, flowRateMetricControl, gravityControl, flowRateEnglishControl,
        fluidPressureFaucet,
        slowMotionRadioButton,
        normalSpeedRadioButton, drainFaucet,
        squarePoolButton, trapezoidPoolButton, massesPoolButton,
        pressureSensor0,
        pressureSensor1,
        pressureSensor2,
        pressureSensor3,

        velocitySensor0,
        velocitySensor1,

        pressureTab,
        flowTab,
        rulerCheckBox, frictionCheckBox, fluxMeterCheckBox, gridCheckBox, measuringTapeCheckBox, hoseCheckBox, waterTowerTab, rulerCheckBoxIcon,
        atmosphereOnRadioButton,
        atmosphereOffRadioButton, atmospheresRadioButton,
        metricRadioButton,
        englishRadioButton,
        minimizeGravityControlButton,
        maximizeGravityControlButton,
        minimizeFluidDensityControlButton,
        maximizeFluidDensityControlButton,

        //Cross products
        textField,
        slider,

        mass1, mass2, mass3,

        gridInjectorButton
    }

    public enum ParameterKeys implements IParameterKey {
        pressure,
        droppedInDottedLineRegion,
        velocityX,
        velocityY
    }

    public enum ComponentTypes implements IUserComponentType {
        pressureSensor,
        velocitySensor
    }

    public static RichVoidFunction0 sendMessage( final IUserComponent component, final IUserComponentType type, final IUserAction action ) {
        return new RichVoidFunction0() {
            @Override public void apply() {
                sendUserMessage( component, type, action );
            }
        };
    }
}