/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.quantum.model;

import edu.colorado.phet.common.model.BaseModel;

/**
 * QuatumModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class QuantumModel extends BaseModel implements Photon.LeftSystemEventListener {
    private ElementProperties currentElementProperties;
//    private LaserElementProperties currentElementProperties;


    public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
        removeModelElement( event.getPhoton() );
    }

//    public AtomicState getGroundState() {
//        return currentElementProperties.getGroundState();
//    }

    protected ElementProperties getCurrentElementProperties() {
//    protected LaserElementProperties getCurrentElementProperties() {
        return currentElementProperties;
    }

    protected void setCurrentElementProperties( ElementProperties currentElementProperties ) {
        this.currentElementProperties = currentElementProperties;
    }
}
