/* Copyright University of Colorado, 2003 */
package edu.colorado.phet.common.view.apparatuspanelcontainment;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.ModuleManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * An on-screen container for the modules in an application. It displays the
 * modules' apparatus panels in tabbed panes. It is only used for applications
 * that have more than one module.
 */
//public class TabbedApparatusPanelContainer extends JTabbedPane implements ApparatusPanelContainer {
public class TabbedApparatusPanelContainer extends JPanel implements ApparatusPanelContainer {
    ModuleManager moduleManager;
    Module current;
    JTabbedPane tabbedPane = new JTabbedPane();

    public TabbedApparatusPanelContainer( final ModuleManager moduleManager ) {
        this.setLayout( new BorderLayout() );
        this.moduleManager = moduleManager;
        //        this.addChangeListener( new ChangeListener() {
        tabbedPane.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                //                int selectedIdx = getSelectedIndex();
                int selectedIdx = tabbedPane.getSelectedIndex();
                current = moduleManager.moduleAt( selectedIdx );
                moduleManager.setActiveModule( selectedIdx );
            }
        } );
        moduleManager.addModuleObserver( this );
        add( tabbedPane );
    }

    public void moduleAdded( Module module ) {
        //        this.addTab( module.getName(), module.getApparatusPanel() );
        tabbedPane.addTab( module.getName(), module.getApparatusPanel() );
    }

    public void activeModuleChanged( Module m ) {
        if( current != m ) {
            int index = moduleManager.indexOf( m );
            //            this.setSelectedIndex( index );
            tabbedPane.setSelectedIndex( index );
            //            m.getApparatusPanel().updateTransform();
        }
    }

    public JComponent getComponent() {
        //        return this;
        return tabbedPane;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }
}


