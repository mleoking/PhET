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
import edu.colorado.phet.statesofmatter.module.interactionpotential.InteractionPotentialModule;
import edu.colorado.phet.statesofmatter.module.phasechanges.PhaseChangesModule;
import edu.colorado.phet.statesofmatter.module.solidliquidgas.SolidLiquidGasModule;

/**
 * Main application class for the Interaction Potential simulation flavor.
 *
 * @author John Blanco
 */
public class InteractionPotentialApplication extends PiccoloPhetApplication implements IProguardKeepClass {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private InteractionPotentialModule   m_interactionPotentialModule;
    private static TabbedModulePanePiccolo m_tabbedModulePane;
    
    //----------------------------------------------------------------------------
    // Sole Constructor
    //----------------------------------------------------------------------------
    
    public InteractionPotentialApplication( PhetApplicationConfig config ) {
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
                m_tabbedModulePane = new TabbedModulePanePiccolo();
                m_tabbedModulePane.setSelectedTabColor( StatesOfMatterConstants.SELECTED_TAB_COLOR );
                return m_tabbedModulePane;
            }
        };
        setTabbedPaneType( tabbedPaneType );
    }
    
    /**
     * Initializes the modules.
     */
    private void initModules() {
        
        Frame parentFrame = getPhetFrame();

        m_interactionPotentialModule = new InteractionPotentialModule( parentFrame );
        addModule( m_interactionPotentialModule );
    }

    /**
     * Initializes the menu bar.
     */
    private void initMenubar( String[] args ) {
    	// Stubbed for now.
    }
    
    //----------------------------------------------------------------------------
    // Setters & getters
    //----------------------------------------------------------------------------

    public void setSelectedTabColor( Color color ) {
        if ( m_tabbedModulePane != null ) {
            m_tabbedModulePane.setSelectedTabColor( color );
        }
    }

    public Color getSelectedTabColor() {
        Color color = Color.WHITE; 
        if ( m_tabbedModulePane != null ) {
            color = m_tabbedModulePane.getSelectedTabColor();
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
        return getModule( 0 ).getControlPanel().getBackground();
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
                InteractionPotentialApplication app = new InteractionPotentialApplication( config );

                // Start the application.
                app.startApplication();
            }
        } );
    }
}
