/*
 * Class: PhetMainPanel
 * Package: edu.colorado.phet.controller
 *
 * Created by: Ron LeMaster
 * Date: Nov 20, 2002
 */
package edu.colorado.phet.controller;

import edu.colorado.phet.graphics.*;
import edu.colorado.phet.physics.PhysicalSystem;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * This class provides the main container for a Phet application's user
 * interface. It is the only JPanel added the PhetApplication's PhetFrame other
 * than the panel for menus.
 */
public abstract class PhetMainPanel extends JPanel implements Observer {

    private PhetApplication application;
    private ApparatusPanel apparatusPanel;
    private PhetMonitorPanel monitorPanel;
    private PhetControlPanel controlPanel;
    private JPanel applicationControlPanel;

    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
    }

    /**
     *
     * @param application The PhetApplication with which this PhetMainPanel
     * is associated
     */
    protected PhetMainPanel( PhetApplication application ) {
        init( application );
    }

    /**
     * Sets up the PhetMainPanel
     * @param application
     */
    private void init( PhetApplication application ) {

        this.application = application;
        getPhysicalSystem().addObserver( this );

        this.setLayout( new BorderLayout() );

        // Add the application control panel
        setApplicationControlPanel( getApplicationControlPanel() );

        // Add an empty, narrow panel to the left just to make things look good
        JPanel westPanel = new JPanel();
        westPanel.setPreferredSize( new Dimension( 20, 200 ) );
        this.add( westPanel, BorderLayout.WEST );
        this.setPreferredSize( s_defaultSize );

    }

    /**
     * Returns the currently active apparatus panel
     * @return The currently active apparatus panel
     */
    public ApparatusPanel getApparatusPanel() {
        return apparatusPanel;
    }

    /**
     * Sets the currently active ApparatusPanel. Activate() is called on the
     * ApparatusPanel.
     * <p>
     * If another ApparatusPanel is active, deactivate() is called on it.
     * @see ApparatusPanel
     * @param apparatusPanel The apparatus panel that is to be made current
     */
    public void setApparatusPanel( ApparatusPanel apparatusPanel ) {

        // If an apparatus panel is currently active, deactivate it
        if( this.apparatusPanel != null ) {
            this.apparatusPanel.deactivate();
        }
        this.apparatusPanel = apparatusPanel;
        apparatusPanel.activate();
    }

    /**
     * Returns the MonitorPanel
     * @return The MonitorPanel
     */
    public PhetMonitorPanel getMonitorPanel() {
        return monitorPanel;
    }

    /**
     * Assigns an instance of an application-specific subclass of MonitorPanel
     * to the PhetMainPanel
     * @see MonitorPanel
     * @param monitorPanel
     */
    public void setMonitorPanel( PhetMonitorPanel monitorPanel ) {
        if( this.monitorPanel != null ) {
            this.getPhysicalSystem().deleteObserver( this.monitorPanel );
        }
        this.monitorPanel = monitorPanel;
        this.add( monitorPanel, BorderLayout.NORTH );
        this.getPhysicalSystem().addObserver( monitorPanel );
    }

    /**
     *
     * @see PhetControlPanel
     * @return The PhetControlPanel
     */
    public PhetControlPanel getControlPanel() {
        return controlPanel;
    }

    /**
     * Assigns an instance of an application-specific subclass of PhetControlPanel
     * to the PhetMainPanel
     * @see PhetControlPanel
     * @param newControlPanel
     */
    public void setControlPanel( PhetControlPanel newControlPanel ) {
        if( this.controlPanel != null ) {
            PhetApplication.instance().getPhysicalSystem().deleteObserver( this.controlPanel );
            this.remove( this.controlPanel );
        }
        this.controlPanel = newControlPanel;
        this.add( newControlPanel, BorderLayout.EAST );
        this.invalidate();
        repaint();
    }

    /**
     * Sets the control panel displayed at the bottom of the frame. This is
     * used to provided so different sets of buttons can be presented for
     * different purposes. An example is additional buttons for guieded inquiry.
     * @param applicationControlPanel
     */
    public void setApplicationControlPanel( JPanel applicationControlPanel ) {
        if( this.applicationControlPanel != null ) {
            this.remove( this.applicationControlPanel );
        }
        if( applicationControlPanel != null ) {
            this.add( applicationControlPanel, BorderLayout.SOUTH );
        }
         this.applicationControlPanel = applicationControlPanel;
        PhetApplication.instance().getPhetFrame().pack();
    }

    /**
     * This method should be overridden by subclasses that want to provide
     * a panel at the bottom of the frame for controlling the overall
     * execution of the system. Examples are panels with "Run", "Stop", and
     * "Clear" buttons.
     * @return
     */
    public JPanel getApplicationControlPanel() {
        return null;
    }

    /**
     * Returns the PhysicalSystem associated with the PhetMainPanel
     * @return The PhysicalSystem associated with the PhetMainPanel
     */
    public PhysicalSystem getPhysicalSystem() {
        return application.getPhysicalSystem();
    }

    /**
     *
     */
    public void toggleMonitorPanel() {
        monitorPanel.setVisible( !monitorPanel.isVisible() );
        this.repaint();
    }

    /**
     *
     */
    public void toggleControlPanel() {
        controlPanel.setVisible( !controlPanel.isVisible() );
        this.repaint();
    }

    /**
     * TODO: Is this needed, or is it vestigial?
     */
    protected void reset() {
        this.repaint();
    }

    /**
     *
     */
    public void clear() {
        getControlPanel().clear();
        getMonitorPanel().clear();
        PhetApplication.instance().init();
    }

    /**
     * Adds a physical body to the apparatus panel, at the default graphic level
     * @param body The body to be added
     */
    public void addBody( edu.colorado.phet.physics.body.Particle body ) {
        this.addBody( body, s_defaultGraphicLevel );
    }

    /**
     * Adds a physical body to the apparatus panel at a specified graphic
     * level
     * @param body
     * @param level
     */
    public void addBody( edu.colorado.phet.physics.body.Particle body, int level ) {
        GraphicFactory graphicFactory = application.getGraphicFactory();
        PhetGraphic graphic = graphicFactory.createGraphic( body, apparatusPanel );

        // Since some physical entities don't have graphic representations, we
        // must test for null
        if( graphic != null ) {
            apparatusPanel.addGraphic( graphic, level );
            apparatusPanel.repaint();
        }
    }

    //
    // Abstract methods
    //

    /**
     * Give the main panel a chance to do things that couldn't be done in the
     * constructor
     */
    public abstract void init();


    //
    // Static fields and methods
    //
    private static Dimension s_defaultSize = new Dimension( 1024, 768 );
    protected static int s_defaultGraphicLevel = 5;
}
