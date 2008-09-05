/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
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
        globalConfig.setVersionString( getApplicationConfig().getVersion().toString() );
        globalConfig.setVersionMajor( getApplicationConfig().getVersion().getMajor() );
        globalConfig.setVersionMinor( getApplicationConfig().getVersion().getMinor() );
        globalConfig.setVersionDev( getApplicationConfig().getVersion().getDev() );
        globalConfig.setVersionRevision( getApplicationConfig().getVersion().getRevision() );
        
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

    /**
     * Main entry point.
     *
     * @param args command line arguments
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    public static void main( final String[] args ) {

        /*
         * Wrap the body of main in invokeLater, so that all initialization occurs
         * in the event dispatch thread. Sun now recommends doing all Swing init in
         * the event dispatch thread. And the Piccolo-based tabs in TabbedModulePanePiccolo
         * seem to cause startup deadlock problems if they aren't initialized in the
         * event dispatch thread. Since we don't have an easy way to separate Swing and
         * non-Swing init, we're stuck doing everything in invokeLater.
         */
        SwingUtilities.invokeLater( new Runnable() {

            public void run() {
                
                // Initialize look-and-feel
                PhetLookAndFeel laf = new PhetLookAndFeel();
                laf.initLookAndFeel();

                PhetApplicationConfig config = new PhetApplicationConfig( args, OTConstants.FRAME_SETUP, OTResources.getResourceLoader(), OTConstants.FLAVOR_OPTICAL_TWEEZERS );

                // Create the application.
                OpticalTweezersApplication app = new OpticalTweezersApplication( config );

                // Start the application.
                app.startApplication();
            }
        } );
    }
}
