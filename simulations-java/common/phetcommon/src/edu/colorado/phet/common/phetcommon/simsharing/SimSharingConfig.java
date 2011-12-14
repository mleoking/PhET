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
     * PhET researchers: Julia Chamberlain, Kelly Lancaster
     *
     * It's unfortunate that this study name is very overly general.
     * But this study was done during prototyping, before "study configurations" were conceived.
     * Since PhET has an investment in data files containing this study name, we're stuck with it.
     */
    private static class ColoradoConfig extends SimSharingConfig {
        public ColoradoConfig() {
            super( "colorado", true, "Enter your computer number:" );
        }
    }

    /*
     * Location: Weber State University, UT
     * Dates: 11/7/11 - 11/9/11
     * Sims: Molecule Shapes, Balancing Chemical Equations, Molecule Polarity
     * PhET researchers: Emily B. Moore
     *
     * It's unfortunate that this study name is very overly general.
     * But this study was done during prototyping, before "study configurations" were conceived.
     * Since PhET has an investment in data files containing this study name, we're stuck with it.
     */
    private static class UtahConfig extends SimSharingConfig {
        public UtahConfig() {
            super( "utah", true, "Enter your audio recorder number:" );
        }
    }
}
