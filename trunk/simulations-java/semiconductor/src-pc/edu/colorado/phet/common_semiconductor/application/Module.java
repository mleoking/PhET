/**
 * Class: Module
 * Package: edu.colorado.phet.common.application
 * Author: Another Guy
 * Date: Jun 9, 2003
 */
package edu.colorado.phet.common_semiconductor.application;

import edu.colorado.phet.common_semiconductor.model.BaseModel;
import edu.colorado.phet.common_semiconductor.model.ModelElement;
import edu.colorado.phet.common_semiconductor.view.ApparatusPanel;
import edu.colorado.phet.common_semiconductor.view.graphics.Graphic;

import javax.swing.*;

/**
 * This class encapsulates the parts of an application that make up
 * a complete virtual experiment. This includes, but is not limited to, the
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

    protected void addModelElement( ModelElement modelElement ) {
        getModel().addModelElement( modelElement );
    }

    protected void addGraphic( Graphic graphic, double layer ) {
        getApparatusPanel().addGraphic( graphic, layer );
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
