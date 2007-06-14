/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/src/edu/colorado/phet/common/view/JTabbedModulePane.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.1 $
 * Date modified : $Date: 2006/06/02 18:04:30 $
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.ModuleEvent;
import edu.colorado.phet.common.application.PhetApplication;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * An on-screen container for the modules in an application.  It is only used for applications
 * that have more than one module. This class uses JTabbedPane as its base, and is intended for
 * use primarilly for simulations that do not use Piccolo.
 *
 * @author Sam and Ron
 * @version $Revision: 1.1 $
 */
public class JTabbedModulePane extends JTabbedPane implements ITabbedModulePane {
    private Module current;
    private PhetApplication application;

    public JTabbedModulePane() {
    }

    public void init( final PhetApplication application, final Module[] modules ) {
        this.application = application;
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int selectedIdx = getSelectedIndex();
                if( selectedIdx >= 0 && application.numModules() > 0 ) {
                    current = application.moduleAt( selectedIdx );
                    application.setActiveModule( selectedIdx );
                }
            }
        } );
        application.addModuleObserver( this );
        for( int i = 0; i < modules.length; i++ ) {
            Module module = modules[i];
            addTab( module );
        }
        setOpaque( true );
    }

    public void addTab( Module module ) {
        addTab( module.getName(), module.getModulePanel() );
    }

    public void moduleAdded( ModuleEvent event ) {
    }

    public void removeTab( Module module ) {
        for( int i = 0; i < getTabCount(); i++ ) {
            if( getTitleAt( i ).equals( module.getName() ) && getComponent( i ).equals( module.getModulePanel() ) ) {
                removeTabAt( i );
                break;
            }
        }
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

    public void moduleRemoved( ModuleEvent event ) {
    }

    public ModulePanel getModulePanel( int i ) {
        return (ModulePanel)getComponent( i );
    }
}
