/**
 * Class: Module
 * Package: edu.colorado.phet.common.application
 * Author: Another Guy
 * Date: Jun 9, 2003
 */
package edu.colorado.phet.common.application;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;

import javax.swing.*;
import java.awt.*;

/**
 * This class encapsulates the parts of an application that make up
 * a complete "experiment." This includes, but is not limited to, the
 * on-screen controls and viewX elements that go along with the
 * experiment.
 */
public class Module {

    BaseModel model;
    ApparatusPanel apparatusPanel;
    JPanel controlPanel;
    JPanel monitorPanel;
    String name;

    protected Module( String name ) {
        this.name = name;
    }

    public ApparatusPanel getApparatusPanel() {
        return apparatusPanel;
    }

    public JPanel getControlPanel() {
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

    protected void setControlPanel( JPanel controlPanel ) {
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

    protected void addGraphic( Graphic graphic, double layer ) {
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
     * @param app
     */
    public void activate( PhetApplication app ) {
    }

    /**
     * Deactivates this Module, empty method here.  This method is provided so that subclasses
     * can override.
     * @param app
     */
    public void deactivate( PhetApplication app ) {
    }

    public void execute( Command c ) {
        model.execute( c );
    }


}
