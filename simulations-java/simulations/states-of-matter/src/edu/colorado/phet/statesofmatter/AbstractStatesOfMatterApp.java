package edu.colorado.phet.statesofmatter;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.statesofmatter.developer.DeveloperMenu;

public class AbstractStatesOfMatterApp extends PiccoloPhetApplication {


    public AbstractStatesOfMatterApp( PhetApplicationConfig config ) {
        super( config );
    }

    /**
     * Initializes the menu bar.
     */
    protected void initMenubar( String[] args ) {
    
        // Developer menu
        final PhetFrame frame = getPhetFrame();
        DeveloperMenu developerMenu = new DeveloperMenu( this );
        if ( developerMenu.getMenuComponentCount() > 0 && isDeveloperControlsEnabled() ) {
            frame.addMenu( developerMenu );
        }
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
