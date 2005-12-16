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
import java.awt.event.*;
import java.io.IOException;

/**
 * ContentPanel
 * <p/>
 * The content pane for the JFrame of a PhetApplication. It holds the apparatus panel container (a tabbed pane
 * container that holds all the apparatus panels), the control panel, and the simulation clock control panel.
 *
 * @author Ron & Sam
 * @version $Revision$
 */
public class ModulePanel extends JPanel {

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
    private ControlPanel controlPanel;
    private JComponent monitorPanel;
    private JComponent clockControlPanel;
    private JDialog buttonDlg;
    private boolean fullScreen = false;

    private GridBagConstraints apparatusPanelGbc = new GridBagConstraints( 0, 1, 1, 1, 1, 1,
                                                                           GridBagConstraints.WEST,
                                                                           GridBagConstraints.BOTH,
                                                                           new Insets( 0, 0, 0, 0 ), 0, 0 );
    // The control panel has a gridheight of 2 so the Help button comes up at the same level as the
    // simulation clock control buttons
    private GridBagConstraints controlPanelGbc = new GridBagConstraints( 1, 1, 1, 2, 0, 1000,
                                                                         GridBagConstraints.NORTH,
                                                                         GridBagConstraints.BOTH,
                                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
    private GridBagConstraints clockControlPanelGbc = new GridBagConstraints( 0, 2, 1, 1, 1, 0,
                                                                              GridBagConstraints.SOUTH,
                                                                              GridBagConstraints.HORIZONTAL,
                                                                              new Insets( 0, 0, 0, 0 ), 0, 0 );
    private GridBagConstraints monitorPanelGbc = new GridBagConstraints( 0, 1, 1, 1, 0, 0,
                                                                         GridBagConstraints.PAGE_END,
                                                                         GridBagConstraints.NONE,
                                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );


    /**
     * @param simulationPanel
     * @param controlPanel
     * @param monitorPanel
     * @param appControl
     */
    public ModulePanel( JComponent simulationPanel, JComponent controlPanel,
                        JComponent monitorPanel, JComponent appControl ) {
        setLayout( new GridBagLayout() );

        // Use this code to put the apparatus panel and control panel in split panes
//        appCtrlPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, apparatusPanelContainer, controlPanel );
//        add( appCtrlPane, appCtrlGbc );
//        appCtrlPane.setResizeWeight( 1 );

        setSimulationPanel( simulationPanel );
        setMonitorPanel( monitorPanel );
        setClockControlPanel( appControl );
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayoutContentPanel();
            }

            public void componentShown( ComponentEvent e ) {
                relayoutContentPanel();
            }
        } );
        relayoutContentPanel();
        addContainerListener( new ContainerListener() {
            public void componentAdded( ContainerEvent e ) {
                relayoutContentPanel();
            }

            public void componentRemoved( ContainerEvent e ) {
                relayoutContentPanel();
            }
        } );
    }

    private void relayoutContentPanel() {
        invalidate();
        validateTree();
        doLayout();
        repaint();
    }

    public JComponent getApparatusPanelContainer() {
        return apparatusPanel;
    }

    public void setControlPanel( ControlPanel panel ) {
        if( controlPanel != null ) {
            remove( controlPanel );
        }
        controlPanel = panel;
        setPanel( panel, controlPanelGbc );
    }

    public void setMonitorPanel( JComponent panel ) {
        if( monitorPanel != null ) {
            remove( monitorPanel );
        }
        monitorPanel = panel;
        setPanel( panel, monitorPanelGbc );
    }

    public void setSimulationPanel( JComponent panel ) {
        if( apparatusPanel != null ) {
            remove( apparatusPanel );
        }
        apparatusPanel = panel;
        setPanel( panel, apparatusPanelGbc );
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

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    public void setClockControlPanel( JComponent clockControlPanel ) {
        if( clockControlPanel != null ) {
            remove( clockControlPanel );
        }
        this.clockControlPanel = clockControlPanel;
        setPanel( clockControlPanel, clockControlPanelGbc );
    }
}