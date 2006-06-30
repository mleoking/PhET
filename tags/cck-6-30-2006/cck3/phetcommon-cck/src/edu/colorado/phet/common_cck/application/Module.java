/* Copyright University of Colorado, 2003 */
package edu.colorado.phet.common_cck.application;

import edu.colorado.phet.common_cck.model.BaseModel;
import edu.colorado.phet.common_cck.model.ModelElement;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.graphics.Graphic;
import edu.colorado.phet.common_cck.view.help.HelpItem;
import edu.colorado.phet.common_cck.view.help.HelpManager;
import edu.colorado.phet.common_cck.view.util.SimStrings;

import javax.swing.*;

/**
 * This class encapsulates the parts of an application that make up
 * a complete virtual experiment. This includes, but is not limited to, the
 * on-screen controls and view elements that go along with the
 * experiment. Each module has its own model.
 */
public class Module {

    BaseModel model;
    ApparatusPanel apparatusPanel;
    JComponent controlPanel;
    JPanel monitorPanel;
    String name;
    HelpManager helpManager = new HelpManager();

    protected Module( String name ) {
        this.name = name;
        SimStrings.setStrings( "localization/CommonStrings" );
    }

    public ApparatusPanel getApparatusPanel() {
        return apparatusPanel;
    }

    public void setHelpEnabled( boolean h ) {
        helpManager.setHelpEnabled( apparatusPanel, h );
    }

    public void addHelpItem( HelpItem helpItem ) {
        helpManager.addHelpItem( helpItem );
    }

    public void removeHelpItem( HelpItem helpItem ) {
        helpManager.removeHelpItem( helpItem );
    }

    public JComponent getControlPanel() {
        return controlPanel;
    }

    protected void setApparatusPanel( ApparatusPanel apparatusPanel ) {
        this.apparatusPanel = apparatusPanel;
    }

    protected void setMonitorPanel( JPanel monitorPanel ) {
        this.monitorPanel = monitorPanel;
    }

    protected void setModel( BaseModel model ) {
        this.model = model;
    }

    protected void setControlPanel( JComponent controlPanel ) {
        this.controlPanel = controlPanel;
    }

    protected void init( ApparatusPanel panel, JPanel controlPanel, JPanel monitorPanel, BaseModel baseModel ) {
        setApparatusPanel( apparatusPanel );
        setControlPanel( controlPanel );
        setMonitorPanel( monitorPanel );
        setModel( model );
    }

    public JPanel getMonitorPanel() {
        return monitorPanel;
    }


    public BaseModel getModel() {
        return model;
    }

    public String getName() {
        return name;
    }

    protected void addModelElement( ModelElement modelElement ) {
        getModel().addModelElement( modelElement );
    }

    public void addGraphic( Graphic graphic, double layer ) {
        getApparatusPanel().addGraphic( graphic, layer );
    }

    protected void add( ModelElement modelElement, Graphic graphic, double layer ) {
        this.addModelElement( modelElement );
        this.addGraphic( graphic, layer );
    }

    protected void remove( ModelElement modelElement, Graphic graphic ) {
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
        app.getApplicationView().getBasicPhetPanel().setControlPanel( this.getControlPanel() );
        app.getApplicationView().getBasicPhetPanel().setMonitorPanel( this.getMonitorPanel() );
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

    public void showMegaHelp() {
    }
}
