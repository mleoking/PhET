/*, 2003.*/
package edu.colorado.phet.coreadditions_microwaves.components;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * A panel for the apparatus, large and in the left side.  A north panel for display, an east panel for controls, and a south panel for other controls.
 */
public class BasicPhetPanel extends JPanel {

    private JComponent center;
    private JComponent east;
    private JComponent north;
    private JComponent south;

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
        if( east != null ) {
            remove( east );
        }
        east = panel;
        setPanel( panel, BorderLayout.EAST );
    }

    public void setMonitorPanel( JComponent panel ) {
        if( north != null ) {
            remove( north );
        }
        north = panel;
        setPanel( panel, BorderLayout.NORTH );
    }

    public void setApparatusPanelContainer( JComponent panel ) {
        if( center != null ) {
            remove( center );
        }
        center = panel;
        setPanel( panel, BorderLayout.CENTER );
    }

    public void setAppControlPanel( JComponent panel ) {
        if( south != null ) {
            remove( south );
        }
        south = panel;
        setPanel( panel, BorderLayout.SOUTH );
    }

    private void setPanel( JComponent component, String place ) {
        if( component != null ) {
            add( component, place );
        }
        repaint();
    }

}