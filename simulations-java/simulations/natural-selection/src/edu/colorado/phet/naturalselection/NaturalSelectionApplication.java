/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.util.Enumeration;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.naturalselection.developer.DeveloperMenu;
import edu.colorado.phet.naturalselection.module.NaturalSelectionModule;
import edu.colorado.phet.naturalselection.persistence.NaturalSelectionConfig;

public class NaturalSelectionApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // PersistanceManager is used to save/load simulation configurations.
    private XMLPersistenceManager persistenceManager;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public NaturalSelectionApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
        initMenubar( config.getCommandLineArgs() );
        //if ( isHighContrast() ) {
        //    setAccessibilityFontScale( 2.0f );
        //}
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /*
    * Initializes the modules.
    */
    private void initModules() {

        Frame parentFrame = getPhetFrame();

        Module tempModule = new NaturalSelectionModule( parentFrame );
        addModule( tempModule );

        tempModule.setLogoPanelVisible( false );
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar( String[] args ) {

        // File->Save/Load
        final PhetFrame frame = getPhetFrame();
        frame.addFileSaveLoadMenuItems();
        if ( persistenceManager == null ) {
            persistenceManager = new XMLPersistenceManager( frame );
        }

        // Developer menu
        DeveloperMenu developerMenu = new DeveloperMenu( this );
        if ( developerMenu.getMenuComponentCount() > 0 && isDeveloperControlsEnabled() ) {
            frame.addMenu( developerMenu );
        }
    }

    //----------------------------------------------------------------------------
    // Setters & getters
    //----------------------------------------------------------------------------

    public void setControlPanelBackground( Color color ) {
        Module[] modules = getModules();
        for ( int i = 0; i < modules.length; i++ ) {
            modules[i].setControlPanelBackground( color );
            modules[i].setClockControlPanelBackground( color );
            modules[i].setHelpPanelBackground( color );
        }
    }

    public Color getControlPanelBackground() {
        return getModule( 0 ).getControlPanel().getBackground();
    }

    public static boolean isHighContrast() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Boolean ret = (Boolean) toolkit.getDesktopProperty( "win.highContrast.on" );
        return ( ret != null && ret );
    }

    public static Color accessibleColor( Color color ) {
        if ( isHighContrast() ) {
            return null;
        }
        return color;
    }

    public static void setAccessibilityFontScale( final float scale ) {
        // NOT WORKING YET
        /*
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while ( keys.hasMoreElements() ) {
            Object key = keys.nextElement();
            Object value = UIManager.get( key );
            if ( value instanceof javax.swing.plaf.FontUIResource ) {
                UIManager.put( key, f );
            }
        }
        */
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                UIDefaults defaults = UIManager.getDefaults();
                Enumeration keys = defaults.keys();
                while ( keys.hasMoreElements() ) {
                    Object key = keys.nextElement();
                    Object value = defaults.get( key );
                    if ( value != null && value instanceof Font ) {
                        UIManager.put( key, null );
                        Font font = UIManager.getFont( key );
                        if ( font != null ) {
                            float size = font.getSize2D();
                            UIManager.put( key, new FontUIResource( font.deriveFont( size *
                                                                                     scale ) ) );
                        }
                    }
                }
            }
        } );

    }

    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /**
     * Saves the simulation's configuration.
     */
    @Override
    public void save() {

        NaturalSelectionConfig appConfig = new NaturalSelectionConfig();

        appConfig.setVersionString( getSimInfo().getVersion().toString() );
        appConfig.setVersionMajor( getSimInfo().getVersion().getMajor() );
        appConfig.setVersionMinor( getSimInfo().getVersion().getMinor() );
        appConfig.setVersionDev( getSimInfo().getVersion().getDev() );
        appConfig.setVersionRevision( getSimInfo().getVersion().getRevision() );

        // TODO: refactor module out to variable
        ( (NaturalSelectionModule) getModule( 0 ) ).save( appConfig );

        persistenceManager.save( appConfig );
    }

    /**
     * Loads the simulation's configuration.
     */
    @Override
    public void load() {

        Object object = persistenceManager.load();
        if ( object != null ) {

            if ( object instanceof NaturalSelectionConfig ) {
                NaturalSelectionConfig appConfig = (NaturalSelectionConfig) object;

                ( (NaturalSelectionModule) getModule( 0 ) ).load( appConfig );
            }
            else {
                String message = PhetCommonResources.getString( "XMLPersistenceManager.message.notAConfigFile" );
                String title = PhetCommonResources.getString( "Common.title.error" );
                JOptionPane.showMessageDialog( getPhetFrame(), message, title, JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {
        /* 
         * If you want to customize your application (look-&-feel, window size, etc) 
         * create your own PhetApplicationConfig and use one of the other launchSim methods
         */

        new PhetApplicationLauncher().launchSim( args, NaturalSelectionConstants.PROJECT_NAME, NaturalSelectionApplication.class );
    }

}
