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

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.help.HelpPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * ContentPanel
 * <p/>
 * The content pane for the JFrame of a PhetApplication. It holds the apparatus panel container (a tabbed pane
 * container that holds all the apparatus panels), the control panel, and the simulation clock control panel.
 *
 * @author ?
 * @version $Revision$
 */
public class ContentPanel extends JPanel {

    private static Image phetLogo;

    static {
        try {
            phetLogo = new ImageLoader().loadImage( "images/Phet-logo-48x48.gif" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private JComponent apparatusPanel;
    private JComponent controlPanel;
    private JComponent monitorPanel;
    private JComponent clockControlPanel;
    private JDialog buttonDlg;
    private boolean fullScreen = false;

    private GridBagConstraints appCtrlGbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                                    GridBagConstraints.WEST,
                                                                    GridBagConstraints.BOTH,
                                                                    new Insets( 0, 0, 0, 0 ), 0, 0 );
    private GridBagConstraints apparatusPanelGbc = new GridBagConstraints( 0, 1, 1, 1, 1, 1,
                                                                           GridBagConstraints.WEST,
                                                                           GridBagConstraints.BOTH,
                                                                           new Insets( 0, 0, 0, 0 ), 0, 0 );
    private GridBagConstraints controlPanelGbc = new GridBagConstraints( 1, 1, 1, 1, 0, 0,
                                                                         GridBagConstraints.NORTH,
                                                                         GridBagConstraints.NONE,
//                                                                         GridBagConstraints.BOTH,
                                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
    private GridBagConstraints clockControlPanelGbc = new GridBagConstraints( 0, 2, 1, 1, 1, 0,
                                                                              GridBagConstraints.SOUTH,
                                                                              GridBagConstraints.HORIZONTAL,
                                                                              new Insets( 0, 0, 0, 0 ), 0, 0 );
    private GridBagConstraints monitorPanelGbc = new GridBagConstraints( 0, 1, 1, 1, 0, 0,
                                                                         GridBagConstraints.PAGE_END,
                                                                         GridBagConstraints.NONE,
                                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
//    private JSplitPane appCtrlPane;


    /**
     * @param apparatusPanelContainer
     * @param controlPanel
     * @param monitorPanel
     * @param appControl
     */
    public ContentPanel( JComponent apparatusPanelContainer, JComponent controlPanel,
                         JComponent monitorPanel, JComponent appControl ) {
        setLayout( new GridBagLayout() );

//        appCtrlPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, apparatusPanelContainer, controlPanel );
//        add( appCtrlPane, appCtrlGbc );
//        appCtrlPane.setResizeWeight( 1 );

//        setLayout( new BorderLayout() );
        setApparatusPanelContainer( apparatusPanelContainer );
        setMonitorPanel( monitorPanel );
        setAppControlPanel( appControl );
    }

    /**
     * @param clockControlPanel
     */
    public ContentPanel( PhetApplication application, JComponent clockControlPanel ) {
        setLayout( new GridBagLayout() );
        JComponent apparatusPanelContainer = createApparatusPanelContainer( application );
        setApparatusPanelContainer( apparatusPanelContainer );
        setAppControlPanel( clockControlPanel );
    }

    public JComponent getApparatusPanelContainer() {
        return apparatusPanel;
    }

    public void setControlPanel( JComponent panel ) {
        if( panel != null ) {
//            appCtrlPane.setRightComponent( panel );
        }
        if( controlPanel != null ) {
            remove( controlPanel );
        }
        controlPanel = panel;
        setPanel( panel, controlPanelGbc );
//        add( panel, BorderLayout.EAST );
    }

    public void setMonitorPanel( JComponent panel ) {
        if( monitorPanel != null ) {
            remove( monitorPanel );
        }
        monitorPanel = panel;
        setPanel( panel, monitorPanelGbc );
    }

    public void setApparatusPanelContainer( JComponent panel ) {
        if( apparatusPanel != null ) {
            remove( apparatusPanel );
        }
        apparatusPanel = panel;
//        add( panel, BorderLayout.CENTER );
        setPanel( panel, apparatusPanelGbc );
    }

//    public void setApparatusPanel( ApparatusPanel apparatusPanel ) {
//        getApparatusPanelContainer().add( apparatusPanel, 0 );
//    }

    public void setAppControlPanel( JComponent panel ) {
        if( clockControlPanel != null ) {
            remove( clockControlPanel );
        }
        clockControlPanel = panel;
        setPanel( clockControlPanel, clockControlPanelGbc );
//        if( panel != null ) {
//            add( panel, BorderLayout.SOUTH );
//        }
    }

    private void setPanel( JComponent component, GridBagConstraints gridBagConstraints ) {
        if( component != null ) {
            add( component, gridBagConstraints );
        }
        revalidate();
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
        if( controlPanel != null ) {
            controlPanel.setVisible( true );
        }
        if( monitorPanel != null ) {
            monitorPanel.setVisible( true );
        }
        if( clockControlPanel != null ) {
            clockControlPanel.setVisible( true );
        }
        this.fullScreen = false;
    }

    private void activateFullScreen() {
        if( controlPanel != null ) {
            controlPanel.setVisible( false );
        }
        if( monitorPanel != null ) {
            monitorPanel.setVisible( false );
        }
        if( clockControlPanel != null ) {
            clockControlPanel.setVisible( false );
        }

        if( buttonDlg == null ) {
            buttonDlg = new JDialog();
            buttonDlg.setTitle( SimStrings.get( "Common.BasicPhetPanel.Title" ) );
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
            logoButton.setToolTipText( SimStrings.get( "Common.BasicPhetPanel.LogoToolTip" ) );
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

    /**
     * This method is used in the older mechanism for application startup that used a public ApplicationModel
     *
     * @param application
     * @return
     * @deprecated
     */
    private JComponent createApparatusPanelContainer( PhetApplication application ) {
        if( application.numModules() == 1 ) {
            JPanel apparatusPanelContainer = new JPanel();
            apparatusPanelContainer.setLayout( new GridLayout( 1, 1 ) );
            if( application.moduleAt( 0 ).getApparatusPanel() == null ) {
                throw new RuntimeException( "Null Apparatus Panel in Module: " + application.moduleAt( 0 ).getName() );
            }
            apparatusPanelContainer.add( application.moduleAt( 0 ).getApparatusPanel() );
            return apparatusPanelContainer;
        }
        else {
            JComponent apparatusPanelContainer = new TabbedApparatusPanelContainer( application );
            return apparatusPanelContainer;
        }
    }
}