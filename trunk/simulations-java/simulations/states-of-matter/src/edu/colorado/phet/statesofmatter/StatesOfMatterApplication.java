/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter;

import java.awt.Color;
import java.awt.Frame;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.common.phetcommon.view.ITabbedModulePane;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.TabbedModulePanePiccolo;
import edu.colorado.phet.statesofmatter.developer.DeveloperMenu;
import edu.colorado.phet.statesofmatter.menu.OptionsMenu;
import edu.colorado.phet.statesofmatter.module.exp2.Exp2SolidLiquidGasModule;
import edu.colorado.phet.statesofmatter.module.experimental.ExpSolidLiquidGasModule;
import edu.colorado.phet.statesofmatter.module.phasechanges.PhaseChangesModule;
import edu.colorado.phet.statesofmatter.module.solidliquidgas.SolidLiquidGasModule;

/**
 * Main application class for the States of Matter simulation.
 *
 * @author John Blanco
 */
public class StatesOfMatterApplication extends PiccoloPhetApplication implements IProguardKeepClass {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private SolidLiquidGasModule _solidLiquidGasModule;
    private PhaseChangesModule   _phaseChangesModule;
    private ExpSolidLiquidGasModule   _experimentalModule;
    private Exp2SolidLiquidGasModule   _experimentalModule2;
    private static TabbedModulePanePiccolo _tabbedModulePane;
    
    //----------------------------------------------------------------------------
    // Sole Constructor
    //----------------------------------------------------------------------------
    
    public StatesOfMatterApplication( PhetApplicationConfig config ) {
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
                _tabbedModulePane.setSelectedTabColor( StatesOfMatterConstants.SELECTED_TAB_COLOR );
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

        _solidLiquidGasModule = new SolidLiquidGasModule( parentFrame );
        addModule( _solidLiquidGasModule );

        _phaseChangesModule = new PhaseChangesModule( parentFrame );
        addModule( _phaseChangesModule );

        _experimentalModule = new ExpSolidLiquidGasModule( parentFrame );
        addModule( _experimentalModule );

        _experimentalModule2 = new Exp2SolidLiquidGasModule( parentFrame );
        addModule( _experimentalModule2 );
    }

    /**
     * Initializes the menu bar.
     */
    private void initMenubar( String[] args ) {

        final PhetFrame frame = getPhetFrame();

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

    public static void main(final String[] args) {
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

                PhetApplicationConfig config = 
                    new PhetApplicationConfig( args, StatesOfMatterConstants.FRAME_SETUP, 
                            StatesOfMatterResources.getResourceLoader() );
                
                PhetLookAndFeel p = new PhetLookAndFeel();
                p.setBackgroundColor( StatesOfMatterConstants.CONTROL_PANEL_COLOR );
                p.initLookAndFeel();

                // Create the application.
                StatesOfMatterApplication app = new StatesOfMatterApplication( config );

                // Start the application.
                app.startApplication();
            }
        } );
    }
}
