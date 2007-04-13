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

/**
 * ModuleStateDescriptor
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ModuleStateDescriptor {
    private BaseModel model;
    private String moduleClassName;

    protected ModuleStateDescriptor() {
    }

    public ModuleStateDescriptor( Module module ) {
        setModel( module.getModel() );
        setModuleClassName( module.getClass().getName() );
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

    public void setModuleState( Module module ) {
//        Module module = (Module)persistentObject;

        // Remove and clean up the current model
//        IClock clock = PhetApplication.instance().getApplicationModel().getClock();
        BaseModel oldModel = module.getModel();
        oldModel.removeAllModelElements();
//        clock.removeClockListener( oldModel );

        // Set up the restored model
        BaseModel newModel = this.getModel();
//        clock.addClockListener( newModel );
        module.setModel( newModel );
        module.activate();//todo test this
    }
}
