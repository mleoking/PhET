/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.application;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.ModulePanel;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Dec 12, 2005
 * Time: 2:16:29 PM
 * Copyright (c) Dec 12, 2005 by Sam Reid
 */

public abstract class Module implements ClockListener {
    private String name;

    private BaseModel model;
    private ModulePanel modulePanel;
    private IClock clock;
    private boolean helpEnabled = false;
    private boolean active = false;
    private boolean clockRunningWhenActive = true;

    protected Module() {
    }

    public Module( String name, IClock clock ) {
        this.name = name;
        this.clock = clock;
        SimStrings.setStrings( "localization/CommonStrings" );

        // Handle redrawing while the clock is paused.
        clock.addClockListener( new ClockPausedHandler( this ) );
        this.modulePanel = new ModulePanel( null, null, null, null );
    }

    public IClock getClock() {
        return clock;
    }

    public void setModel( BaseModel model ) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    protected void addModelElement( ModelElement modelElement ) {
        getModel().addModelElement( modelElement );
    }

    /**
     * Activates this Module.
     */
    public void activate() {
        if( !moduleIsWellFormed() ) {
            throw new RuntimeException( "Module missing important data, module=" + this );
        }
        if( clockRunningWhenActive ) {
            clock.start();
        }
        active = true;
    }

    /**
     * Deactivates this Module, empty method here.  This method is provided so that subclasses
     * can override.
     */
    public void deactivate() {
        this.clockRunningWhenActive = getClock().isRunning();
        clock.pause();
        active = false;
    }

    /**
     * Is this module active?
     *
     * @return true or false
     */
    public boolean isActive() {
        return active;
    }

    public boolean moduleIsWellFormed() {
        boolean result = true;
        result &= this.getModel() != null;
        result &= this.getSimulationPanel() != null;
        return result;
    }

    public String toString() {
        return "name=" + name + ", model=" + model + ", simulationPanel=" + getSimulationPanel();
    }

    /**
     * Tells whether this module has on-screen help
     *
     * @return whether this module has on-screen help
     */
    public boolean hasHelp() {
        return false;
    }

    /**
     * Switches the display of onscreen help off and on
     *
     * @param h
     */
    public void setHelpEnabled( boolean h ) {
        helpEnabled = h;
        // If our control panel is a Phet control panel, then change the
        // state of its Help button.
        getControlPanel().setHelpEnabled( h );
    }

    public ControlPanel getControlPanel() {
        return modulePanel.getControlPanel();
    }

    public boolean isHelpEnabled() {
        return helpEnabled;
    }

    /**
     * This must be overrideen by subclasses that have megahelp
     */
    public void showMegaHelp() {
    }

    /**
     * This must be overriden by subclasses that have megahelp to return true
     *
     * @return whether there is megahelp
     */
    public boolean hasMegaHelp() {
        return false;
    }

    /**
     * Any module that wants to do some graphics updating that isn't handled through
     * model element/observer mechanisms can overide this method
     *
     * @param event
     */
    public void updateGraphics( ClockEvent event ) {
    }

    /**
     * Refreshes the Module.
     * This is typically called by something else (eg, ClockPausedHandler)
     * while the clock is paused.
     */
    public void refresh() {
    }

    public void clockTicked( ClockEvent event ) {
        handleUserInput();
        model.clockTicked( event );
        updateGraphics( event );
    }

    protected void handleUserInput() {
    }

    public BaseModel getModel() {
        return model;
    }

    /**
     * Returns a ModuleStateDescriptor for this Module.
     * <p/>
     * This method should be extended by subclasses that have state attributes.
     *
     * @return a ModuleStateDescriptor for this Module.
     */
    public ModuleStateDescriptor getState() {
        ModuleStateDescriptor sd = new ModuleStateDescriptor( this );
        return sd;
    }

    public abstract JComponent getSimulationPanel();

    public Class[] getTransientPropertySources() {
        return new Class[0];
    }

    public void setReferenceSize() {
    }

    public void clockStarted( ClockEvent clockEvent ) {
    }

    public void clockPaused( ClockEvent clockEvent ) {
    }

    public void simulationTimeChanged( ClockEvent clockEvent ) {
    }

    public void clockReset( ClockEvent clockEvent ) {
    }

    public void setMonitorPanel( JPanel monitorPanel ) {
        modulePanel.setMonitorPanel( monitorPanel );
    }

    protected void setSimulationPanel( JComponent simulationPanel ) {
        modulePanel.setSimulationPanel( simulationPanel );
    }

    public void setControlPanel( ControlPanel controlPanel ) {
        modulePanel.setControlPanel( controlPanel );
    }

    public ModulePanel getModulePanel() {
        return modulePanel;
    }
}
