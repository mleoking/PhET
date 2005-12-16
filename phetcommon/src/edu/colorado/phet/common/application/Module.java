/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.application;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Dec 12, 2005
 * Time: 2:16:29 PM
 * Copyright (c) Dec 12, 2005 by Sam Reid
 */

public abstract class Module implements ClockTickListener {
    BaseModel model;
    JPanel controlPanel;
    JPanel monitorPanel;
    String name;
    protected AbstractClock clock;
    protected boolean helpEnabled;
    private boolean isActive;

    public Module() {
        helpEnabled = false;
    }

    public Module( String name, AbstractClock clock ) {

        this.name = name;
        this.clock = clock;
        SimStrings.setStrings( "localization/CommonStrings" );

        // Handle redrawing while the clock is paused.
        clock.addClockStateListener( new ClockPausedHandler( this ) );
    }

    public AbstractClock getClock() {
        return clock;
    }

    public void setMonitorPanel( JPanel monitorPanel ) {
        this.monitorPanel = monitorPanel;
    }

    public void setModel( BaseModel model ) {
        this.model = model;
    }

    public void setControlPanel( JPanel controlPanel ) {
        this.controlPanel = controlPanel;
    }

    public JPanel getControlPanel() {
        return controlPanel;
    }

    public JPanel getMonitorPanel() {
        return monitorPanel;
    }

    public String getName() {
        return name;
    }

    protected void addModelElement( ModelElement modelElement ) {
        getModel().addModelElement( modelElement );
    }

    /**
     * Activates this Module, empty method here.  This method is provided so that subclasses
     * can override.
     *
     * @param app
     */
    public void activate( PhetApplication app ) {
        if( !moduleIsWellFormed() ) {
            throw new RuntimeException( "Module missing important data, module=" + this );
        }
        app.getPhetFrame().getBasicPhetPanel().setControlPanel( this.getControlPanel() );
        app.getPhetFrame().getBasicPhetPanel().setMonitorPanel( this.getMonitorPanel() );
        app.addClockTickListener( this );
        isActive = true;
    }

    /**
     * Deactivates this Module, empty method here.  This method is provided so that subclasses
     * can override.
     *
     * @param app
     */
    public void deactivate( PhetApplication app ) {
        app.removeClockTickListener( this );
        isActive = false;
    }

    /**
     * Is this module active?
     *
     * @return true or false
     */
    public boolean isActive() {
        return isActive;
    }

    public boolean moduleIsWellFormed() {
        boolean result = true;
        result &= this.getModel() != null;
        result &= this.getSimulationPanel() != null;
        return result;
    }

    public String toString() {
        return "name=" + name + ", model=" + model + ", simulationPanel=" + getSimulationPanel() + ", controlPanel=" + controlPanel + ", monitorPanel=" + monitorPanel;
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
        if( controlPanel instanceof ControlPanel ) {
            // If our control panel is a Phet control panel, then change the
            // state of its Help button.
            ( (ControlPanel)controlPanel ).setHelpEnabled( h );
        }
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
    public void updateGraphics( ClockTickEvent event ) {
        // noop

    }

    /**
     * Refreshes the Module.
     * This is typically called by something else (eg, ClockPausedHandler)
     * while the clock is paused.
     */
    public void refresh() {

    }

    public void clockTicked( ClockTickEvent event ) {
        handleUserInput();
        model.clockTicked( event );
        updateGraphics( event );
//        getApparatusPanel().paint();
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
}
