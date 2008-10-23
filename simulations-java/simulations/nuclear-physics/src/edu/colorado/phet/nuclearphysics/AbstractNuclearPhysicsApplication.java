
package edu.colorado.phet.nuclearphysics;


import java.awt.Color;
import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplication.TabbedPaneType;
import edu.colorado.phet.common.phetcommon.util.QuickProfiler;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.ITabbedModulePane;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.TabbedModulePanePiccolo;
import edu.colorado.phet.nuclearphysics.developer.DeveloperMenu;
import edu.colorado.phet.nuclearphysics.menu.OptionsMenu;
import edu.colorado.phet.nuclearphysics.module.chainreaction.ChainReactionModule;
import edu.colorado.phet.nuclearphysics.module.fissiononenucleus.FissionOneNucleusModule;
import edu.colorado.phet.nuclearphysics.module.nuclearreactor.NuclearReactorModule;
import edu.colorado.phet.nuclearphysics.module.singlenucleusalphadecay.SingleNucleusAlphaDecayModule;

public class AbstractNuclearPhysicsApplication extends PiccoloPhetApplication {

    private XMLPersistenceManager _persistenceManager;
    private static TabbedModulePanePiccolo _tabbedModulePane;


    public AbstractNuclearPhysicsApplication( PhetApplicationConfig config ) {
        super( config );
    }

    /**
     * Initializes the tabbed pane.
     */
    protected void initTabbedPane() {
    
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
     * Initializes the menu bar.
     */
    protected void initMenubar( String[] args ) {
    
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
        return getModule( 0 ).getControlPanel().getBackground();
    }

}