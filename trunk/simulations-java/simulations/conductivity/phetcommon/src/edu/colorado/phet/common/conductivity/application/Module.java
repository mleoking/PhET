/**
 * Class: Module
 * Package: edu.colorado.phet.common.application
 * Author: Another Guy
 * Date: Jun 9, 2003
 */
package edu.colorado.phet.common.conductivity.application;

import edu.colorado.phet.common.conductivity.model.BaseModel;
import edu.colorado.phet.conductivity.oldphetgraphics.ApparatusPanel;

import javax.swing.*;

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

    protected Module( String name ) {
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

    protected void setModel( BaseModel model ) {
        this.model = model;
    }

    protected void setControlPanel( JPanel controlPanel ) {
        this.controlPanel = controlPanel;
    }


    public BaseModel getModel() {
        return model;
    }

    /**
     * Activates this Module, empty method here.  This method is provided so that subclasses
     * can override.
     *
     * @param app
     */
    public void activate( PhetApplication app ) {
    }

    /**
     * Deactivates this Module, empty method here.  This method is provided so that subclasses
     * can override.
     *
     * @param app
     */
    public void deactivate( PhetApplication app ) {
    }


}
