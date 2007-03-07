/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions_microwaves.components;

import edu.colorado.phet.common_microwaves.view.util.graphics.ImageLoader;
import edu.colorado.phet.common_microwaves.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A panel for the apparatus, large and in the left side.  A north panel for display, an east panel for controls, and a south panel for other controls.
 */
public class BasicPhetPanel extends JPanel {

    private JComponent center;
    private JComponent east;
    private JComponent north;
    private JComponent south;

    public BasicPhetPanel(JComponent apparatusPanelContainer, JComponent controlPanel, JComponent monitorPanel, JComponent appControl) {
        this.setLayout(new BorderLayout());
        setApparatusPanelContainer(apparatusPanelContainer);
        setControlPanel(controlPanel);
        setMonitorPanel(monitorPanel);
        setAppControlPanel(appControl);
    }

    public JComponent getApparatusPanelContainer() {
        return center;
    }

    public void setControlPanel(JComponent panel) {
        if (east != null) {
            remove(east);
        }
        east = panel;
        setPanel(panel, BorderLayout.EAST);
    }

    public void setMonitorPanel(JComponent panel) {
        if (north != null) {
            remove(north);
        }
        north = panel;
        setPanel(panel, BorderLayout.NORTH);
    }

    public void setApparatusPanelContainer(JComponent panel) {
        if (center != null) {
            remove(center);
        }
        center = panel;
        setPanel(panel, BorderLayout.CENTER);
    }

    public void setAppControlPanel(JComponent panel) {
        if (south != null) {
            remove(south);
        }
        south = panel;
        setPanel(panel, BorderLayout.SOUTH);
    }

    private void setPanel(JComponent component, String place) {
        if (component != null) {
            add(component, place);
        }
        repaint();
    }


    public void toggleFullScreen() {
        if( east.isVisible() ) {
            east.setVisible( false );
            north.setVisible( false );
            south.setVisible( false );

            final JDialog buttonDlg = new JDialog();
            buttonDlg.setTitle( SimStrings.get( "BasicPhetPanel.ReturnButton" ) );
            buttonDlg.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
            ImageIcon logo = new ImageIcon( new ImageLoader().loadImage( "images/Phet-logo-48x48.gif" ));
            JButton logoButton = new JButton( logo );
            logoButton.setPreferredSize( new Dimension( logo.getIconWidth() + 12, logo.getIconHeight() + 12 ));
            logoButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    toggleFullScreen();
                    buttonDlg.setVisible( false );
                }
            } );
            buttonDlg.getContentPane().setLayout( new FlowLayout( FlowLayout.CENTER ));
            buttonDlg.getContentPane().add( logoButton );
            Rectangle thisBounds = this.getBounds();
            buttonDlg.pack();
            buttonDlg.setLocation( (int)( this.getLocationOnScreen().getX() + thisBounds.getMaxX() - buttonDlg.getWidth() ),
                                   (int)( this.getLocationOnScreen().getY() + thisBounds.getMaxY() - buttonDlg.getHeight() ));
            buttonDlg.setVisible( true );
        }
        else {
            east.setVisible( true );
            north.setVisible( true );
            south.setVisible( true );
        }
    }
}