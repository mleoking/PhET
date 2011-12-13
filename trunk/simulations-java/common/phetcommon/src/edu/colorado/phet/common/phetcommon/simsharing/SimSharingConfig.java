// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.util.HashMap;

/**
 * Configuration information for specific sim-sharing studies.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class SimSharingConfig {

    private static final HashMap<String, SimSharingConfig> configMap = new HashMap<String, SimSharingConfig>();

    private static void addConfig( SimSharingConfig config ) {
        assert ( configMap.get( config.studyName ) == null );
        configMap.put( config.studyName, config );
    }

    static {
        addConfig( new ColoradoConfig() );
        addConfig( new UtahConfig() );
    }

    public static SimSharingConfig getConfig( String studyName ) {
        SimSharingConfig config = configMap.get( studyName );
        if ( config == null ) {
            config = new DefaultConfig();
        }
        return config;
    }

    public final String studyName;
    public final boolean hasInputDialog;
    public final String inputDialogPrompt;

    protected SimSharingConfig( String studyName, boolean hasInputDialog, String inputDialogPrompt ) {
        this.studyName = studyName;
        this.hasInputDialog = hasInputDialog;
        this.inputDialogPrompt = inputDialogPrompt;
    }

    // Default behavior, used if there is no study specified or the specified study isn't found.
    private static class DefaultConfig extends SimSharingConfig {
        public DefaultConfig() {
            this( "default" );
        }

        protected DefaultConfig( String name ) {
            super( name, false, "" );
        }
    }

    // Fall 2011 study done a CU on molecule-shapes, molecule-polarity, balancing-chemical-equations
    private static class ColoradoConfig extends SimSharingConfig {
        public ColoradoConfig() {
            super( "colorado", true, "Enter your computer number:" );
        }
    }

    // Fall 2011 study done in Utah on molecule-shapes, molecule-polarity, balancing-chemical-equations
    private static class UtahConfig extends SimSharingConfig {
        public UtahConfig() {
            super( "utah", true, "Enter your audio recorder number:" );
        }
    }
}
