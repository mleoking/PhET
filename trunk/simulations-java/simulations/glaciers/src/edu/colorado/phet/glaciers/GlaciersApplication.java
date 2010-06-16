/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.glaciers.module.advanced.AdvancedModule;
import edu.colorado.phet.glaciers.module.intro.IntroModule;
import edu.colorado.phet.glaciers.persistence.AdvancedConfig;
import edu.colorado.phet.glaciers.persistence.GlaciersConfig;
import edu.colorado.phet.glaciers.persistence.IntroConfig;

/**
 * GlaciersApplication is the main application for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private IntroModule _introModule;
    private AdvancedModule _advancedModule;

    // PersistanceManager is used to save/load simulation configurations.
    private XMLPersistenceManager _persistenceManager;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param args command line arguments
     */
    public GlaciersApplication( PhetApplicationConfig config )
    {
        super( config );
        initModules();
        initMenubar( config.getCommandLineArgs() );
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /*
     * Initializes the modules.
     */
    private void initModules() {
        
        Frame parentFrame = getPhetFrame();
        
        _introModule = new IntroModule( parentFrame );
        addModule( _introModule );

        _advancedModule = new AdvancedModule( parentFrame );
        addModule( _advancedModule );
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar( String[] args ) {

        // File->Save/Load
        final PhetFrame frame = getPhetFrame();
        frame.addFileSaveLoadMenuItems();
        if ( _persistenceManager == null ) {
            _persistenceManager = new XMLPersistenceManager( frame );
        }

        // Developer menu
        JMenu developerMenu = frame.getDeveloperMenu();
        final JCheckBoxMenuItem evolutionStateDialogItem = new JCheckBoxMenuItem( "Glacier Evolution State..." );
        developerMenu.add( evolutionStateDialogItem );
        evolutionStateDialogItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setEvolutionStateDialogVisible( evolutionStateDialogItem.isSelected() );
            }
        } );
        final JCheckBoxMenuItem modelConstantsDialogItem = new JCheckBoxMenuItem( "Model Constants..." );
        developerMenu.add( modelConstantsDialogItem );
        modelConstantsDialogItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setModelConstantsDialogVisible( modelConstantsDialogItem.isSelected() );
            }
        } );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setEvolutionStateDialogVisible( boolean visible ) {
        _introModule.setEvolutionStateDialogVisible( visible );
        _advancedModule.setEvolutionStateDialogVisible( visible );
    }
    
    public void setModelConstantsDialogVisible( boolean visible ) {
        _introModule.setModelConstantsDialogVisible( visible );
        _advancedModule.setModelConstantsDialogVisible( visible );
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /**
     * Saves the simulation's configuration.
     */
    @Override
    public void save() {
        
        GlaciersConfig appConfig = new GlaciersConfig();
        
        appConfig.setVersionString( getSimInfo().getVersion().toString() );
        appConfig.setVersionMajor( getSimInfo().getVersion().getMajor() );
        appConfig.setVersionMinor( getSimInfo().getVersion().getMinor() );
        appConfig.setVersionDev( getSimInfo().getVersion().getDev() );
        appConfig.setVersionRevision( getSimInfo().getVersion().getRevision() );
        
        IntroConfig basicConfig = _introModule.save();
        appConfig.setBasicConfig( basicConfig );
        
        AdvancedConfig advancedConfig = _advancedModule.save();
        appConfig.setAdvancedConfig( advancedConfig );
        
        _persistenceManager.save( appConfig );
    }

    /**
     * Loads the simulation's configuration.
     */
    @Override
    public void load() {
        
        Object object = _persistenceManager.load();
        if ( object != null ) {
            
            if ( object instanceof GlaciersConfig ) {
                GlaciersConfig appConfig = (GlaciersConfig) object;
                
                IntroConfig basicConfig = appConfig.getBasicConfig();
                _introModule.load( basicConfig );
                
                AdvancedConfig advancedConfig = appConfig.getAdvancedConfig();
                _advancedModule.load( advancedConfig );
            }
            else {
                PhetOptionPane.showErrorDialog( getPhetFrame(), GlaciersStrings.MESSAGE_NOT_A_CONFIG_FILE );
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
     */
    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, GlaciersConstants.PROJECT_NAME, GlaciersApplication.class );
    }
}
