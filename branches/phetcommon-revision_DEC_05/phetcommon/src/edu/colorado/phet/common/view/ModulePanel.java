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
 * The Swing panel for a Module in a PhetApplication. It holds
 * the control panel, the simulation panel, the clock control panel, and a monitor panel.
 * <p/>
 * All panels are optional (can be null).
 *
 * @author Ron & Sam
 * @version $Revision$
 */
public class ModulePanel extends JPanel {

    /* Static Data*/
    private static Image phetLogo = loadPhetLogo();

    private static Image loadPhetLogo() {
        try {
            return ImageLoader.loadBufferedImage( "images/Phet-logo-48x48.gif" );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    /* Instance Data*/
    private JComponent simulationPanel;
    private ControlPanel controlPanel;
    private JComponent monitorPanel;
    private JComponent clockControlPanel;
    private JDialog buttonDlg;
    private boolean fullScreen = false;

    static Insets createInsets() {
        return new Insets( 0, 0, 0, 0 );
    }

    private GridBagConstraints apparatusPanelConstraints = new GridBagConstraints( 0, 1, 1, 1, 1, 1,
                                                                                   GridBagConstraints.WEST,
                                                                                   GridBagConstraints.BOTH,
                                                                                   createInsets(), 0, 0 );
    // The control panel has a gridheight of 2 so the Help button comes up at the same level as the
    // simulation clock control buttons
    private GridBagConstraints controlPanelConstraints = new GridBagConstraints( 1, 1, 1, 2, 0, 1000,
                                                                                 GridBagConstraints.NORTH,
                                                                                 GridBagConstraints.BOTH,
                                                                                 createInsets(), 0, 0 );
    private GridBagConstraints clockPanelConstraints = new GridBagConstraints( 0, 2, 1, 1, 1, 0,
                                                                               GridBagConstraints.SOUTH,
                                                                               GridBagConstraints.HORIZONTAL,
                                                                               createInsets(), 0, 0 );
    private GridBagConstraints monitorPanelGbc = new GridBagConstraints( 0, 1, 1, 1, 0, 0,
                                                                         GridBagConstraints.PAGE_END,
                                                                         GridBagConstraints.NONE,
                                                                         createInsets(), 0, 0 );

    /**
     * Constructs a new ModulePanel with null contents.
     */
    public ModulePanel() {
        this( null, null, null, null );
    }

    /**
     * Constructs a new ModulePanel with the specified contents (which may be null).
     *
     * @param simulationPanel
     * @param controlPanel
     * @param monitorPanel
     * @param clockControlPanel
     */
    public ModulePanel( JComponent simulationPanel, ControlPanel controlPanel,
                        JComponent monitorPanel, JComponent clockControlPanel ) {
        setLayout( new GridBagLayout() );

        // Use this code to put the apparatus panel and control panel in split panes
//        appCtrlPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, apparatusPanelContainer, controlPanel );
//        add( appCtrlPane, appCtrlGbc );
//        appCtrlPane.setResizeWeight( 1 );

        setSimulationPanel( simulationPanel );
        setMonitorPanel( monitorPanel );
        setClockControlPanel( clockControlPanel );
        setControlPanel( controlPanel );
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayoutAll();
            }

            public void componentShown( ComponentEvent e ) {
                relayoutAll();
            }
        } );
        addContainerListener( new ContainerListener() {
            public void componentAdded( ContainerEvent e ) {
                relayoutAll();
            }

            public void componentRemoved( ContainerEvent e ) {
                relayoutAll();
            }
        } );
        relayoutAll();
    }

    /**
     * Relayout, revalidate and repaint this ModulePanel.
     */
    protected void relayoutAll() {
        invalidate();
        validateTree();
        doLayout();
        repaint();
    }

    /**
     * Set the ControlPanel of this ModulePanel.
     *
     * @param panel
     */
    public void setControlPanel( ControlPanel panel ) {
        if( controlPanel != null ) {
            remove( controlPanel );
        }
        controlPanel = panel;
        setPanel( panel, controlPanelConstraints );
    }

    /**
     * Set the monitor panel of this ModulePanel.
     *
     * @param panel
     */
    public void setMonitorPanel( JComponent panel ) {
        if( monitorPanel != null ) {
            remove( monitorPanel );
        }
        monitorPanel = panel;
        setPanel( panel, monitorPanelGbc );
    }

    /**
     * Set the simulation panel of this ModulePanel.
     *
     * @param panel
     */
    public void setSimulationPanel( JComponent panel ) {
        if( simulationPanel != null ) {
            remove( simulationPanel );
        }
        simulationPanel = panel;
        setPanel( panel, apparatusPanelConstraints );
    }

    /**
     * Sets the clock control panel for the Module.
     *
     * @param clockControlPanel
     */
    public void setClockControlPanel( JComponent clockControlPanel ) {
        if( this.clockControlPanel != null ) {
            remove( this.clockControlPanel );
        }
        this.clockControlPanel = clockControlPanel;
        setPanel( clockControlPanel, clockPanelConstraints );
    }

    private void setPanel( JComponent component, GridBagConstraints gridBagConstraints ) {
        if( component != null ) {
            add( component, gridBagConstraints );
        }
        revalidate();
        repaint();
    }

    /**
     * Hide everything but the SimulationPanel.
     *
     * @param fullScreen
     */
    public void setFullScreen( boolean fullScreen ) {
        if( fullScreen && !isFullScreen() ) {
            activateFullScreen();
        }
        else if( !fullScreen && isFullScreen() ) {
            deactivateFullScreen();
        }
    }

    private void deactivateFullScreen() {
        setVisible( controlPanel, true );
        setVisible( monitorPanel, true );
        setVisible( clockControlPanel, true );
        this.fullScreen = false;
    }

    private void activateFullScreen() {
        setVisible( controlPanel, false );
        setVisible( monitorPanel, false );
        setVisible( clockControlPanel, false );

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

    private void setVisible( JComponent component, boolean visible ) {
        if( component != null ) {
            component.setVisible( visible );
        }
    }

    private boolean isFullScreen() {
        return fullScreen;
    }

    /**
     * Gets the monitor panel.
     *
     * @return the monitor panel.
     */
    public JComponent getMonitorPanel() {
        return monitorPanel;
    }

    /**
     * Gets the clock control panel.
     *
     * @return the clock control panel.
     */
    public JComponent getClockControlPanel() {
        return clockControlPanel;
    }

    /**
     * Gets the simulation panel.
     *
     * @return the simulation panel.
     */
    public JComponent getSimulationPanel() {
        return simulationPanel;
    }

    /**
     * Gets the ControlPanel.
     *
     * @return the ControlPanel.
     */
    public ControlPanel getControlPanel() {
        return controlPanel;
    }

}