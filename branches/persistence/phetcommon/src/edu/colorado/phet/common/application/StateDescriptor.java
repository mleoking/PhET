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
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.util.MultiMap;

import java.util.HashMap;

/**
 * StateDescriptor
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class StateDescriptor {
    private BaseModel model;
    private String moduleClassName;
    private MultiMap graphicMap;
//    private ApparatusPanel apparatusPanel;

    public StateDescriptor() {
    }

    protected StateDescriptor( Module module ) {
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
}
