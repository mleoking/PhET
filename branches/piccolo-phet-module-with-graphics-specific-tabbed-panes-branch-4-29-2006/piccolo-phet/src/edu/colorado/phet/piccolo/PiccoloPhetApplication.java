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

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.TabbedModulePanePhetGraphics;
import edu.colorado.phet.common.view.ITabbedModulePane;

import javax.swing.*;

/**
 * PiccoloPhetApplication
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PiccoloPhetApplication extends PhetApplication {

    public static class TabbedModulePaneType {
        private TabbedModulePaneType() {
        }
    }

    public static final TabbedModulePaneType JTABBED_PANE = new TabbedModulePaneType();
    public static final TabbedModulePaneType PHET_TABBED_PANE = new TabbedModulePaneType();

    // The type to be used for tabbed module panes
    private TabbedModulePaneType tabbedModulePaneType = JTABBED_PANE;

    public PiccoloPhetApplication( String[] args, String title, String description, String version ) {
        super( args, title, description, version );
    }

    public PiccoloPhetApplication( String[] args, String title, String description, String version, TabbedModulePaneType tabbedModulePaneType ) {
        super( args, title, description, version );
        this.tabbedModulePaneType = tabbedModulePaneType;
    }

    public PiccoloPhetApplication( String[] args, String title, String description, String version, FrameSetup frameSetup ) {
        super( args, title, description, version, frameSetup );
    }

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
