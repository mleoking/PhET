/**
 * Class: TabbedApparatusPanelContainer
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.greenhouse.common_greenhouse.view.apparatuspanelcontainment;

import edu.colorado.phet.greenhouse.common_greenhouse.application.Module;
import edu.colorado.phet.greenhouse.common_greenhouse.application.ModuleManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A container for the modules in an application. It displays the
 * modules' apparatus panels in tabbed panes.
 */
public class TabbedApparatusPanelContainer implements ApparatusPanelContainer {
    ModuleManager mm;
    Module current;
    JTabbedPane tabbedPane = new JTabbedPane();

    public TabbedApparatusPanelContainer( final ModuleManager mm ) {
        this.mm = mm;
        tabbedPane.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int selectedIdx = tabbedPane.getSelectedIndex();
                current = mm.moduleAt( selectedIdx );
                mm.setActiveModule( selectedIdx );
            }
        } );
        mm.addModuleObserver( this );
    }

    public void moduleAdded( Module module ) {
        tabbedPane.addTab( module.getName(), module.getApparatusPanel() );
    }

    public void activeModuleChanged( Module m ) {
        if( current != m ) {
            int index = mm.indexOf( m );
            tabbedPane.setSelectedIndex( index );
//            m.getApparatusPanel().updateTransform();
        }
    }

    public JComponent getComponent() {
        return tabbedPane;
    }


    public static ApparatusPanelContainerFactory getFactory() {
        return new Factory();
    }

    public static class Factory implements ApparatusPanelContainerFactory {
        public ApparatusPanelContainer createApparatusPanelContainer( ModuleManager manager ) {
            return new TabbedApparatusPanelContainer( manager );
        }
    }
}


