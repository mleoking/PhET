/* Copyright 2003-2004, University of Colorado */

package edu.colorado.phet.common.piccolophet;

import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.ITabbedModulePane;

/**
 * PiccoloPhetApplication
 * <p/>
 * Allows for the use of components and graphics that require Piccolo support.
 * <p/>
 * Piccolo-dependent items that can be specified:
 * <ul>
 * <li>PhetTabbedPane is used in Module instances. (JTabbedPane can be specified in the constructor, if
 * desired.)
 * </ul>
 *
 * @author Ron LeMaster
 */
public class PiccoloPhetApplication extends PhetApplication {

    public PiccoloPhetApplication( PhetApplicationConfig config ) {
        this( config, new TabbedModulePanePiccolo() );
    }
    
    public PiccoloPhetApplication( PhetApplicationConfig config, TabbedModulePanePiccolo tabbedModulePane ) {
        super( config, tabbedModulePane );
        
        // Add Piccolo-specific items to the developer menu
        getPhetFrame().getDeveloperMenu().add( new TabbedPanePropertiesMenuItem( getPhetFrame(), tabbedModulePane ) );
    }
}