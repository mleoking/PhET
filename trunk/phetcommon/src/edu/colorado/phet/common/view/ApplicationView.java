/*Copyright, Sam Reid, 2003.  Editing.*/
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.ModuleManager;
import edu.colorado.phet.common.application.ModuleObserver;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.apparatuspanelcontainment.ApparatusPanelContainer;
import edu.colorado.phet.common.view.components.menu.PhetFileMenu;

import javax.swing.*;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 12, 2003
 * Time: 7:27:29 AM
 * Copyright (c) Jun 12, 2003 by Sam Reid
 */
public class ApplicationView {
    private PhetFrame phetFrame;
    private ApparatusPanelContainer apparatusPanelContainer;
    private ApplicationModelControlPanel controlPanel;

    private BasicPhetPanel basicPhetPanel;
    private PhetApplication application;

    public ApplicationView( PhetApplication application ) throws IOException {
        this.application = application;
        apparatusPanelContainer = application.getContainerStrategy().createApparatusPanelContainer( application.getModuleManager() );

        if( application.getClock() == null ) {
            throw new RuntimeException( "Clock is null" );
        }
        controlPanel = new ApplicationModelControlPanel( application.getClock() );
        basicPhetPanel = new BasicPhetPanel( null, null, null, controlPanel );
        basicPhetPanel.setApparatusPanelContainer( apparatusPanelContainer.getComponent() );
        new ControlAndMonitorSwapper( basicPhetPanel, application.getModuleManager() );
        phetFrame = new PhetFrame( application );
        phetFrame.setContentPane( basicPhetPanel );
    }

    public ApparatusPanelContainer getApparatusPanelContainer() {
        return apparatusPanelContainer;
    }

    public BasicPhetPanel getBasicPhetPanel() {
        return basicPhetPanel;
    }

    public void setVisible( boolean isVisible ) {
        phetFrame.setVisible( isVisible );
        application.getApplicationDescriptor().getFrameSetup().initialize( phetFrame);
    }

    public void addFileMenuItem( JMenuItem menuItem ) {
        phetFrame.addFileMenuItem( menuItem );
    }

    public void addFileMenuSeparator() {
        phetFrame.addFileMenuSeparator();
    }

    public void removeFileMenuItem( JMenuItem menuItem ) {
        phetFrame.removeFileMenuItem( menuItem );
    }

    public void setFileMenu( PhetFileMenu fileMenu ) {
        this.phetFrame.setFileMenu( fileMenu );
    }

    public void removeFileMenu( PhetFileMenu fileMenu ) {
        this.phetFrame.removeFileMenuItem( fileMenu );
    }

    public PhetFrame getPhetFrame() {
        return this.phetFrame;
    }

    public void setFullScreen(boolean fullScreen){
        basicPhetPanel.setFullScreen(fullScreen);
    }

    public static boolean moduleIsWellFormed( Module module ) {
        boolean result = true;
        result &= module.getModel() != null;
        result &= module.getApparatusPanel() != null;
        return result;
    }

    private class ControlAndMonitorSwapper implements ModuleObserver {
        BasicPhetPanel bpp;
        ModuleManager mm;

        public ControlAndMonitorSwapper( BasicPhetPanel bpp, ModuleManager mm ) {
            this.bpp = bpp;
            this.mm = mm;
            mm.addModuleObserver( this );
        }

        public void moduleAdded( Module m ) {
        }

        public void activeModuleChanged( Module m ) {
            bpp.setControlPanel( m.getControlPanel() );
            bpp.setMonitorPanel( m.getMonitorPanel() );
        }
    }
}
