/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticalquantumcontrol;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * OQCResources is the collection of JAR file resources used in this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OQCResources {
    
    /* not intended for instantiation */
    private OQCResources() {}
    
    private static PhetResources RESOURCES = new PhetResources( OQCConstants.PROJECT_NAME );
    
    public static PhetResources getResourceLoader() {
        return RESOURCES;
    }
    
    public static BufferedImage getImage( String name ) {
        return RESOURCES.getImage( name );
    }
    
    // Numbers
    public static final int EXPLANATION_FONT_SIZE = RESOURCES.getLocalizedInt( "ExplanationDialog.fontSize", 12 );
    
    // Images
    public static final BufferedImage CLOSE_BUTTON_IMAGE = RESOURCES.getImage( "closeButton.png" );
    public static final BufferedImage EXPLANATION_IMAGE = RESOURCES.getImage( "explanation.jpg" );
    public static final BufferedImage KABOOM_IMAGE = RESOURCES.getImage( "kaboom.png" );
    public static final BufferedImage MAGNIFYING_GLASS_IMAGE = RESOURCES.getImage( "magnifyingGlass.png" );

    // Strings
    public static final String MODULE_TITLE = RESOURCES.getLocalizedString( "OQCModule.title" );
    public static final String AMPLITUDES = RESOURCES.getLocalizedString( "AmplitudesView.title" );
    public static final String INPUT_PULSE = RESOURCES.getLocalizedString( "InputPulseView.title" );
    public static final String OUTPUT_PULSE = RESOURCES.getLocalizedString( "OutputPulseView.title" );
    public static final String MASK = RESOURCES.getLocalizedString( "Mask.label" );
    public static final String MIRROR = RESOURCES.getLocalizedString( "Mirror.label" );
    public static final String DIFFRACTION_GRATING = RESOURCES.getLocalizedString( "DiffractionGratings.label" );
    public static final String CLOSENESS_READOUT = RESOURCES.getLocalizedString( "closenessReadout" );
    public static final String NEW_OUTPUT_PULSE = RESOURCES.getLocalizedString( "newOutputPulse" );
    public static final String INSTRUCTIONS = RESOURCES.getLocalizedString( "instructions" );
    public static final String AMPLITUDE_ERROR_MESSAGE = RESOURCES.getLocalizedString( "AmplitudeErrorDialog.message" );
    public static final String WIN_DIALOG_TITLE = RESOURCES.getLocalizedString( "WinDialog.title" );
    public static final String WIN_DIALOG_MESSAGE = RESOURCES.getLocalizedString( "WinDialog.message" );
    public static final String HELP_AMPLITUDE_SLIDERS = RESOURCES.getLocalizedString( "Help.amplitude.sliders" );
    public static final String HELP_AMPLITUDE_TEXTFIELDS = RESOURCES.getLocalizedString( "Help.amplitude.textfields" );
    public static final String HELP_AMPLITUDE_RESET = RESOURCES.getLocalizedString( "Help.amplitude.reset" );
    public static final String HELP_NEW_PULSE = RESOURCES.getLocalizedString( "Help.newPulse" );
    public static final String CHEAT_DIALOG_TITLE = RESOURCES.getLocalizedString( "CheatDialog.title" );
    public static final String CHEAT_DIALOG_LABEL = RESOURCES.getLocalizedString( "CheatDialog.label" );
    public static final String MENU_CHEAT = RESOURCES.getLocalizedString( "HelpMenu.cheat" );
    public static final char MENU_CHEAT_MNEMONIC = RESOURCES.getLocalizedChar( "HelpMenu.cheat.mnemonic", 'C' );
    public static final String MENU_EXPLANATION = RESOURCES.getLocalizedString( "HelpMenu.explanation" );
    public static final char MENU_EXPLANATION_MNEMONIC = RESOURCES.getLocalizedChar( "HelpMenu.explanation.mnemonic", 'E' );
    public static final String RESET = RESOURCES.getLocalizedString( "reset" );
    public static final String EXPLANATION_TITLE = RESOURCES.getLocalizedString( "ExplanationDialog.title" );
    public static final String EXPLANATION_MIRROR = RESOURCES.getLocalizedString( "ExplanationDialog.mirror" );
    public static final String EXPLANATION_MASK = RESOURCES.getLocalizedString( "ExplanationDialog.mask" );
    public static final String EXPLANATION_GRATING = RESOURCES.getLocalizedString( "ExplanationDialog.grating" );
    public static final String EXPLANATION_TEXT = RESOURCES.getLocalizedString( "ExplanationDialog.text" );
    public static final String PULSE_X_AXIS_LABEL = RESOURCES.getLocalizedString( "pulse.xAxis" );
    public static final String AMPLITUDE_X_AXIS_LABEL = RESOURCES.getLocalizedString( "amplitude.xAxis" );
}
