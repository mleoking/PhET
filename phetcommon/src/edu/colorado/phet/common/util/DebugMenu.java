/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.util.LineGrid;
import edu.colorado.phet.common.view.util.MouseTracker;
import edu.colorado.phet.common.application.PhetApplication;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.HashMap;

/**
 * DebugMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DebugMenu extends JMenu {
    private HashMap appPanelsToGrids = new HashMap();
    private PhetApplication app;

    public DebugMenu( final PhetApplication app ) {
        super( "Debug" );
        this.app = app;
        setMnemonic( 'D' );

        this.add( new GridMenuItem() );
        this.add( new MouseTrackerMenuItem() );
    }

    private ApparatusPanel getApparatusPanel() {
        ApparatusPanel appPanel = app.getModuleManager().getActiveModule().getApparatusPanel();
        return appPanel;
    }


    //----------------------------------------------------------------
    // Menu Items
    //----------------------------------------------------------------

    private class GridMenuItem extends JCheckBoxMenuItem {
        public GridMenuItem() {
            super( "Grid" );
            this.setMnemonic( 'G' );
            this.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ApparatusPanel appPanel = getApparatusPanel();
                    if( appPanel instanceof ApparatusPanel2 ) {
                        ApparatusPanel2 appPanel2 = (ApparatusPanel2)appPanel;
                        if( isSelected() ) {
                            LineGrid grid = new LineGrid( appPanel2, 100, 100, new Color( 0, 128, 0 ) );
                            appPanelsToGrids.put( appPanel2, grid );
                            appPanel2.addGraphic( grid );
                            appPanel.repaint();
                        }
                        else {
                            LineGrid grid = (LineGrid)appPanelsToGrids.get( appPanel2 );
                            appPanel2.removeGraphic( grid );
                            appPanelsToGrids.remove( appPanel2 );
                            appPanel2.repaint();
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog( app.getPhetFrame(), "<html>This option only applies to modules<br>that use ApparatusPanel2</html>" );
                    }
                }
            } );
        }

    }

    private class MouseTrackerMenuItem extends JCheckBoxMenuItem {
        private HashMap appPanelToTracker = new HashMap();

        public MouseTrackerMenuItem() {
            super( "Mouse tracker " );
            this.setMnemonic( 'M' );
            this.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ApparatusPanel appPanel = getApparatusPanel();
                    if( appPanel instanceof ApparatusPanel2 ) {
                        ApparatusPanel2 appPanel2 = (ApparatusPanel2)appPanel;
                        if( isSelected() ) {
                            MouseTracker tracker = new MouseTracker( appPanel2 );
                            appPanel.addGraphic( tracker, Double.MAX_VALUE );
                            appPanelToTracker.put( appPanel2, tracker );
                        }
                        else {
                            MouseTracker tracker = (MouseTracker)appPanelToTracker.get( appPanel );
                            appPanel.removeGraphic( tracker );
                            appPanelToTracker.remove( appPanel );
                        }
                    }
                }
            } );
        }
    }
}
