/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.quantum.model.Photon;
import edu.colorado.phet.mri.MriConfig;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.ArrayList;

/**
 * Detector
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Detector extends SimpleObservable implements ModelElement {
    private Rectangle2D bounds;
    private MriModel model;
    private double detectingPeriod = MriConfig.DT * 10;
    private double elapsedTime;
    private int numDetected;
    private List detectedPhotons = new ArrayList( );

    /**
     * Constructor
     * @param bounds
     * @param model
     */
    public Detector( Rectangle2D bounds, MriModel model ) {
        this.bounds = bounds;
        this.model = model;
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

    public void stepInTime( double dt ) {
        List photons = model.getPhotons();
        for( int i = 0; i < photons.size(); i++ ) {
            Photon photon = (Photon)photons.get( i );
            if( !detectedPhotons.contains( photon) && bounds.contains( photon.getPosition() )) {
                numDetected++;
                detectedPhotons.add( photon );
            }
        }
        elapsedTime += dt;
        if( elapsedTime >= detectingPeriod ) {
            elapsedTime = 0;
            detectedPhotons.clear();
            numDetected = 0;
        }
        notifyObservers();        
    }

    public int getNumDetected() {
        return numDetected;
    }
}
