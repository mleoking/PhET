/**
 * Class: SingleApparatusPanelContainer
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: Jun 11, 2003
 */
package edu.colorado.phet.semiconductor.phetcommon.view.apparatuspanelcontainment;

import javax.swing.*;

import edu.colorado.phet.semiconductor.phetcommon.application.Module;
import edu.colorado.phet.semiconductor.phetcommon.application.ModuleManager;
import edu.colorado.phet.semiconductor.phetcommon.view.ApparatusPanel;
import edu.colorado.phet.semiconductor.phetcommon.view.util.AspectRatioLayout;

/**
 * An apparatus panel container for applications that have only one
 * module.
 */
public class SingleApparatusPanelContainer implements ApparatusPanelContainer {

    private ApparatusPanel apparatusPanel;
    JPanel container = new JPanel();

    public SingleApparatusPanelContainer( ModuleManager mm ) {
        mm.addModuleObserver( this );
//        container.setLayout(new AspectRatioLayout(null, 10, 10));
//        container.setLayout( new BorderLayout() );
    }

    public JComponent getComponent() {
        return container;
    }

    public void moduleAdded( Module m ) {
    }

    public void activeModuleChanged( Module m ) {
        if ( apparatusPanel != null ) {
            container.remove( apparatusPanel );
        }
        this.apparatusPanel = m.getApparatusPanel();
//        this.container.add( m.getApparatusPanel(), BorderLayout.CENTER );
        container.setLayout( new AspectRatioLayout( apparatusPanel, 10, 10 ) );
        this.container.add( m.getApparatusPanel() );
    }

    public static ApparatusPanelContainerFactory getFactory() {
        return new Factory();
    }

    private static class Factory implements ApparatusPanelContainerFactory {
        public ApparatusPanelContainer createApparatusPanelContainer( ModuleManager manager ) {
            return new SingleApparatusPanelContainer( manager );
        }
    }
}
