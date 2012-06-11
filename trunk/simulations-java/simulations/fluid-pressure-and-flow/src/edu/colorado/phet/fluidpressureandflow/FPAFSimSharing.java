// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.fluidpressureandflow.pressure.view.CompositeVoidFunction0;

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
        measuringTapeCheckBoxIcon,
        hoseCheckBoxIcon,
        atmosphereOnRadioButton,
        atmosphereOffRadioButton, atmospheresRadioButton,
        metricRadioButton,
        englishRadioButton,
        minimizeGravityControlButton,
        maximizeGravityControlButton,
        minimizeFluidDensityControlButton,
        maximizeFluidDensityControlButton,
        mass1, mass2, mass3,
        gridInjectorButton,
        waterTowerHandle,

        //Cross products
        textField,
        slider,
        top, bottom,

        rightPipeHandle,
        leftPipeHandle,
        pipeCrossSectionHandle1,
        pipeCrossSectionHandle2,
        pipeCrossSectionHandle3,
        pipeCrossSectionHandle4,
        pipeCrossSectionHandle5,
        pipeCrossSectionHandle6,
        pipeCrossSectionHandle7,

        fluxMeterPanel, fluxMeterHoop,
        faucetManualRadioButton, faucetMatchLeakageRadioButton,
        fillButton,
        waterTowerDoor,
        hoseNozzle,
        hoseHandle
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

    public static CompositeVoidFunction0 sendMessage( final IUserComponent component, final IUserComponentType type, final IUserAction action ) {
        return new CompositeVoidFunction0() {
            public void apply() {
                sendUserMessage( component, type, action );
            }
        };
    }
}