// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.opticaltweezers.module.dna.DNAModule;
import edu.colorado.phet.opticaltweezers.persistence.DNAConfig;
import edu.colorado.phet.opticaltweezers.persistence.GlobalConfig;
import edu.colorado.phet.opticaltweezers.persistence.StretchingDNAConfig;

/**
 * StretchingDNAApplication is the main application for the "Stretching DNA" flavor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StretchingDNAApplication extends OTAbstractApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private DNAModule _dnaModule;

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
    public StretchingDNAApplication( PhetApplicationConfig config ) {
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
        
        _dnaModule = new DNAModule( frame );
        addModule( _dnaModule );
    }

    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /**
     * Saves the simulation's configuration.
     */
    public void save() {

        StretchingDNAConfig appConfig = new StretchingDNAConfig();
        
        GlobalConfig globalConfig = appConfig.getGlobalConfig();
        globalConfig.setVersionString( getSimInfo().getVersion().toString() );
        globalConfig.setVersionMajor( getSimInfo().getVersion().getMajor() );
        globalConfig.setVersionMinor( getSimInfo().getVersion().getMinor() );
        globalConfig.setVersionDev( getSimInfo().getVersion().getDev() );
        globalConfig.setVersionRevision( getSimInfo().getVersion().getRevision() );
        
        DNAConfig dnaConfig = _dnaModule.save();
        appConfig.setDNAConfig( dnaConfig );
        
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
            if ( object instanceof StretchingDNAConfig ) {
                StretchingDNAConfig appConfig = (StretchingDNAConfig) object;
                _dnaModule.load( appConfig.getDNAConfig() );
            }
            else {
                String message = PhetCommonResources.getString( "XMLPersistenceManager.message.notAConfigFile" );
                PhetOptionPane.showErrorDialog( getPhetFrame(), message );
            }
        }
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new StretchingDNAApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, OTConstants.PROJECT_NAME, OTConstants.FLAVOR_STRETCHING_DNA );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
