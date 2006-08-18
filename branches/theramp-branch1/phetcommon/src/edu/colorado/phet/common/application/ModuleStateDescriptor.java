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
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;

/**
 * ModuleStateDescriptor
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ModuleStateDescriptor {
    private BaseModel model;
    private String moduleClassName;
    private GraphicLayerSet graphic;

    public ModuleStateDescriptor() {
    }

    protected ModuleStateDescriptor( Module module ) {
        setModel( module.getModel() );
        setModuleClassName( module.getClass().getName() );
        setGraphic( module.getApparatusPanel().getGraphic() );
    }

    public BaseModel getModel() {
        return model;
    }

    public void setModel( BaseModel model ) {
        this.model = model;
    }

    public String getModuleClassName() {
        return moduleClassName;
    }

    public void setModuleClassName( String moduleClassName ) {
        this.moduleClassName = moduleClassName;
    }

    public GraphicLayerSet getGraphic() {
        return graphic;
    }

    public void setGraphic( GraphicLayerSet graphic ) {
        this.graphic = graphic;
    }

    public void setModuleState( Module module ) {
//        Module module = (Module)persistentObject;

        // Remove and clean up the current model
        AbstractClock clock = PhetApplication.instance().getApplicationModel().getClock();
        BaseModel oldModel = module.getModel();
        oldModel.removeAllModelElements();
        clock.removeClockTickListener( oldModel );

        // Set up the restored model
        BaseModel newModel = this.getModel();
        clock.addClockTickListener( newModel );
        module.setModel( newModel );

        // Set up the restored graphics
        module.getApparatusPanel().setGraphic( graphic );

        // Force a repaint on the apparatus panel
        module.getApparatusPanel().paintImmediately( module.getApparatusPanel().getBounds() );
    }
}
