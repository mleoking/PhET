// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * PiccoloPhetApplication
 * <p/>
 * Allows for the use of components and graphics that require Piccolo support.
 * Piccolo-dependent items that can be specified:
 * <ul>
 * <li>PhetTabbedPane is used in Module instances. (JTabbedPane can be specified in the constructor, if
 * desired.)
 * </ul>
 *
 * @author Ron LeMaster
 */
public class PiccoloPhetApplication extends PhetApplication {
    public static final PhetResources RESOURCES = new PhetResources( "piccolo-phet" );

    public PiccoloPhetApplication( PhetApplicationConfig config ) {
        this( config, new TabbedModulePanePiccolo() );
    }

    public PiccoloPhetApplication( PhetApplicationConfig config, TabbedModulePanePiccolo tabbedModulePane ) {
        super( config, tabbedModulePane );

        // Add Piccolo-specific items to the developer menu
        getPhetFrame().getDeveloperMenu().add( new TabbedPanePropertiesMenuItem( getPhetFrame(), tabbedModulePane ) );
    }
}