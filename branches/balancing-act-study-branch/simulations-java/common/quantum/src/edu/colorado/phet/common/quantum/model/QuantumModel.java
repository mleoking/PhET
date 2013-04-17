// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.quantum.model;

import edu.colorado.phet.common.phetcommon.model.BaseModel;

/**
 * QuatumModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class QuantumModel extends BaseModel implements Photon.LeftSystemEventListener {
    private ElementProperties currentElementProperties;
    private double photonSpeedScale;

    public QuantumModel( double photonSpeedScale ) {
        this.photonSpeedScale = photonSpeedScale;
    }

    public double getPhotonSpeedScale() {
        return photonSpeedScale;
    }

    public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
        removeModelElement( event.getPhoton() );
    }

    public AtomicState getGroundState() {
        return currentElementProperties.getGroundState();
    }

    protected ElementProperties getCurrentElementProperties() {
//    protected LaserElementProperties getCurrentElementProperties() {
        return currentElementProperties;
    }

    public void setCurrentElementProperties( ElementProperties currentElementProperties ) {
        this.currentElementProperties = currentElementProperties;
    }
}
