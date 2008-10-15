/* Copyright University of Colorado, 2003 */
package edu.colorado.phet.radiowaves.common_1200.graphics;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.ModuleEvent;
import edu.colorado.phet.common.phetcommon.application.ModuleObserver;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;

/**
 * An on-screen container for the modules in an application. It displays the
 * modules' apparatus panels in tabbed panes. It is only used for applications
 * that have more than one module.
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

    public void moduleAdded( Module module ) {
        addTab( module.getName(), module.getSimulationPanel() );
    }

    public void activeModuleChanged( Module m ) {
        if( current != m ) {
            int index = application.indexOf( m );
            setSelectedIndex( index );
        }
    }

    public void moduleRemoved( Module m ) {
//        removeTabAt( );
        throw new RuntimeException( "Module removal is not yet handled." );
    }

    public void activeModuleChanged( ModuleEvent event ) {
        // TODO Auto-generated method stub
        
    }

    public void moduleAdded( ModuleEvent event ) {
        // TODO Auto-generated method stub
        
    }

    public void moduleRemoved( ModuleEvent event ) {
        // TODO Auto-generated method stub
        
    }

}


