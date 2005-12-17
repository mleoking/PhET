/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.application;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.ClockControlPanel;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.ModulePanel;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;

/**
 * The Module is the fundamental unit of a phet simulation.
 * It entails graphics, controls and a model.
 */

public abstract class Module {
    private String name;

    private BaseModel model;
    private ModulePanel modulePanel;
    private IClock clock;

    private boolean active = false;
    private boolean clockRunningWhenActive = true;

    private boolean helpEnabled = false;
    private ClockAdapter moduleRunner;

    /**
     * Initialize an emtpy module.  This is for subclasses who intend something rather different than the norm.
     */
    protected Module() {
    }

    /**
     * Initialize a Module.
     *
     * @param name  the name for this Module
     * @param clock a clock to model passage of time for this Module.  Should be unique to this module (non-shared).
     */
    public Module( String name, IClock clock ) {
        this.name = name;
        this.clock = clock;
        SimStrings.setStrings( "localization/CommonStrings" );

        // Handle redrawing while the clock is paused.
        clock.addClockListener( new ClockPausedHandler( this ) );

        this.modulePanel = new ModulePanel();
        modulePanel.setClockControlPanel( new ClockControlPanel( clock ) );
        moduleRunner = new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                handleClockTick( clockEvent );
            }
        };
        clock.addClockListener( moduleRunner );
    }

    /**
     * Get the clock associated with this Module.
     *
     * @return the clock
     */
    public IClock getClock() {
        return clock;
    }

    /**
     * Set the BaseModel for this Module.
     *
     * @param model
     */
    public void setModel( BaseModel model ) {
        this.model = model;
    }

    /**
     * Get the name of the Module.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the base model.
     *
     * @return the model
     */
    public BaseModel getModel() {
        return model;
    }

    /**
     * Set the monitor panel, can be null.
     *
     * @param monitorPanel
     */
    public void setMonitorPanel( JPanel monitorPanel ) {
        modulePanel.setMonitorPanel( monitorPanel );
    }

    /**
     * Set the SimulationPanel.
     *
     * @param simulationPanel
     */
    protected void setSimulationPanel( JComponent simulationPanel ) {
        modulePanel.setSimulationPanel( simulationPanel );
    }

    /**
     * Set the ControlPanel for this Module.
     *
     * @param controlPanel
     */
    public void setControlPanel( ControlPanel controlPanel ) {
        modulePanel.setControlPanel( controlPanel );
    }

    /**
     * Get the ModulePanel, which contains everything required for this Module.
     *
     * @return the ModulePanel.
     */
    public ModulePanel getModulePanel() {
        return modulePanel;
    }

    /**
     * Adds a ModelElement to the BaseModel of this Module.
     *
     * @param modelElement
     */
    protected void addModelElement( ModelElement modelElement ) {
        getModel().addModelElement( modelElement );
    }

    /**
     * Activates the Module, starting it, if necessary.
     */
    public void activate() {
        if( !isWellFormed() ) {
            throw new RuntimeException( "Module missing important data, module=" + this );
        }
        if( clockRunningWhenActive ) {
            clock.start();
        }
        active = true;
    }

    /**
     * Deactivates this Module (pausing it).
     */
    public void deactivate() {
        this.clockRunningWhenActive = getClock().isRunning();
        clock.pause();
        active = false;
    }

    /**
     * Determine if the Module is currently an active module.
     *
     * @return true or false
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Determine whether the Module has all the necessary information to run.
     *
     * @return true if the Module is ready to run.
     */
    public boolean isWellFormed() {
        boolean result = true;
        result &= this.getModel() != null;
        result &= this.getSimulationPanel() != null;
        return result;
    }

    /**
     * Returns a String to represent the Module.
     *
     * @return a string
     */
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

    /**
     * Gets the ControlPanel for this Module.
     *
     * @return the ControlPanel
     */
    public ControlPanel getControlPanel() {
        return modulePanel.getControlPanel();
    }

    /**
     * Gets whether help is currently enabled (active) for this Module.
     *
     * @return help
     */
    public boolean isHelpEnabled() {
        return helpEnabled;
    }

    /**
     * During a clock tick, the Module will handle user input, step the model, and update the graphics.
     *
     * @param event
     */
    protected void handleClockTick( ClockEvent event ) {
        handleUserInput();
        model.update( event );
        updateGraphics( event );
    }

    /**
     * Returns a ModuleStateDescriptor for this Module.
     * <p/>
     * This method should be extended by subclasses that have state attributes.
     *
     * @return a ModuleStateDescriptor for this Module.
     */
    public ModuleStateDescriptor getState() {
        return new ModuleStateDescriptor( this );
    }

    /**
     * Get the SimulationPanel, the play area for this simulation.
     *
     * @return the SimulationPanel
     */
    public abstract JComponent getSimulationPanel();

    /**
     * Get any help for persistence.
     *
     * @return an array of Classes which can be used as transient property sources.
     */
    public Class[] getTransientPropertySources() {
        return new Class[0];
    }

    /**
     * Notifies the Module that this is the reference (default) size for rendering.
     */
    public void setReferenceSize() {
    }

    /**
     * Any specific user input code may go here (no-op in base class).
     */
    protected void handleUserInput() {
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
     * Refreshes the Module, redrawing it while its clock is paused.
     */
    public void refresh() {
    }
}