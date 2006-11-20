/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.hydrogenatom.enums.DeBroglieView;

/**
 * DeBroglieModel is the deBroglie model of a hydrogen atom.
 * It is identical to the Bohr model, but has a different visual representation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeBroglieModel extends BohrModel {

    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_VIEW = "view";
    
    public static DeBroglieView DEFAULT_VIEW = DeBroglieView.BRIGHTNESS_MAGNITUDE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DeBroglieView _view;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeBroglieModel( Point2D position ) {
        super( position );
        _view = DEFAULT_VIEW;
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the type of view.
     * This really shouldn't be in the model, but in our current architecture,
     * the only way to pass information to view components is via the model.
     * 
     * @param view
     */
    public void setView( DeBroglieView view ) {
        if ( view != _view ) {
            _view = view;
            notifyObservers( PROPERTY_VIEW );
        }
    }
    
    /**
     * Gets the type of view.
     * @return DeBroglieView
     */
    public DeBroglieView getView() {
        return _view;
    }
    
    /**
     * DeBroglie is represented as a standing wave on a ring.
     * This method determines the wave's amplitude at some angle.
     * 
     * @param angle
     * @return -1 <= amplitude <= 1
     */
    public double getAmplitude( double angle ) {
        int state = getElectronState();
        double electronAngle = getElectronAngle();
        double amplitude = Math.sin( state * angle ) * Math.sin( electronAngle );
        assert( amplitude >= -1 && amplitude <= 1 );
        return amplitude;
    }
}
