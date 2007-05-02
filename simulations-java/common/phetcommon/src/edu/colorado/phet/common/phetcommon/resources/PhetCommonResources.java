/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.resources;

/**
 * PhetCommonResources is a singleton that provides access to phetcommon's JAR resources.
 */
public class PhetCommonResources {
    
    // Symbolic names for image resources
    public static final String IMAGE_CLOSE_BUTTON = "buttons/closeButton.png";
    public static final String IMAGE_MINIMIZE_BUTTON = "buttons/minimizeButton.png";
    public static final String IMAGE_MAXIMIZE_BUTTON = "buttons/maximizeButton.png";
    public static final String IMAGE_FAST_FORWARD = "clock/FastForward24.gif";
    public static final String IMAGE_PAUSE = "clock/Pause24.gif";
    public static final String IMAGE_PLAY = "clock/Play24.gif";
    public static final String IMAGE_REWIND = "clock/Rewind.gif";
    public static final String IMAGE_STEP_FORWARD = "clock/StepForward24.gif";
    public static final String IMAGE_STOP = "clock/Stop24.gif";
    
    private static PhetResources INSTANCE = PhetResources.forProject( "phetcommon" );

    /* not intended for instantiation */
    private PhetCommonResources() {}
    
    public static PhetResources getInstance() {
        return INSTANCE;
    }
}
