/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics;

import java.awt.Color;
import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.QuickProfiler;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.ITabbedModulePane;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.TabbedModulePanePiccolo;
import edu.colorado.phet.nuclearphysics.developer.DeveloperMenu;
import edu.colorado.phet.nuclearphysics.menu.OptionsMenu;
import edu.colorado.phet.nuclearphysics.module.alpharadiation.AlphaRadiationModule;
import edu.colorado.phet.nuclearphysics.module.chainreaction.ChainReactionModule;
import edu.colorado.phet.nuclearphysics.module.fissiononenucleus.FissionOneNucleusModule;
import edu.colorado.phet.nuclearphysics.module.nuclearreactor.NuclearReactorModule;

/**
 * TemplateApplication is the main application for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * 
 */
public class NuclearPhysicsApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Module for the tab that displays alpha radiation behavior.
    private AlphaRadiationModule _alphaRadiationModule;
    
    // Module for the tab that displays fission of a single atomic nucleus.
    private FissionOneNucleusModule _fissionOneNucleusModule;
    
    // Module for the tab that displays a chain reaction of multiple nuclei.
    private ChainReactionModule _chainReactionModule;
    
    // Module for the tab that displays the nuclear reactor.
    private NuclearReactorModule _nuclearReactorModule;
    
    // PersistanceManager is used to save/load simulation configurations.
    private XMLPersistenceManager _persistenceManager;

    private static TabbedModulePanePiccolo _tabbedModulePane;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param config - The configuration for the application.
     */
    public NuclearPhysicsApplication( PhetApplicationConfig config )
    {
        super( config );
        initTabbedPane();
        initModules();
        initMenubar( config.getCommandLineArgs() );
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /**
     * Initializes the tabbed pane.
     */
    private void initTabbedPane() {

        // Create our own tabbed pane type so we can set the tab color
        TabbedPaneType tabbedPaneType = new TabbedPaneType(){
            public ITabbedModulePane createTabbedPane() {
                _tabbedModulePane = new TabbedModulePanePiccolo();
                _tabbedModulePane.setSelectedTabColor( NuclearPhysicsConstants.SELECTED_TAB_COLOR );
                return _tabbedModulePane;
            }
        };
        setTabbedPaneType( tabbedPaneType );
    }
    
    /**
     * Initializes the modules.
     */
    private void initModules() {
        
        Frame parentFrame = getPhetFrame();

        _alphaRadiationModule = new AlphaRadiationModule( parentFrame );
        addModule( _alphaRadiationModule );

        _fissionOneNucleusModule = new FissionOneNucleusModule( parentFrame );
        addModule( _fissionOneNucleusModule );

        _chainReactionModule = new ChainReactionModule( parentFrame );
        addModule( _chainReactionModule );

        _nuclearReactorModule = new NuclearReactorModule( parentFrame );
        addModule( _nuclearReactorModule );
    }

    /**
     * Initializes the menu bar.
     */
    private void initMenubar( String[] args ) {

        final PhetFrame frame = getPhetFrame();

        if ( _persistenceManager == null ) {
            _persistenceManager = new XMLPersistenceManager( frame );
        }

        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu();
        if ( optionsMenu.getMenuComponentCount() > 0 ) {
            frame.addMenu( optionsMenu );
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

    public void setSelectedTabColor( Color color ) {
        if ( _tabbedModulePane != null ) {
            _tabbedModulePane.setSelectedTabColor( color );
        }
    }

    public Color getSelectedTabColor() {
        Color color = Color.WHITE; 
        if ( _tabbedModulePane != null ) {
            color = _tabbedModulePane.getSelectedTabColor();
        }
        return color;
    }

    public void setControlPanelBackground( Color color ) {
        Module[] modules = getModules();
        for ( int i = 0; i < modules.length; i++ ) {
            modules[i].setControlPanelBackground( color );
            modules[i].setClockControlPanelBackground( color );
            modules[i].setHelpPanelBackground( color );
        }
    }

    public Color getControlPanelBackground() {
        return ( (Module) getModule( 0 ) ).getControlPanel().getBackground();
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
        final QuickProfiler profiler=new QuickProfiler( "Nuclear Physics 2 Startup Time");
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

                PhetApplicationConfig config = new PhetApplicationConfig( args, NuclearPhysicsConstants.FRAME_SETUP, NuclearPhysicsResources.getResourceLoader() );
                
                PhetLookAndFeel p = new PhetLookAndFeel();
                p.setBackgroundColor( NuclearPhysicsConstants.CONTROL_PANEL_COLOR );
                p.initLookAndFeel();

                // Create the application.
                NuclearPhysicsApplication app = new NuclearPhysicsApplication( config );

                // Start the application.
                app.startApplication();
                System.out.println( "profiler = " + profiler );
            }
        } );
    }
}
