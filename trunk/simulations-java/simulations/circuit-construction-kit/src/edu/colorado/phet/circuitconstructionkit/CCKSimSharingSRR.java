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
 * Components created by SRR to be merged with main file after complete (to avoid too much merging)
 */
public class CCKSimSharingSRR {
    public static enum UserComponents implements IUserComponent {
        loadButton, lifelikeRadioButton, schematicRadioButton, showReadoutCheckBox, smallRadioButton, mediumRadioButton, largeRadioButton, showAdvancedControlsButton, hideAdvancedControlsButton, hideElectronsCheckBox, resistivitySlider, resistorEditor, bulbResistorEditor, voltageEditor, moreVoltsCheckBox, batteryResistanceEditor, acVoltageSourceEditor, inductorEditor, capacitorEditor, frequencyEditor, saveButton
    }

    public static enum UserComponentType implements IUserComponentType {
        editor
    }

    public static enum ParameterKeys implements IParameterKey {
        component
    }

    public static enum UserActions implements IUserAction {
    }

    public static enum ModelComponents implements IModelComponent {
    }

    public static enum ModelComponentTypes implements IModelComponentType {
    }

    public static enum ModelActions implements IModelAction {
        fireStarted, fireEnded
    }
}
