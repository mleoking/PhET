/* Copyright 2009, University of Colorado */

package edu.colorado.phet.common.phetcommon.util;

import java.net.URL;

import javax.jnlp.UnavailableServiceException;

import edu.colorado.phet.common.phetcommon.files.PhetInstallation;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.util.logging.USLogger;

/**
 * PhET simulations can be deployed in several different ways.
 * This class determines how the sim that is running was deployed.
 * <p>
 * This implementation combines the singleton pattern and the Java 1.4 typesafe enum pattern.
 */
public class DeploymentScenario {
    
    // enumeration
    public static final DeploymentScenario PHET_INSTALLATION = new DeploymentScenario( "phet-installation", false );
    public static final DeploymentScenario STANDALONE_JAR = new DeploymentScenario( "standalone-jar", false );
    public static final DeploymentScenario PHET_PRODUCTION_WEBSITE = new DeploymentScenario( "phet-production-website", true );
    public static final DeploymentScenario PHET_DEVELOPMENT_WEBSITE = new DeploymentScenario( "phet-development-website", true );
    public static final DeploymentScenario OTHER_WEBSITE = new DeploymentScenario( "other-website", true );
    public static final DeploymentScenario DEVELOPER_IDE = new DeploymentScenario( "developer-ide", false );
    public static final DeploymentScenario UNKNOWN = new DeploymentScenario( "unknown", false );
    
    /*
     * There are fragments of the codebase attribute in JNLP files.
     * Codebase is a URL, whose syntax is:
     * <scheme>://<authority><path>?<query>#<fragment>
     * 
     * Example: 
     * http://www.colorado.edu/physics/phet/dev/balloons/1.07.05
     *
     * We're using <authority> and the general part of <path> to identify the codebase
     * for the PhET production and development websites.
     */
    private static final String PHET_PRODUCTION_CODEBASE_PREFIX = "phet.colorado.edu"; // prefix!
    private static final String PHET_DEVELOPMENT_CODEBASE_SUBSTRING = "colorado.edu"; // substring!

    // singleton
    private static DeploymentScenario instance = null;
    
    private final String name;
    private final boolean online;
    
    /* singleton */
    private DeploymentScenario( String name, boolean online ) {
        this.name = name;
        this.online = online;
    }
    
    public boolean isOnline() {
        return online;
    }
    
    public boolean isOffline() {
        return !online;
    }
    
    public String getName() {
        return name;
    }
    
    public String toString() {
        return name;
    }
    
    public boolean equals( Object object ) {
        boolean equals = false;
        if ( object instanceof DeploymentScenario ) {
            equals = getName().equals( ( (DeploymentScenario) object ).getName() );
        }
        return equals;
    }
    
    /**
     * Gets the deployment scenario, a singleton.
     * This is determined once, on demand, since it does not change.
     * @return
     */
    public static DeploymentScenario getInstance() {
        if ( instance == null ) {
            instance = determineScenario();
        }
        return instance;
    }
    
    /*
     * Determines which scenario was used to deploy the application that we're running.
     */
    private static DeploymentScenario determineScenario() {

        DeploymentScenario scenario = UNKNOWN;

        if ( PhetServiceManager.isJavaWebStart() ) {

            if ( PhetInstallation.exists() ) {
                scenario = DeploymentScenario.PHET_INSTALLATION;
            }
            else {
                URL codeBase = PhetServiceManager.getCodeBase();
                if ( codeBase != null ) {

                    // web-started sims are differentiated base on the codebase attribute specified in the JNLP file

                    USLogger.log( "DeploymentScenario codeBase=" + codeBase.toString() );
                    String codebaseFragment = codeBase.getAuthority() + codeBase.getPath();

                    if ( codebaseFragment.startsWith( PHET_PRODUCTION_CODEBASE_PREFIX ) ) {
                        scenario = DeploymentScenario.PHET_PRODUCTION_WEBSITE;
                    }
                    else if ( codebaseFragment.indexOf( PHET_DEVELOPMENT_CODEBASE_SUBSTRING ) >= 0 ) {
                        /* 
                         * Do this after checking the production server scenario, 
                         * because deployment codebase substring may be contained in production codebase prefix.
                         */
                        scenario = DeploymentScenario.PHET_DEVELOPMENT_WEBSITE;
                    }
                    else {
                        scenario = DeploymentScenario.OTHER_WEBSITE;
                    }
                }
            }
        }
        else if ( FileUtils.isJarCodeSource() ) {
            scenario = DeploymentScenario.STANDALONE_JAR;
        }
        else {
            scenario = DeploymentScenario.DEVELOPER_IDE;
        }

        USLogger.log( "DeploymentScenario.determineScenario " + scenario.getName() );
        return scenario;
    }
    
    public static void main( String[] args ) {
        System.out.println( DeploymentScenario.getInstance() );
    }
}
