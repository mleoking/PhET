/* Copyright University of Colorado, 2003 */

package edu.colorado.phet.common_cck.view;

import edu.colorado.phet.common_cck.application.ApplicationModel;
import edu.colorado.phet.common_cck.view.components.menu.PhetFileMenu;

import javax.swing.*;
import java.io.IOException;

/**
 * This class contains all the elements of an application that appear on the screen.
 */
public class ApplicationView {
    private PhetFrame phetFrame;
    private ClockControlPanel clockControlPanel;

    private BasicPhetPanel basicPhetPanel;
    private ApplicationModel appDescriptor;

    public ApplicationView( ApplicationModel applicationModel, JComponent apparatusPanelContainer ) throws IOException {
        this.appDescriptor = applicationModel;
        if( applicationModel.getUseClockControlPanel() ) {
            clockControlPanel = new ClockControlPanel( applicationModel.getClock() );
        }
        basicPhetPanel = new BasicPhetPanel( apparatusPanelContainer, null, null, clockControlPanel );
        phetFrame = new PhetFrame( applicationModel );
        phetFrame.setContentPane( basicPhetPanel );
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

}
