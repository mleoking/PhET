// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.util.HashMap;

/**
 * Configuration information for specific sim-sharing studies.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class SimSharingConfig {

    private static final HashMap<String, SimSharingConfig> CONFIG_MAP = new HashMap<String, SimSharingConfig>();

    private static void addConfig( SimSharingConfig config ) {
        assert ( CONFIG_MAP.get( config.studyName ) == null );
        CONFIG_MAP.put( config.studyName, config );
    }

    static {
        addConfig( new ColoradoConfig() );
        addConfig( new UtahConfig() );
    }

    public static SimSharingConfig getConfig( String studyName ) {
        SimSharingConfig config = CONFIG_MAP.get( studyName );
        if ( config == null ) {
            config = new DefaultConfig();
        }
        return config;
    }

    public final String studyName; // optional study name, as identified on via program args
    public final boolean requestId; // does the study request that students provide an id?
    public final String idPrompt; // prompt used to request student's id (irrelevant if requestId is false)

    protected SimSharingConfig( String studyName, boolean requestId, String idPrompt ) {
        this.studyName = studyName;
        this.requestId = requestId;
        this.idPrompt = idPrompt;
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

    /*
     * Location: CU Boulder, Chem 1113 course
     * Study dates: 11/7/11 - 11/11/11
     * Sims: Molecule Shapes, Molecule Polarity
     * PhET researchers: Julia and Kelly
     *
     * This study was done during prototyping, so it's unfortunate that the study name is very general.
     * But since PhET has an investment in data files containing this study name, we're stuck with it.
     */
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
