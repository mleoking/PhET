/* Copyright University of Colorado, 2003 */

package edu.colorado.phet.common.view;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.apparatuspanelcontainment.ApparatusPanelContainer;
import edu.colorado.phet.common.view.components.menu.PhetFileMenu;

import javax.swing.*;
import java.io.IOException;

/**
 * This class contains all the elements of an application that appear on the screen.
 */
public class ApplicationView {
    private PhetFrame phetFrame;
    private ApparatusPanelContainer apparatusPanelContainer;
    private ApplicationModelControlPanel controlPanel;

    private BasicPhetPanel basicPhetPanel;
    private ApplicationDescriptor appDescriptor;

    public ApplicationView( ApplicationDescriptor appDescriptor, JComponent apparatusPanelContainer, AbstractClock clock ) throws IOException {
        this.appDescriptor = appDescriptor;
        controlPanel = new ApplicationModelControlPanel( clock );
        basicPhetPanel = new BasicPhetPanel( apparatusPanelContainer, null, null, controlPanel );
        phetFrame = new PhetFrame( appDescriptor );
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
        appDescriptor.getFrameSetup().initialize( phetFrame );
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

    public void setFullScreen( boolean fullScreen ) {
        basicPhetPanel.setFullScreen( fullScreen );
    }

    public static boolean moduleIsWellFormed( Module module ) {
        boolean result = true;
        result &= module.getModel() != null;
        result &= module.getApparatusPanel() != null;
        return result;
    }
}
