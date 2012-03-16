// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.membranechannels;

import static edu.colorado.phet.membranechannels.MembraneChannelsResources.getString;

/**
 * A collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 */
public class MembraneChannelsStrings {

    /* not intended for instantiation */
    private MembraneChannelsStrings() {}

    public static final String TITLE_MEMBRANE_CHANNELS_MODULE = getString( "ModuleTitle.MembraneChannelsModule" );
    public static final String SHOW_CONCENTRATIONS = getString( "controlPanel.showConcentrations" );
    public static final String CLOSE = getString( "controlPanel.close" );
    public static final String OPEN = getString( "controlPanel.open" );
    public static final String CHANNELS = getString( "controlPanel.channels" );
    public static final String CLEAR_PARTICLES = getString( "controlPanel.clearParticles" );
    public static final String GATED_CHANNELS = getString( "toolBox.gatedChannels" );
    public static final String LEAKAGE_CHANNELS = getString( "toolBox.leakageChannels" );
    public static final String MEMBRANE = getString( "playArea.membrane" );
}
