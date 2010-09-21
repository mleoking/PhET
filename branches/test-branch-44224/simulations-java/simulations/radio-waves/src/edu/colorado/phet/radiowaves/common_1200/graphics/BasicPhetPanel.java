/* Copyright University of Colorado, 2003 */
package edu.colorado.phet.radiowaves.common_1200.graphics;

import java.awt.*;

import javax.swing.*;

/**
 * The content pane for the JFrame of a PhetApplication.
 */
public class BasicPhetPanel extends JPanel {

    private JComponent center;
    private JComponent east;
    private JComponent north;
    private JComponent south;
    private JDialog buttonDlg;
    private boolean fullScreen = false;

    public BasicPhetPanel( JComponent apparatusPanelContainer, JComponent controlPanel, JComponent monitorPanel, JComponent appControl ) {
        this.setLayout( new BorderLayout() );
        setApparatusPanelContainer( apparatusPanelContainer );
        setControlPanel( controlPanel );
        setMonitorPanel( monitorPanel );
        setAppControlPanel( appControl );
    }

    public JComponent getApparatusPanelContainer() {
        return center;
    }

    public void setControlPanel( JComponent panel ) {
        if ( east != null ) {
            remove( east );
        }
        east = panel;
        setPanel( panel, BorderLayout.EAST );
    }

    public void setMonitorPanel( JComponent panel ) {
        if ( north != null ) {
            remove( north );
        }
        north = panel;
        setPanel( panel, BorderLayout.NORTH );
    }

    public void setApparatusPanelContainer( JComponent panel ) {
        if ( center != null ) {
            remove( center );
        }
        center = panel;
        setPanel( panel, BorderLayout.CENTER );
    }

    public void setAppControlPanel( JComponent panel ) {
        if ( south != null ) {
            remove( south );
        }
        south = panel;
        setPanel( panel, BorderLayout.SOUTH );
    }

    private void setPanel( JComponent component, String place ) {
        if ( component != null ) {
            add( component, place );
        }
        repaint();
    }

    public void setApparatusPanel( ApparatusPanel apparatusPanel ) {
        //        getApparatusPanelContainer().remove( 0 );//TODO don't we need this line?
        getApparatusPanelContainer().add( apparatusPanel, 0 );
    }
}