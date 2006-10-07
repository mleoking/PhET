/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.piccolo;

import javax.swing.JComponent;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ITabbedModulePane;
import edu.colorado.phet.common.view.JTabbedModulePane;
import edu.colorado.phet.common.view.util.FrameSetup;

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
 * @version $Revision$
 */
public class PiccoloPhetApplication extends PhetApplication {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods and inner classes
    //--------------------------------------------------------------------------------------------------
            
    // Graphical PhetTabbedPanes
    public static final TabbedPaneType PHET_TABBED_PANE = new TabbedPaneType(){
        public ITabbedModulePane createTabbedPane() {
            return new TabbedModulePanePiccolo();
        }
    };

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    /**
     * @param args
     * @param title
     * @param description
     * @param version
     */
    public PiccoloPhetApplication( String[] args, String title, String description, String version ) {
        super( args, title, description, version );
        super.setTabbedPaneType(PHET_TABBED_PANE);
    }

    /**
     * Allows for the specification of the type of tabbed panes to be used.
     *
     * @param args
     * @param title
     * @param description
     * @param version
     * @param tabbedPaneType The type of tabbed panes to be used
     */
    public PiccoloPhetApplication( String[] args, String title, String description, String version, TabbedPaneType tabbedPaneType ) {
        super( args, title, description, version );
        super.setTabbedPaneType(PHET_TABBED_PANE);
    }

    /**
     * @param args
     * @param title
     * @param description
     * @param version
     * @param frameSetup
     */
    public PiccoloPhetApplication( String[] args, String title, String description, String version, FrameSetup frameSetup ) {
        super( args, title, description, version, frameSetup );
        super.setTabbedPaneType(PHET_TABBED_PANE);
    }

    /**
     * Allows for the specification of the type of tabbed panes to be used.
     *
     * @param args
     * @param title
     * @param description
     * @param version
     * @param frameSetup
     * @param tabbedPaneType
     */
    public PiccoloPhetApplication( String[] args, String title, String description, String version, FrameSetup frameSetup, TabbedPaneType tabbedPaneType ) {
        super( args, title, description, version, frameSetup,PHET_TABBED_PANE );
    }

}
