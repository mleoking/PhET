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

import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The Swing panel for a Module in a PhetApplication. It holds
 * the control panel, the simulation panel, the clock control panel, and a monitor panel.
 * All panels are optional (can be null).
 *
 * @author Ron & Sam
 * @version $Revision$
 */
public class ModulePanel extends JPanel {

    /* Instance Data*/
    private JComponent simulationPanel;
    private JComponent controlPanel;
    private JComponent monitorPanel;
    private JComponent clockControlPanel;

    private boolean fullScreen = false;
    private JDialog buttonDlg;

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
    public ModulePanel( JComponent simulationPanel, JComponent controlPanel,
                        JComponent monitorPanel, JComponent clockControlPanel ) {
        setLayout( new BorderLayout() );

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
     * Gets whether the ModulePanel is currently running in full screen.
     *
     * @return whether the ModulePanel is currently running in full screen.
     */
    public boolean isFullScreen() {
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
    public JComponent getControlPanel() {
        return controlPanel;
    }

    /**
     * Relayout, revalidate and repaint this ModulePanel.  This is necessary when using scrollbars in the control panel.
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
    public void setControlPanel( JComponent panel ) {
        if( controlPanel != null ) {
            remove( controlPanel );
        }
        controlPanel = panel;
        setPanel( controlPanel, BorderLayout.EAST );
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
        setPanel( monitorPanel, BorderLayout.NORTH );
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
        setPanel( simulationPanel, BorderLayout.CENTER );
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
        setPanel( clockControlPanel, BorderLayout.SOUTH );
    }

    /**
     * Adds the panel in the specified location.
     *
     * @param panel
     * @param location
     */
    private void setPanel( JComponent panel, String location ) {
        if( panel != null ) {
            add( panel, location );
        }
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
            ImageIcon logo = new ImageIcon( getClass().getClassLoader().getResource( "images/Phet-Flatirons-logo-3-small.gif" ) );
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

}