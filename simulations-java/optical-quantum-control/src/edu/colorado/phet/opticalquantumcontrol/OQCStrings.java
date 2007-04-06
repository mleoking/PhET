/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticalquantumcontrol;

import edu.colorado.phet.common.view.util.PhetProjectConfig;

/**
 * OQCStrings is the collection of localized strings used in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OQCStrings {
    
    /* not intended for instantiation */
    private OQCStrings() {}
    
    private static PhetProjectConfig CONFIG = OQCConstants.CONFIG;

    public static final String MODULE_TITLE = CONFIG.getString( "OQCModule.title" );
    public static final String AMPLITUDES = CONFIG.getString( "AmplitudesView.title" );
    public static final String INPUT_PULSE = CONFIG.getString( "InputPulseView.title" );
    public static final String OUTPUT_PULSE = CONFIG.getString( "OutputPulseView.title" );
    public static final String MASK = CONFIG.getString( "Mask.label" );
    public static final String MIRROR = CONFIG.getString( "Mirror.label" );
    public static final String DIFFRACTION_GRATING = CONFIG.getString( "DiffractionGratings.label" );
    public static final String CLOSENESS_READOUT = CONFIG.getString( "closenessReadout" );
    public static final String NEW_OUTPUT_PULSE = CONFIG.getString( "newOutputPulse" );
    public static final String INSTRUCTIONS = CONFIG.getString( "instructions" );
    public static final String AMPLITUDE_ERROR_MESSAGE = CONFIG.getString( "AmplitudeErrorDialog.message" );
    public static final String WIN_DIALOG_TITLE = CONFIG.getString( "WinDialog.title" );
    public static final String WIN_DIALOG_MESSAGE = CONFIG.getString( "WinDialog.message" );
    public static final String HELP_AMPLITUDE_SLIDERS = CONFIG.getString( "Help.amplitude.sliders" );
    public static final String HELP_AMPLITUDE_TEXTFIELDS = CONFIG.getString( "Help.amplitude.textfields" );
    public static final String HELP_AMPLITUDE_RESET = CONFIG.getString( "Help.amplitude.reset" );
    public static final String HELP_NEW_PULSE = CONFIG.getString( "Help.newPulse" );
    public static final String CHEAT_DIALOG_TITLE = CONFIG.getString( "CheatDialog.title" );
    public static final String CHEAT_DIALOG_LABEL = CONFIG.getString( "CheatDialog.label" );
    public static final String MENU_CHEAT = CONFIG.getString( "HelpMenu.cheat" );
    public static final char MENU_CHEAT_MNEMONIC = CONFIG.getChar( "HelpMenu.cheat.mnemonic", 'C' );
    public static final String MENU_EXPLANATION = CONFIG.getString( "HelpMenu.explanation" );
    public static final char MENU_EXPLANATION_MNEMONIC = CONFIG.getChar( "HelpMenu.explanation.mnemonic", 'E' );
    public static final String RESET = CONFIG.getString( "reset" );
    public static final String EXPLANATION_TITLE = CONFIG.getString( "ExplanationDialog.title" );
    public static final String EXPLANATION_MIRROR = CONFIG.getString( "ExplanationDialog.mirror" );
    public static final String EXPLANATION_MASK = CONFIG.getString( "ExplanationDialog.mask" );
    public static final String EXPLANATION_GRATING = CONFIG.getString( "ExplanationDialog.grating" );
    public static final String EXPLANATION_TEXT = CONFIG.getString( "ExplanationDialog.text" );
}
