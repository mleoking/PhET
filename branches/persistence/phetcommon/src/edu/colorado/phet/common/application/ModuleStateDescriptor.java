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
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.util.MultiMap;
import edu.colorado.phet.common.util.persistence.StateDescriptor;
import edu.colorado.phet.common.util.persistence.Persistent;

import java.util.HashMap;
import java.util.Iterator;

/**
 * ModuleStateDescriptor
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ModuleStateDescriptor implements StateDescriptor {
    private BaseModel model;
    private String moduleClassName;
    private MultiMap graphicMap;
//    private ApparatusPanel apparatusPanel;

    public ModuleStateDescriptor() {
    }

    protected ModuleStateDescriptor( Module module ) {
        setModel( module.getModel() );
        setModuleClassName( module.getClass().getName() );
        setGraphicMap( module.getApparatusPanel().getGraphic().getGraphicMap() );
//        setApparatusPanel( module.getApparatusPanel() );
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

    public MultiMap getGraphicMap() {
        return graphicMap;
    }

    public void setGraphicMap( MultiMap graphicMap ) {
        this.graphicMap = graphicMap;
    }

//    public ApparatusPanel getApparatusPanel() {
//        return apparatusPanel;
//    }
//
//    public void setApparatusPanel( ApparatusPanel apparatusPanel ) {
//        this.apparatusPanel = apparatusPanel;
//    }


    public void setState( Persistent persistentObject ) {
        Module module = (Module)persistentObject;

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
        // Hook all the graphics up to the current apparatus panel
        MultiMap graphicsMap = this.getGraphicMap();
        Iterator it = graphicsMap.iterator();
        while( it.hasNext() ) {
            Object obj = it.next();
            if( obj instanceof PhetGraphic ) {
                PhetGraphic phetGraphic = (PhetGraphic)obj;
                phetGraphic.setComponent( module.getApparatusPanel() );
            }
        }
        module.getApparatusPanel().getGraphic().setGraphicMap( this.getGraphicMap() );

        // Force a repaint on the apparatus panel
        module.getApparatusPanel().repaint();


    }
}
