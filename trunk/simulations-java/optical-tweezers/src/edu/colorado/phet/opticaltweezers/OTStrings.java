/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers;

import edu.colorado.phet.common.view.ClockControlPanel;
import edu.colorado.phet.common.view.util.PhetProjectConfig;

/**
 * OTStrings contains symbolic constants for all localized strings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OTStrings {

    /* Not intended for instantiation */
    private OTStrings() {}
    
    private static final PhetProjectConfig CONFIG = OTConstants.CONFIG;
    
    public static final String ALL_CHARGES = CONFIG.getString( "label.allCharges" );
    public static final String CLEAR_BUTTON = CONFIG.getString( "button.clear" );
    public static final String CLOSE_BUTTON = CONFIG.getString( "button.close" );
    public static final String CONTROL_FLUID_FLOW = CONFIG.getString( "label.controlFluidFlow" );
    public static final String CONFIRM_RESET_ALL = CONFIG.getString( "message.confirmResetAll" );
    public static final String ERROR_TITLE = CONFIG.getString( "title.error" );
    public static final String EXCESS_CHARGES = CONFIG.getString( "label.excessCharges" );
    public static final String FAST = CONFIG.getString( "label.fast" );
    public static final String FIELDS_AND_CHARGES = CONFIG.getString( "label.fieldsAndCharges" );
    public static final String FILE_LOAD = CONFIG.getString( "menu.file.load" );
    public static final char  FILE_LOAD_MNEMONIC = CONFIG.getChar( "menu.file.load.mnemonic", 'L' );
    public static final String FILE_SAVE = CONFIG.getString( "menu.file.save" );
    public static final char  FILE_SAVE_MNEMONIC = CONFIG.getChar( "menu.file.save.mnemonic", 'S' );
    public static final String FORCES_ON_BEAD = CONFIG.getString( "label.forcesOnBead" );
    public static final String FLUID_CONTROLS_TITLE = CONFIG.getString( "title.fluidControls" );
    public static final String FLUID_SPEED = CONFIG.getString( "label.fluidSpeed" );
    public static final String FLUID_SPEED_UNITS = CONFIG.getString( "units.fluidSpeed" );
    public static final String FLUID_TEMPERATURE = CONFIG.getString( "label.fluidTemperature" );
    public static final String FLUID_TEMPERATURE_UNITS = CONFIG.getString( "units.fluidTemperature" );
    public static final String FLUID_VISCOSITY = CONFIG.getString( "label.fluidViscosity" );
    public static final String FLUID_VISCOSITY_UNITS = CONFIG.getString( "units.fluidViscosity" );
    public static final String FUN_WITH_DNA = CONFIG.getString( "title.funWithDNA" );
    public static final String HALF_BEAD = CONFIG.getString( "label.halfBead" );
    public static final String HIDE_ADVANCED = CONFIG.getString( "label.hideAdvanced" );
    public static final String HORIZONTAL_TRAP_FORCE = CONFIG.getString( "label.horizontalTrapForce" );
    public static final String LOAD_ERROR_CONTENTS = CONFIG.getString( "Load.error.contents" );
    public static final String LOAD_ERROR_DECODE = CONFIG.getString( "Load.error.decode" );
    public static final String LOAD_ERROR_MESSAGE = CONFIG.getString( "Load.error.message" );
    public static final String LOAD_TITLE = CONFIG.getString( "Load.title" );
    public static final String MEASUREMENTS = CONFIG.getString( "label.measurements" );
    public static final String MOLECULAR_MOTORS = CONFIG.getString( "title.molecularMotors" );
    public static final String OPTIONS = CONFIG.getString( "menu.options" );
    public static final char OPTIONS_MNEMONIC = CONFIG.getChar( "menu.options.mnemonic", 'O' );
    public static final String PHYSICS_OF_TWEEZERS = CONFIG.getString( "title.physicsOfTweezers" );
    public static final String POSITION_AXIS = CONFIG.getString( "axis.position" );
    public static final String POSITION_HISTOGRAM = CONFIG.getString( "title.positionHistogram" );
    public static final String POSITION_UNITS = CONFIG.getString( "units.position" );
    public static final String POTENTIAL_ENERGY_AXIS = CONFIG.getString( "axis.potentialEnergy" );
    public static final String POTENTIAL_ENERGY_CHART_TITLE = CONFIG.getString( "title.potentialEnergyChart" );
    public static final String POWER = CONFIG.getString( "label.power" );
    public static final String POWER_UNITS = CONFIG.getString( "units.power" );
    public static final String RESET_ALL = CONFIG.getString( "button.resetAll" );
    public static final String RESTART = CONFIG.getString( "button.restart" );
    public static final String RETURN_BEAD = CONFIG.getString( "button.returnBead" );
    public static final String SAVE_CONFIRM_MESSAGE = CONFIG.getString( "Save.confirm.message" );
    public static final String SAVE_ERROR_ENCODE = CONFIG.getString( "Save.error.encode" );
    public static final String SAVE_ERROR_MESSAGE = CONFIG.getString( "Save.error.message" );
    public static final String SAVE_TITLE = CONFIG.getString( "Save.title" );
    public static final String SHOW_ADVANCED = CONFIG.getString( "label.showAdvanced" );
    public static final String SHOW_BEAD_CHARGES = CONFIG.getString( "label.showBeadCharges" );
    public static final String SHOW_BROWNIAN_FORCE = CONFIG.getString( "label.showBrownianForce" );
    public static final String SHOW_ELECTRIC_FIELD = CONFIG.getString( "label.showElectricField" );
    public static final String SHOW_FLUID_DRAG = CONFIG.getString( "label.showFluidDrag" );
    public static final String SHOW_MOMENTUM_CHANGE = CONFIG.getString( "label.showMomentumChange" );
    public static final String SHOW_POSITION_HISTOGRAM = CONFIG.getString( "label.showPositionHistogram" );
    public static final String SHOW_POTENTIAL_ENERGY_CHART = CONFIG.getString( "label.showPotentialEnergyChart" );
    public static final String SHOW_RULER = CONFIG.getString( "label.showRuler" );
    public static final String SHOW_TRAP_FORCE = CONFIG.getString( "label.showTrapForce" );
    public static final String SIMULATION_SPEED = CONFIG.getString( "label.simulationSpeed" );
    public static final String SLOW = CONFIG.getString( "label.slow" );
    public static final String START_LASER = CONFIG.getString( "label.startLaser" );
    public static final String STOP_LASER = CONFIG.getString( "label.stopLaser" );
    public static final String START_BUTTON = CONFIG.getString( "button.start" );
    public static final String STOP_BUTTON = CONFIG.getString( "button.stop" );
    public static final String TIME_UNITS = CONFIG.getString( "units.time" );
    public static final String VERSION = CONFIG.getVersion().toString();
    public static final String WHOLE_BEAD = CONFIG.getString( "label.wholeBead" );
    public static final String WIGGLE_ME = CONFIG.getString( "label.wiggleMe" );
    
    //XXX from phetcommon
    public static final String PAUSE = CONFIG.getString( ClockControlPanel.PROPERTY_PAUSE );
    public static final String PLAY = CONFIG.getString( ClockControlPanel.PROPERTY_PLAY );
    public static final String STEP = CONFIG.getString( ClockControlPanel.PROPERTY_STEP );
}
