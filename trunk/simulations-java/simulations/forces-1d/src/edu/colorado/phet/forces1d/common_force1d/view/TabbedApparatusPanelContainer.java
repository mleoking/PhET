/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.common_force1d.view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.forces1d.common_force1d.application.Module;
import edu.colorado.phet.forces1d.common_force1d.application.ModuleEvent;
import edu.colorado.phet.forces1d.common_force1d.application.ModuleObserver;
import edu.colorado.phet.forces1d.common_force1d.application.PhetApplication;

/**
 * An on-screen container for the modules in an application. It displays the
 * modules' apparatus panels in tabbed panes. It is only used for applications
 * that have more than one module.
 *
 * @author ?
 * @version $Revision$
 */
public class TabbedApparatusPanelContainer extends JTabbedPane implements ModuleObserver {
    Module current;
    private PhetApplication application;

    public TabbedApparatusPanelContainer( final PhetApplication application ) {
        this.application = application;
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int selectedIdx = getSelectedIndex();
                current = application.moduleAt( selectedIdx );
                application.setActiveModule( selectedIdx );
            }
        } );
        application.addModuleObserver( this );
    }

    //----------------------------------------------------------------
    // ModuleObserver implementation
    //----------------------------------------------------------------

    public void moduleRemoved( ModuleEvent event ) {
        remove( event.getModule().getApparatusPanel() );
    }

    public void moduleAdded( ModuleEvent event ) {
        addTab( event.getModule().getName(), event.getModule().getApparatusPanel() );
    }

    public void activeModuleChanged( ModuleEvent event ) {
        if ( current != event.getModule() ) {
            int index = application.indexOf( event.getModule() );
            int numTabs = getTabCount();
            if ( index < numTabs ) {
                setSelectedIndex( index );
            }
            else {
                throw new RuntimeException( "Requested illegal tab: tab count=" + numTabs + ", requestedIndex=" + index );
            }
        }
    }

}


