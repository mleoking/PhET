/* Copyright 2009, University of Colorado */

package edu.colorado.phet.common.phetcommon.util;

import java.io.File;

import javax.jnlp.UnavailableServiceException;

import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;

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
    
    // prefix of codebase attribute in JNLP files
    private static final String PHET_PRODUCTION_CODE_BASE_PREFIX = "http://phet.colorado.edu";
    private static final String PHET_DEVELOPMENT_CODE_BASE_PREFIX = "http://www.colorado.edu/physics/phet/dev";

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
    
    public String toString() {
        return name;
    }
    
    /**
     * Gets the deployment scenario, a singleton.
     * This is determined once, on demand, since it does not change.
     * @return
     */
    public static DeploymentScenario getInstance() {
        if ( instance == null ) {
            instance = determineScenario();
            System.out.println( "DeploymentScenario.instance=" + instance.toString() );//XXX
        }
        return instance;
    }
    
    /*
     * Determines which scenario was used to deploy the application that we're running.
     */
    private static DeploymentScenario determineScenario() {
        
        DeploymentScenario scenario = null;

        if ( PhetServiceManager.isJavaWebStart() ) {

            if ( isPhetInstallation() ) {
                scenario = DeploymentScenario.PHET_INSTALLATION;
            }
            else {
                // web-started sims are differentiated base on the codebase attribute specified in the JNLP file
                String codeBase = null;
                try {
                    codeBase = PhetServiceManager.getBasicService().getCodeBase().getPath();
                }
                catch ( UnavailableServiceException e ) {
                    codeBase = "?";
                    e.printStackTrace();
                }

                if ( codeBase.startsWith( PHET_PRODUCTION_CODE_BASE_PREFIX ) ) {
                    scenario = DeploymentScenario.PHET_PRODUCTION_WEBSITE;
                }
                else if ( codeBase.startsWith( PHET_DEVELOPMENT_CODE_BASE_PREFIX ) ) {
                    scenario = DeploymentScenario.PHET_DEVELOPMENT_WEBSITE;
                }
                else {
                    scenario = DeploymentScenario.OTHER_WEBSITE;
                }
            }
        }
        else if ( FileUtils.isJarCodeSource() ) {
            scenario = DeploymentScenario.STANDALONE_JAR;
        }
        else {
            scenario = DeploymentScenario.DEVELOPER_IDE;
        }
        return scenario;
    }
    
    /*
     * Detect a PhET installation by looking for the phet-installation.properties file.
     */
    private static boolean isPhetInstallation( ) {
        boolean exists = false;
        if ( FileUtils.isJarCodeSource() ) {
            File codeSource = FileUtils.getCodeSource();
            File parent = codeSource.getParentFile();
            if ( parent != null ) {
                File grandparent = parent.getParentFile();
                if ( grandparent != null ) {
                    File specialFile = new File( grandparent.getAbsolutePath() + System.getProperty( "file.separator" ) + "phet-installation.properties" );
                    exists = specialFile.exists();
                }
            }
        }
        return exists;
    }
    
    public static void main( String[] args ) {
        System.out.println( DeploymentScenario.getInstance() );
    }
}
