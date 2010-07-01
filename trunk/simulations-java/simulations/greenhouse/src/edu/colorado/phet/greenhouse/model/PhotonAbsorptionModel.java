/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

/**
 * Primary model for the Photon Absorption tab.  This models photons being
 * absorbed (or often NOT absorbed) by various molecules.  The scale for this
 * model is picometers (10E-12 meters).
 * 
 * @author John Blanco
 */
public class PhotonAbsorptionModel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    
    // Constant that controls the point in model space where photons are
    // emitted.
    private static final Point2D PHOTON_EMISSION_LOCATION = new Point2D.Double(-50, 20);

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
    
    public Point2D getPhotonEmissionLocation(){
        return PHOTON_EMISSION_LOCATION;
    }

    //----------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //----------------------------------------------------------------------------
}
