/*, 2003.*/
package edu.colorado.phet.common_microwaves.view;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common_microwaves.application.Module;
import edu.colorado.phet.common_microwaves.application.ModuleManager;
import edu.colorado.phet.common_microwaves.application.ModuleObserver;
import edu.colorado.phet.common_microwaves.application.PhetApplication;
import edu.colorado.phet.common_microwaves.view.apparatuspanelcontainment.ApparatusPanelContainer;
import edu.colorado.phet.common_microwaves.view.components.menu.PhetFileMenu;
import edu.colorado.phet.coreadditions_microwaves.components.ApplicationModelControlPanel;
import edu.colorado.phet.coreadditions_microwaves.components.BasicPhetPanel;
import edu.colorado.phet.coreadditions_microwaves.components.PhetFrame;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Jun 12, 2003
 * Time: 7:27:29 AM
 */
public class ApplicationView {
    private PhetFrame phetFrame;
    private ApparatusPanelContainer appPnlContainer;
    private ApplicationModelControlPanel controlPanel;

    private BasicPhetPanel basicPhetPanel;
    //    private ApparatusPanelContainerFactory containerStrategy;
    private PhetApplication application;

    public ApplicationView( PhetApplication application ) {
        this.application = application;
//        ApparatusPanelContainer appPnlContainer;
        appPnlContainer = application.getContainerStrategy().createApparatusPanelContainer( application.getModuleManager() );
        controlPanel = new ApplicationModelControlPanel( application.getApplicationModel() );
        basicPhetPanel = new BasicPhetPanel( null, null, null, controlPanel );
        basicPhetPanel.setApparatusPanelContainer( appPnlContainer.getComponent() );
        new ControlAndMonitorSwapper( basicPhetPanel, application.getModuleManager() );
        phetFrame = new PhetFrame( application );
        phetFrame.setContentPane( basicPhetPanel );

//        final ClockDialog cd = new ClockDialog( phetFrame, (DynamicClock)application.getApplicationModel().getClock() );

//        JMenu menu = new JMenu( "Controls" );
//        JMenuItem clockDialogItem = new JMenuItem( "FixedClock Parameters" );
//        menu.add( clockDialogItem );
//        clockDialogItem.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                cd.setVisible( true );
//            }
//        } );
//        phetFrame.addMenu( menu );
//        edu.colorado.phet.common.view.util.GraphicsUtil.centerDialogInParent( cd );
        application.getModuleManager().addModuleObserver( new ModuleObserver() {
            public void moduleAdded( Module m ) {
            }

            public void activeModuleChanged( Module m ) {
                Resettable r = m.getResettable();
                controlPanel.setResettable( r );
            }
        } );
    }

    public BasicPhetPanel getBasicPhetPanel() {
        return basicPhetPanel;
    }

    public void setVisible( boolean isVisible ) {
        phetFrame.setVisible( isVisible );
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

    public void removeFileMenuSeparator() {
//        phetFrame.removeFileMenuSeparator();
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
