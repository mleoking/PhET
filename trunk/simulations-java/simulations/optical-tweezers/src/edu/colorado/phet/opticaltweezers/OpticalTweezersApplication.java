/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers;

import javax.swing.JOptionPane;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.opticaltweezers.module.dna.DNAModule;
import edu.colorado.phet.opticaltweezers.module.motors.MotorsModule;
import edu.colorado.phet.opticaltweezers.module.physics.PhysicsModule;
import edu.colorado.phet.opticaltweezers.persistence.*;

/**
 * OpticalTweezersApplication is the main application for the "Optical Tweezers" flavor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OpticalTweezersApplication extends OTAbstractApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private PhysicsModule _physicsModule;
    private DNAModule _dnaModule;
    private MotorsModule _motorsModule;

    // PersistanceManager handles loading/saving application configurations.
    private XMLPersistenceManager _persistenceManager;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param args command line arguments
     */
    public OpticalTweezersApplication( PhetApplicationConfig config ) {
        super( config );
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /*
     * Initializes the modules.
     */
    protected void initModules() {

        final PhetFrame frame = getPhetFrame();
        
        _physicsModule = new PhysicsModule( frame );
        addModule( _physicsModule );

        _dnaModule = new DNAModule( frame );
        addModule( _dnaModule );

        _motorsModule = new MotorsModule( frame );
        addModule( _motorsModule );

        setControlPanelBackground( OTConstants.CONTROL_PANEL_COLOR );
    }

    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /**
     * Saves the simulation's configuration.
     */
    public void save() {

        OpticalTweezersConfig appConfig = new OpticalTweezersConfig();
        
        GlobalConfig globalConfig = appConfig.getGlobalConfig();
        globalConfig.setVersionString( getSimInfo().getVersion().toString() );
        globalConfig.setVersionMajor( getSimInfo().getVersion().getMajor() );
        globalConfig.setVersionMinor( getSimInfo().getVersion().getMinor() );
        globalConfig.setVersionDev( getSimInfo().getVersion().getDev() );
        globalConfig.setVersionRevision( getSimInfo().getVersion().getRevision() );
        
        PhysicsConfig physicsConfig = _physicsModule.save();
        appConfig.setPhysicsConfig( physicsConfig );
        
        DNAConfig dnaConfig = _dnaModule.save();
        appConfig.setDNAConfig( dnaConfig );
        
        MotorsConfig motorsConfig = _motorsModule.save();
        appConfig.setMotorsConfig( motorsConfig );
        
        if ( _persistenceManager == null ) {
            _persistenceManager = new XMLPersistenceManager( getPhetFrame() );
        }
        _persistenceManager.save( appConfig );
    }

    /**
     * Loads a simulation configuration.
     */
    public void load() {

        if ( _persistenceManager == null ) {
            _persistenceManager = new XMLPersistenceManager( getPhetFrame() );
        }
        
        Object object = _persistenceManager.load();
        if ( object != null ) {
            if ( object instanceof OpticalTweezersConfig ) {
                OpticalTweezersConfig appConfig = (OpticalTweezersConfig) object;
                _physicsModule.load( appConfig.getPhysicsConfig() );
                _dnaModule.load( appConfig.getDNAConfig() );
                _motorsModule.load( appConfig.getMotorsConfig() );
            }
            else {
                String message = OTResources.getString( "message.notAConfigFile" );
                String title = OTResources.getString( "title.error" );
                JOptionPane.showMessageDialog( getPhetFrame(), message, title, JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new OpticalTweezersApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, OTConstants.PROJECT_NAME, OTConstants.FLAVOR_OPTICAL_TWEEZERS );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
