/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.view.util.graphics.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * A panel for the apparatus, large and in the left side.  A north panel for display, an east panel for controls, and a south panel for other controls.
 */
public class BasicPhetPanel extends JPanel {

    private JComponent center;
    private JComponent east;
    private JComponent north;
    private JComponent south;
    private static Image phetLogo;
    private JDialog buttonDlg;
    private boolean fullScreen = false;

    static {
        try {
            phetLogo = new ImageLoader().loadImage( "images/Phet-logo-48x48.gif" );
        }
        catch( IOException e ) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }

    public BasicPhetPanel( JComponent apparatusPanelContainer, JComponent controlPanel, JComponent monitorPanel, JComponent appControl ) {
        this.setLayout( new BorderLayout() );
        setApparatusPanelContainer( apparatusPanelContainer );
        setControlPanel( controlPanel );
        setMonitorPanel( monitorPanel );
        setAppControlPanel( appControl );
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

    public void setFullScreen( boolean fullScreen ) {
        if( fullScreen && !isFullScreen() ) {
            activateFullScreen();
        }
        else if( !fullScreen && isFullScreen() ) {
            deactivateFullScreen();
        }
    }

    private void deactivateFullScreen() {
        if( east != null ) {
            east.setVisible( true );
        }
        if( north != null ) {
            north.setVisible( true );
        }
        if( south != null ) {
            south.setVisible( true );
        }
        this.fullScreen = false;
    }

    private void activateFullScreen() {
        if( east != null ) {
            east.setVisible( false );
        }
        if( north != null ) {
            north.setVisible( false );
        }
        if( south != null ) {
            south.setVisible( false );
        }

        if( buttonDlg == null ) {
            buttonDlg = new JDialog();
            buttonDlg.setTitle( "Return" );
            buttonDlg.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
            ImageIcon logo = new ImageIcon( phetLogo );
            JButton logoButton = new JButton( logo );
            logoButton.setPreferredSize( new Dimension( logo.getIconWidth() + 12, logo.getIconHeight() + 12 ) );
            logoButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setFullScreen( false );
                    buttonDlg.setVisible( false );
                }
            } );
            logoButton.setToolTipText( "Disable Full Frame" );
            buttonDlg.getContentPane().setLayout( new FlowLayout( FlowLayout.CENTER ) );
            buttonDlg.getContentPane().add( logoButton );
            Rectangle thisBounds = this.getBounds();
            buttonDlg.pack();
            buttonDlg.setLocation( (int)( this.getLocationOnScreen().getX() + thisBounds.getMaxX() - buttonDlg.getWidth() ),
                                   (int)( this.getLocationOnScreen().getY() + thisBounds.getMaxY() - buttonDlg.getHeight() ) );
        }
        buttonDlg.setVisible( true );
        this.fullScreen = true;
    }

    private boolean isFullScreen() {
        return fullScreen;
    }
}