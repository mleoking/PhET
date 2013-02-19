// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;

/**
 * This class is where all the user components, actions, and such are defined
 * for the sim, which are necessary for the "sim sharing" (a.k.a. data
 * collection) feature.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class CCKSimSharing {
    public static enum UserComponents implements IUserComponent {
        unspecifiedBranch, battery, wire, resistor, grabBagResistor, lightBulb,
        circuitSwitch, inductor, acVoltageSource, capacitor, voltmeterCheckBox,
        nonContactAmmeterCheckBox, seriesAmmeterCheckBox, voltmeter, seriesAmmeter,
        loadButton, lifelikeRadioButton, schematicRadioButton, showReadoutCheckBox,
        smallRadioButton, mediumRadioButton, largeRadioButton, showAdvancedControlsButton,
        hideAdvancedControlsButton, hideElectronsCheckBox, resistivitySlider,
        resistorEditor, bulbResistorEditor, voltageEditor, moreVoltsCheckBox,
        batteryResistanceEditor, acVoltageSourceEditor, inductorEditor, capacitorEditor,
        frequencyEditor, saveButton
    }

    public static enum UserComponentType implements IUserComponentType {
        editor
    }

    public static enum UserActions implements IUserAction {
        addedComponent, movedComponent, removedComponent, connectionFormed
    }

    public static enum ModelComponents implements IModelComponent {
        circuit, voltmeterModel, voltmeterRedLeadModel, voltmeterBlackLeadModel,
        nonContactAmmeterModel, seriesAmmeterModel;
    }

    public static enum ModelComponentTypes implements IModelComponentType {
        connection
    }

    public static enum ModelActions implements IModelAction {
        connectionFormed, measuredVoltageChanged, measuredCurrentChanged,
        connectionBroken, fireStarted, fireEnded
    }

    public static enum ParameterKeys implements IParameterKey {
        component
    }
}
