/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.ModuleEvent;
import edu.colorado.phet.common.application.ModuleObserver;
import edu.colorado.phet.common.application.PhetApplication;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * An on-screen container for the modules in an application.  It is only used for applications
 * that have more than one module.
 *
 * @author Sam and Ron
 * @version $Revision$
 */
public class TabbedModulePane extends JTabbedPane implements ModuleObserver {
    private Module current;
    private PhetApplication application;

    public TabbedModulePane( final PhetApplication application, final Module[] modules ) {
        this.application = application;
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int selectedIdx = getSelectedIndex();
                if( selectedIdx >= 0 && application.getModuleManager().numModules() > 0 ) {
                    current = application.moduleAt( selectedIdx );
                    application.setActiveModule( selectedIdx );
                }
            }
        } );
        application.addModuleObserver( this );
        for( int i = 0; i < modules.length; i++ ) {
            Module module = modules[i];
            addTab( module.getName(), module.getModulePanel() );
        }
        setOpaque( true );
    }

    public void moduleRemoved( ModuleEvent event ) {
        remove( event.getModule().getModulePanel() );
    }

    public void moduleAdded( ModuleEvent event ) {
        addTab( event.getModule().getName(), event.getModule().getSimulationPanel() );
    }

    public void activeModuleChanged( ModuleEvent event ) {
        if( current != event.getModule() ) {
            int index = application.indexOf( event.getModule() );
            int numTabs = getTabCount();
            if( index < numTabs ) {
                setSelectedIndex( index );
            }
            else {
                throw new RuntimeException( "Requested illegal tab: tab count=" + numTabs + ", requestedIndex=" + index );
            }
        }
    }

}


