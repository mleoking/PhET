/*, 2003.  Editing.*/
package edu.colorado.phet.semiconductor.phetcommon.view;

import edu.colorado.phet.semiconductor.phetcommon.application.Module;
import edu.colorado.phet.semiconductor.phetcommon.application.ModuleManager;
import edu.colorado.phet.semiconductor.phetcommon.application.ModuleObserver;
import edu.colorado.phet.semiconductor.phetcommon.application.PhetApplication;
import edu.colorado.phet.semiconductor.phetcommon.view.apparatuspanelcontainment.ApparatusPanelContainer;

import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 12, 2003
 * Time: 7:27:29 AM
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
        application.getApplicationDescriptor().getFrameSetup().initialize( phetFrame );
    }

    public void setFullScreen( boolean fullScreen ) {
        basicPhetPanel.setFullScreen( fullScreen );
    }

    private class ControlAndMonitorSwapper implements ModuleObserver {
        BasicPhetPanel bpp;

        public ControlAndMonitorSwapper( BasicPhetPanel bpp, ModuleManager mm ) {
            this.bpp = bpp;
            mm.addModuleObserver( this );
        }

        public void moduleAdded( Module m ) {
        }

        public void activeModuleChanged( Module m ) {
            bpp.setControlPanel( m.getControlPanel() );
//            bpp.setMonitorPanel( m.getMonitorPanel() );
        }
    }

    public PhetFrame getPhetFrame() {
        return phetFrame;
    }
}
