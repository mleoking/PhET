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

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ITabbedModulePane;
import edu.colorado.phet.common.view.TabbedModulePanePhetGraphics;
import edu.colorado.phet.common.view.util.FrameSetup;

import javax.swing.*;

/**
 * PiccoloPhetApplication
 * <p/>
 * Allows for the use of components and graphics that require Piccolo support.
 * <p/>
 * Piccolo-dependent items that can be specified:
 * <ul>
 * <li>PhetTabbedPane is used in TabbedModulePane instances. (JTabbedPane can be specified in the constructor, if
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
     * its TabbedModulePane instances
     */
    public static class TabbedModulePaneType {
        private TabbedModulePaneType() {
        }
    }

    // Standard Swing JTabbedPanes
    public static final TabbedModulePaneType JTABBED_PANE = new TabbedModulePaneType();
    // Graphical PhetTabbedPanes
    public static final TabbedModulePaneType PHET_TABBED_PANE = new TabbedModulePaneType();
    // The default TabbedModulePaneType
    private static final TabbedModulePaneType DEFAULT_TABBED_PANE_TYPE = PHET_TABBED_PANE;

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    // The type to be used for tabbed module panes
    private TabbedModulePaneType tabbedModulePaneType = DEFAULT_TABBED_PANE_TYPE;

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
     * @param tabbedModulePaneType The type of tabbed panes to be used
     */
    public PiccoloPhetApplication( String[] args, String title, String description, String version, TabbedModulePaneType tabbedModulePaneType ) {
        super( args, title, description, version );
        this.tabbedModulePaneType = tabbedModulePaneType;
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
     * @param tabbedModulePaneType
     */
    public PiccoloPhetApplication( String[] args, String title, String description, String version, FrameSetup frameSetup, TabbedModulePaneType tabbedModulePaneType ) {
        super( args, title, description, version, frameSetup );
        this.tabbedModulePaneType = tabbedModulePaneType;
    }

    /**
     * Overrides parent class behavior to provide the option of specifying what type of tabbed panes to use.
     *
     * @param modules
     * @return a tabbed module pane
     */
    public JComponent createTabbedPane( Module[] modules ) {
        ITabbedModulePane tabbedPane = null;
        if( tabbedModulePaneType == PHET_TABBED_PANE ) {
            tabbedPane = new TabbedModulePanePiccolo();
        }
        else if( tabbedModulePaneType == JTABBED_PANE ) {
            tabbedPane = new TabbedModulePanePhetGraphics();
        }
        tabbedPane.init( this, modules );
        return (JComponent)tabbedPane;
    }
}
