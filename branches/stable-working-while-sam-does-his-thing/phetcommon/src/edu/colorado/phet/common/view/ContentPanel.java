/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * The content pane for the JFrame of a PhetApplication.
 *
 * @author ?
 * @version $Revision$
 */
public class ContentPanel extends JPanel {

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
            e.printStackTrace();
        }
    }

    public ContentPanel( JComponent apparatusPanelContainer, JComponent controlPanel, JComponent monitorPanel, JComponent appControl ) {
        this.setLayout( new GridBagLayout() );
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
        GridBagConstraints gbc = new GridBagConstraints( 1, 0, 1, 3, 1, 1000,
                                                         GridBagConstraints.NORTHEAST, GridBagConstraints.VERTICAL,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        if( panel != null ) {
//            panel.setBorder( new EtchedBorder( ) );
            add( panel, gbc );
        }
    }

    public void setApparatusPanelContainer( JComponent panel ) {
        if( center != null ) {
            remove( center );
        }
        center = panel;
        GridBagConstraints gbc = new GridBagConstraints( 0, 1, 1, 1, 100, 100,
                                                         GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        if( panel != null ) {
//            panel.setBorder( new EtchedBorder( ) );
            add( panel, gbc );
        }
    }

    public void setAppControlPanel( JComponent panel ) {
        if( south != null ) {
            remove( south );
        }
        south = panel;
        GridBagConstraints gbc = new GridBagConstraints( 0, 2, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        if( panel != null ) {
//            panel.setBorder( new EtchedBorder( ) );
            add( panel, gbc );
        }
    }

    public void setMonitorPanel( JComponent panel ) {
        if( north != null ) {
            remove( north );
        }
        north = panel;
        GridBagConstraints gbc = new GridBagConstraints( 0, 1, 2, 1, 1, 1,
                                                         GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        if( panel != null ) {
//            panel.setBorder( new EtchedBorder( ) );
            add( panel, gbc );
        }
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
            buttonDlg.setTitle( SimStrings.get( "Common.ContentPanel.Title" ) );
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
            logoButton.setToolTipText( SimStrings.get( "Common.ContentPanel.LogoToolTip" ) );
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

    public void setApparatusPanel( ApparatusPanel apparatusPanel ) {
        //        getApparatusPanelContainer().remove( 0 );//TODO don't we need this line?
        getApparatusPanelContainer().add( apparatusPanel, 0 );
    }
}