// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.util.HashMap;

/**
 * Immutable configuration information for specific sim-sharing studies.
 * To add a configuration for a new study, create a static instance and add it to CONFIG_MAP.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingConfig {

    // General-purpose config, for routine interviews.
    public static final SimSharingConfig INTERVIEWS = new SimSharingConfig( "interviews", true, false, false, false );

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
    public static final SimSharingConfig COLORADO_CONFIG = new SimSharingConfig( "colorado", false, true, true, true, "Enter your computer number:" );

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
    public static final SimSharingConfig UTAH_CONFIG = new SimSharingConfig( "utah", false, true, true, false, "Enter your audio recorder number:" );

    /*
    * Location: Dallas, TX
    * Dates: Week of Jan 23 2012
    * Sims: Energy Skate Park
    * PhET researchers: Emily B. Moore
    * Contacts: DMcLeod@uplifteducation.org
    */
    public static final SimSharingConfig DALLAS_JAN_2012 = new SimSharingConfig( "dallas-jan-2012", true, true, false, false );
    public static final SimSharingConfig DALLAS_JAN_2012_ID = new SimSharingConfig( "dallas-jan-2012-id", true, true, false, false );

    /*
     * Acid-Base Solutions study to be done in Spring 2012. See #3170.
     * Principal researcher: Kelly Lancaster
     * Location: CU Boulder
     * Dates: Jan 23-27, 2012
     */
    public static final SimSharingConfig ABS_SPRING_2012 = new SimSharingConfig( "acid-base-solutions-spring-2012", true, true, false, false );

    /*
    *  Faraday's Electromagnetic Lab study to be done in Spring 2012. See #3214.
    *  Principal researcher: Ariel Paul
    */
    public static final SimSharingConfig FARADAY_SPRING_2012 = new SimSharingConfig( "faraday-spring-2012", false, true, false, false );

    /*
     *  Balancing Act study that will be done in conjunction with researchers
     *  at Stanford.  Time frame is February 2012.  See #3207.
     */
    public static final SimSharingConfig BALANCING_ACT_SPRING_2012 = new SimSharingConfig( "balancing-act-spring-2012", true, true, true, true, "Please enter ID:" );

    /*
     *  Molecule Shapes study to be done in Feb 2012. See #3238.
     *  Principal researcher: Kelly Lancaster
     *  Location: CU Boulder
     */
    public static final SimSharingConfig MOLECULE_SHAPED_FEB_2012 = new SimSharingConfig(  "molecule-shapes-feb-2012", true, true, false, false );

    /*
     *  Fake configuration used for load testing of the DB.
     */
    public static final SimSharingConfig LOAD_TESTING = new SimSharingConfig( "load-testing", false, true, false, false );

    // Config used when study name doesn't match any other config.
    public static final SimSharingConfig DEFAULT = new SimSharingConfig( "default", false, false, false, false, "" );

    /*
     * Reactants, Products and Leftovers (RPAL) study to be done in April 2012. See #3204.
     * Principal researcher: Julia Chamberlain
     * Location: Boulder, CO
     */
    public static final SimSharingConfig RPAL_APRIL_2012 = new SimSharingConfig( "rpal-april-2012", true, true, false, false );

    private static final HashMap<String, SimSharingConfig> CONFIG_MAP = new HashMap<String, SimSharingConfig>();

    private static void addConfig( SimSharingConfig config ) {
        assert ( CONFIG_MAP.get( config.studyName ) == null );
        CONFIG_MAP.put( config.studyName, config );
    }

    static {
        addConfig( INTERVIEWS );
        addConfig( COLORADO_CONFIG );
        addConfig( UTAH_CONFIG );
        addConfig( DALLAS_JAN_2012 );
        addConfig( DALLAS_JAN_2012_ID );
        addConfig( ABS_SPRING_2012 );
        addConfig( FARADAY_SPRING_2012 );
        addConfig( BALANCING_ACT_SPRING_2012 );
        addConfig( MOLECULE_SHAPED_FEB_2012 );
        addConfig( LOAD_TESTING );
        addConfig( RPAL_APRIL_2012 );
    }

    public static SimSharingConfig getConfig( String studyName ) {
        SimSharingConfig config = CONFIG_MAP.get( studyName );
        if ( config == null ) {
            config = DEFAULT;
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
}