/* Copyright 2003-2004, University of Colorado */

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
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.help.HelpItem;
import edu.colorado.phet.common.view.help.HelpManager;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.util.MultiMap;
import edu.colorado.phet.common.util.persistence.Persistent;
import edu.colorado.phet.common.util.persistence.StateDescriptor;

import javax.swing.*;
import java.util.Iterator;

/**
 * This class encapsulates the parts of an application that make up
 * a complete virtual experiment. This includes, but is not limited to, the
 * on-screen controls and view elements that go along with the
 * experiment. Each module has its own model.
 *
 * @author ?
 * @version $Revision$
 */
public class Module implements Persistent {

    BaseModel model;
    ApparatusPanel apparatusPanel;
    JPanel controlPanel;
    JPanel monitorPanel;
    String name;
    HelpManager helpManager;


    protected Module( String name ) {
        this.name = name;
        SimStrings.setStrings( "localization/CommonStrings" );
        helpManager = new HelpManager();
    }

    protected void init( ApparatusPanel apparatusPanel, JPanel controlPanel, JPanel monitorPanel, BaseModel baseModel ) {
        setApparatusPanel( apparatusPanel );
        setControlPanel( controlPanel );
        setMonitorPanel( monitorPanel );
        setModel( baseModel );
    }

    /////////////////////////////////////////////////////////////////
    // Setters and getters
    //
    public void setApparatusPanel( ApparatusPanel apparatusPanel ) {
        this.apparatusPanel = apparatusPanel;
        helpManager = new HelpManager( apparatusPanel );//TODO fix this.
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

    public BaseModel getModel() {
        return model;
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
        app.addClockTickListener( model );
    }

    /**
     * Deactivates this Module, empty method here.  This method is provided so that subclasses
     * can override.
     *
     * @param app
     */
    public void deactivate( PhetApplication app ) {
        app.removeClockTickListener( model );
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


    ///////////////////////////////////////////////////////////////////////////////////
    // Help related methods
    //

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
    }

    /**
     * Adds a an onscreen help item to the module
     *
     * @param helpItem
     */
    public void addHelpItem( HelpItem helpItem ) {
        helpManager.addHelpItem( helpItem );
        if( controlPanel != null && controlPanel instanceof ControlPanel ) {
            ( (ControlPanel)controlPanel ).setHelpPanelEnabled( true );
        }
    }

    /**
     * Removes an onscreen help item from the module
     *
     * @param helpItem
     */
    public void removeHelpItem( HelpItem helpItem ) {
        helpManager.removeHelpItem( helpItem );
        if( controlPanel != null && controlPanel instanceof ControlPanel && helpManager.getNumHelpItems() == 0 ) {
            ( (ControlPanel)controlPanel ).setHelpPanelEnabled( false );
        }
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

    ////////////////////////////////////////////////////////////////
    // Persistence
    //

    public void setState( StateDescriptor stateDescriptor ) {
        stateDescriptor.setState( this );
//        restoreState( (ModuleStateDescriptor)stateDescriptor );
    }

    /**
     * Returns a ModuleStateDescriptor for this Module.
     * <p/>
     * This method should be extended by subclasses that have state attributes.
     *
     * @return
     */
    public StateDescriptor getState() {
        ModuleStateDescriptor sd = new ModuleStateDescriptor( this );
        return sd;
    }

    /**
     * Restores the state of this Module to that specificied in a ModuleStateDescriptor
     *
     * @param stateDescriptor
     */
//    private void restoreState( ModuleStateDescriptor stateDescriptor ) {
//
//        // Remove and clean up the current model
//        AbstractClock clock = PhetApplication.instance().getApplicationModel().getClock();
//        BaseModel oldModel = getModel();
//        oldModel.removeAllModelElements();
//        clock.removeClockTickListener( oldModel );
//
//        // Set up the restored model
//        BaseModel newModel = sd.getModel();
//        clock.addClockTickListener( newModel );
//        setModel( newModel );
//
//        // Set up the restored graphics
//        // Hook all the graphics up to the current apparatus panel
//        MultiMap graphicsMap = sd.getGraphicMap();
//        Iterator it = graphicsMap.iterator();
//        while( it.hasNext() ) {
//            Object obj = it.next();
//            if( obj instanceof PhetGraphic ) {
//                PhetGraphic phetGraphic = (PhetGraphic)obj;
//                phetGraphic.setComponent( getApparatusPanel() );
//            }
//        }
//        getApparatusPanel().getGraphic().setGraphicMap( sd.getGraphicMap() );
//
//        // Force a repaint on the apparatus panel
//        getApparatusPanel().repaint();
//    }
}
