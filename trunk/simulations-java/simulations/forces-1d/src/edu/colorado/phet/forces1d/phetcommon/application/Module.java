/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.forces1d.phetcommon.application;

import javax.swing.*;

import edu.colorado.phet.forces1d.phetcommon.model.BaseModel;
import edu.colorado.phet.forces1d.phetcommon.model.ModelElement;
import edu.colorado.phet.forces1d.phetcommon.model.clock.AbstractClock;
import edu.colorado.phet.forces1d.phetcommon.model.clock.ClockTickEvent;
import edu.colorado.phet.forces1d.phetcommon.model.clock.ClockTickListener;
import edu.colorado.phet.forces1d.phetcommon.view.ApparatusPanel;
import edu.colorado.phet.forces1d.phetcommon.view.ControlPanel;
import edu.colorado.phet.forces1d.phetcommon.view.help.HelpManager;
import edu.colorado.phet.forces1d.phetcommon.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.forces1d.Force1DResources;

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

    /**
     * @param name
     * @param clock
     */
    protected Module( String name, AbstractClock clock ) {
        this.name = name;
        this.clock = clock;
        Force1DResources.setStrings( "localization/CommonStrings" );
        helpManager = new HelpManager();
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
        if ( helpManager != null ) {
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
        if ( !moduleIsWellFormed() ) {
            throw new RuntimeException( "Module missing important data, module=" + this );
        }
        app.getPhetFrame().getBasicPhetPanel().setControlPanel( this.getControlPanel() );
        app.getPhetFrame().getBasicPhetPanel().setMonitorPanel( this.getMonitorPanel() );
        app.addClockTickListener( this );
    }

    /**
     * Deactivates this Module, empty method here.  This method is provided so that subclasses
     * can override.
     *
     * @param app
     */
    public void deactivate( PhetApplication app ) {
        app.removeClockTickListener( this );
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
        helpManager.setHelpEnabled( apparatusPanel, h );
        if ( controlPanel instanceof ControlPanel ) {
            // If our control panel is a Phet control panel, then change the 
            // state of its Help button.
            ( (ControlPanel) controlPanel ).setHelpEnabled( h );
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
        PhetJComponent.getRepaintManager().updateGraphics();
    }

    //----------------------------------------------------------------
    // Main loop
    //----------------------------------------------------------------

    public void clockTicked( ClockTickEvent event ) {
        getApparatusPanel().handleUserInput();
        model.clockTicked( event );
        updateGraphics( event );
        getApparatusPanel().paint();
    }

    public BaseModel getModel() {
        return model;
    }

}
