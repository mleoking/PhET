/**
 * Class: Module
 * Package: edu.colorado.phet.common.application
 * Author: Another Guy
 * Date: Jun 9, 2003
 */
package edu.colorado.phet.common.application;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.components.media.Resettable;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.movingman.application.MovingManModule;

import javax.swing.*;

/**
 * This class encapsulates the parts of an application that make up
 * a complete "experiment." This includes, but is not limited to, the
 * on-screen controls and viewX elements that go along with the
 * experiment.
 */
public abstract class Module {

    BaseModel model;
    ApparatusPanel apparatusPanel;
    JPanel controlPanel;
    JPanel monitorPanel;
    String name;
    private Resettable resettable;

    protected Module( String name ) {
        this.name = name;
    }

    public MovingManModule.RepaintDebugPanel getApparatusPanel() {
        return (MovingManModule.RepaintDebugPanel)apparatusPanel;
    }

    protected void setApparatusPanel( ApparatusPanel apparatusPanel ) {
        this.apparatusPanel = apparatusPanel;
    }

    public JPanel getControlPanel() {
        return controlPanel;
    }

    protected void setControlPanel( JPanel controlPanel ) {
        this.controlPanel = controlPanel;
    }

    public JPanel getMonitorPanel() {
        return monitorPanel;
    }

    protected void setMonitorPanel( JPanel monitorPanel ) {
        this.monitorPanel = monitorPanel;
    }

    public Resettable getResettable() {
        return this.resettable;
    }

    protected void setResetter( Resettable resettable ) {
        this.resettable = resettable;
    }

    protected void setModel( BaseModel model ) {
        this.model = model;
    }

    public BaseModel getModel() {
        return model;
    }

    public void activateInternal( PhetApplication app ) {
        getModel().addObserver( getApparatusPanel() );
        this.activate( app );
    }

    public void deactivateInternal( PhetApplication app ) {
        getModel().deleteObserver( getApparatusPanel() );
        this.deactivate( app );
    }

    public String getName() {
        return name;
    }

    protected void remove( ModelElement modelElement, Graphic graphic ) {
        getModel().removeModelElement( modelElement );
        getApparatusPanel().removeGraphic( graphic );
    }

    //
    public abstract void activate( PhetApplication app );

    public abstract void deactivate( PhetApplication app );


}
