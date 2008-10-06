/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common_sound.application;

import javax.swing.JComponent;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common_sound.model.BaseModel;
import edu.colorado.phet.common_sound.model.ModelElement;
import edu.colorado.phet.common_sound.model.clock.AbstractClock;
import edu.colorado.phet.common_sound.model.clock.ClockTickEvent;
import edu.colorado.phet.common_sound.model.clock.ClockTickListener;
import edu.colorado.phet.common_sound.view.ApparatusPanel;
import edu.colorado.phet.common_sound.view.ControlPanel;
import edu.colorado.phet.common_sound.view.help.HelpManager;

/**
 * This class encapsulates the parts of an application that make up
 * a complete virtual experiment. This includes, but is not limited to, the
 * on-screen controls and view elements that go along with the
 * experiment. Each module has its own model.
 *
 * @author ?
 * @version $Revision$
 */
public class Module implements ClockTickListener {

    BaseModel model;
    ApparatusPanel apparatusPanel;
    JPanel controlPanel;
    JPanel monitorPanel;
    String name;
    private AbstractClock clock;
    HelpManager helpManager;
    private boolean helpEnabled;
    private boolean isActive;

    /**
     * @param name
     * @param clock
     */
    protected Module( String name, AbstractClock clock ) {
        this.name = name;
        this.clock = clock;
        SimStrings.getInstance().addStrings( "sound/localization/phetcommon-strings" );
        helpManager = new HelpManager();
        helpEnabled = false;

        // Handle redrawing while the clock is paused.
//        clock.addClockStateListener( new ClockPausedHandler( this ) );
    }

    /**
     * @param name
     * @deprecated
     */
    protected Module( String name ) {
        this( name, null );
    }

    protected void init( ApparatusPanel apparatusPanel, JPanel controlPanel, JPanel monitorPanel, BaseModel baseModel ) {
        setApparatusPanel( apparatusPanel );
        setControlPanel( controlPanel );
        setMonitorPanel( monitorPanel );
        setModel( baseModel );
    }

    //-----------------------------------------------------------------
    // Setters and getters
    //-----------------------------------------------------------------

    public AbstractClock getClock() {
        return clock;
    }

    public void setApparatusPanel( ApparatusPanel apparatusPanel ) {
        this.apparatusPanel = apparatusPanel;
        if( helpManager != null ) {
            helpManager.setComponent( apparatusPanel );
        }
        else {
            helpManager = new HelpManager( apparatusPanel );//TODO fix this.
        }
    }

    public ApparatusPanel getApparatusPanel() {
        return apparatusPanel;
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

    public void addGraphic( PhetGraphic graphic, double layer ) {
        getApparatusPanel().addGraphic( graphic, layer );
    }

    protected void add( ModelElement modelElement, PhetGraphic graphic, double layer ) {
        this.addModelElement( modelElement );
        this.addGraphic( graphic, layer );
    }

    protected void remove( ModelElement modelElement, PhetGraphic graphic ) {
        getModel().removeModelElement( modelElement );
        getApparatusPanel().removeGraphic( graphic );
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
        result &= this.getApparatusPanel() != null;
        return result;
    }

    public String toString() {
        return "name=" + name + ", model=" + model + ", apparatusPanel=" + apparatusPanel + ", controlPanel=" + controlPanel + ", monitorPanel=" + monitorPanel;
    }

    //-----------------------------------------------------------------
    // Help-related methods
    //-----------------------------------------------------------------

    /**
     * Tells whether this module has on-screen help
     *
     * @return
     */
    public boolean hasHelp() {
        return helpManager.getNumGraphics() > 0;
    }

    /**
     * Switches the display of onscreen help off and on
     *
     * @param h
     */
    public void setHelpEnabled( boolean h ) {
        helpEnabled = h;
        helpManager.setHelpEnabled( apparatusPanel, h );
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
     * Adds an onscreen help item to the module
     *
     * @param helpItem
     */
    public void addHelpItem( PhetGraphic helpItem ) {
        helpManager.addGraphic( helpItem );
        if( controlPanel != null && controlPanel instanceof ControlPanel ) {
            ( (ControlPanel)controlPanel ).setHelpPanelEnabled( true );
        }
    }

    /**
     * Removes an onscreen help item from the module
     *
     * @param helpItem
     */
    public void removeHelpItem( PhetGraphic helpItem ) {
        helpManager.removeGraphic( helpItem );
        if( controlPanel != null && controlPanel instanceof ControlPanel && helpManager.getNumHelpItems() == 0 ) {
            ( (ControlPanel)controlPanel ).setHelpPanelEnabled( false );
        }
    }

    public HelpManager getHelpManager() {
        return helpManager;
    }

    /**
     * This must be overrideen by subclasses that have megahelp
     */
    public void showMegaHelp() {
    }

    /**
     * This must be overriden by subclasses that have megahelp to return true
     *
     * @return
     */
    public boolean hasMegaHelp() {
        return false;
    }

    //----------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------

    /**
     * Any module that wants to do some graphics updating that isn't handled through
     * model element/observer mechanisms can overide this method
     *
     * @param event
     */
    public void updateGraphics( ClockTickEvent event ) {
        // noop
//        PhetJComponent.getRepaintManager().updateGraphics();
    }

    /**
     * Refreshes the Module.
     * This is typically called by something else (eg, ClockPausedHandler)
     * while the clock is paused.
     */
    public void refresh() {
        // Repaint all dirty PhetJComponents
//        PhetJComponent.getRepaintManager().updateGraphics();
        // Paint the apparatus panel
        apparatusPanel.paint();
    }

    //----------------------------------------------------------------
    // Main loop
    //----------------------------------------------------------------

    public void clockTicked( ClockTickEvent event ) {
        handleUserInput();
        model.clockTicked( event );
        updateGraphics( event );
//        getApparatusPanel().paint();
    }

    protected void handleUserInput() {
        getApparatusPanel().handleUserInput();
    }

    ////////////////////////////////////////////////////////////////
    // Persistence
    //

    public BaseModel getModel() {
        return model;
    }

    public JComponent getSimulationPanel() {
        return getApparatusPanel();
    }
}
