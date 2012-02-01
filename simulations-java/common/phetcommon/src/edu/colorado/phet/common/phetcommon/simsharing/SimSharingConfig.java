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
        addConfig( new AcidBaseSolutionsSpring2012() );
        addConfig( new DallasJan2012() );
        addConfig( new DallasJan2012ID() );
        addConfig( new FaradaySpring2012() );
        addConfig( new BalancingActSpring2012() );
        addConfig( new MoleculeShapesFeb2012() );
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
    public final boolean idRequired; // true=id required, false=optional
    public final String idPrompt; // prompt used to request student's id (irrelevant if requestId is false)
    public final boolean sendToLogFile; // prompt used to request student's id (irrelevant if requestId is false)
    public final boolean sendToServer;
    public final boolean collectIPAddress = false;

    protected SimSharingConfig( String studyName, boolean sendToLogFile, boolean sendToServer, boolean requestId, boolean idRequired ) {
        this( studyName, sendToLogFile, sendToServer, requestId, idRequired, null );
    }

    protected SimSharingConfig( String studyName, boolean sendToLogFile, boolean sendToServer, boolean requestId, boolean idRequired, String idPrompt ) {
        this.studyName = studyName;
        this.sendToLogFile = sendToLogFile;
        this.sendToServer = sendToServer;
        this.requestId = requestId;
        this.idPrompt = idPrompt;
        this.idRequired = idRequired;
    }

    public boolean isSendToLogFile() {
        return sendToLogFile;
    }

    public boolean isSendToServer() {
        return sendToServer;
    }

    // Default behavior, used if there is no study specified or the specified study isn't found.
    private static class DefaultConfig extends SimSharingConfig {
        public DefaultConfig() {
            this( "default" );
        }

        protected DefaultConfig( String name ) {
            super( name, false, true, false, false, "" );
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
            super( "colorado", false, true, true, true, "Enter your computer number:" );
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
            super( "utah", false, true, true, false, "Enter your audio recorder number:" );
        }
    }

    /*
    * Location: Dallas, TX
    * Dates: Week of Jan 23 2012
    * Sims: Energy Skate Park
    * PhET researchers: Emily B. Moore
    * Contacts: DMcLeod@uplifteducation.org
    */
    private static class DallasJan2012 extends SimSharingConfig {
        public DallasJan2012() {
            super( "dallas-jan-2012", true, true, false, false );
        }
    }

    /*
    * Location: Dallas, TX
    * Dates: Week of Jan 23 2012
    * Sims: Energy Skate Park
    * PhET researchers: Emily B. Moore
    * Contacts: DMcLeod@uplifteducation.org
    *
    * Same as above, but requires student id
    */
    private static class DallasJan2012ID extends SimSharingConfig {
        public DallasJan2012ID() {
            super( "dallas-jan-2012-id", true, true, true, true );
        }
    }

    /*
     * Acid-Base Solutions study to be done in Spring 2012. See #3170.
     * Principal researcher: Kelly Lancaster
     * Location: CU Boulder
     * Dates: Jan 23-27, 2012
     */
    private static class AcidBaseSolutionsSpring2012 extends SimSharingConfig {
        public AcidBaseSolutionsSpring2012() {
            super( "acid-base-solutions-spring-2012", true, true, false, false );
        }
    }

    /*
     *  Faraday's Electromagnetic Lab study to be done in Spring 2012. See #3214.
     *  Principal researcher: Ariel Paul
     *  TODO document details
     */
    private static class FaradaySpring2012 extends SimSharingConfig {
        public FaradaySpring2012() {
            super( "faraday-spring-2012", false, true, false, false );
        }
    }


    /*
     * TODO: document
     */
    private static class BalancingActSpring2012 extends SimSharingConfig {
        public BalancingActSpring2012() {
            super( "balancing-act-spring-2012", true, true, false, false );
        }
    }

    /*
     *  Molecule Shapes study to be done in Feb 2012. See #3238.
     *  Principal researcher: Kelly Lancaster
     *  Location: CU Boulder
     *  Dates: TODO
     */
    private static class MoleculeShapesFeb2012 extends SimSharingConfig {
        public MoleculeShapesFeb2012() {
            super( "molecule-shapes-feb-2012", true, true, false, false );
        }
    }
}