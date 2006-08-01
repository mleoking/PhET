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

    /**
     * Enumeration class used to specify the type of tabbed panes the application is to use in
     * its Module instances
     */
    public static class TabbedPaneType {
        private TabbedPaneType() {
        }
    }

    // Standard Swing JTabbedPanes
    public static final TabbedPaneType JTABBED_PANE = new TabbedPaneType();
    // Graphical PhetTabbedPanes
    public static final TabbedPaneType PHET_TABBED_PANE = new TabbedPaneType();
    // The default TabbedModulePaneType
    private static final TabbedPaneType DEFAULT_TABBED_PANE_TYPE = PHET_TABBED_PANE;

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    // The type to be used for tabbed module panes
    private TabbedPaneType tabbedPaneType = DEFAULT_TABBED_PANE_TYPE;

    /**
     * @param args
     * @param title
     * @param description
     * @param version
     */
    public PiccoloPhetApplication( String[] args, String title, String description, String version ) {
        super( args, title, description, version );
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
        this.tabbedPaneType = tabbedPaneType;
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
        super( args, title, description, version, frameSetup );
        this.tabbedPaneType = tabbedPaneType;
    }

    /**
     * Overrides parent class behavior to provide the option of specifying what type of tabbed panes to use.
     *
     * @param modules
     * @return a tabbed module pane
     */
    public JComponent createTabbedPane( Module[] modules ) {
        ITabbedModulePane tabbedPane = null;
        if( tabbedPaneType == PHET_TABBED_PANE ) {
            tabbedPane = new TabbedModulePanePiccolo();
        }
        else if( tabbedPaneType == JTABBED_PANE ) {
            tabbedPane = new JTabbedModulePane();
        }
        tabbedPane.init( this, modules );
        return (JComponent)tabbedPane;
    }
}
