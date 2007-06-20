/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;


/**
 * LaserElectricFieldNode is the visual representation of the laser's electric field.
 * A collection of vectors are shown distibuted across the laser beam.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LaserElectricFieldNode extends PhetPNode implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Laser _laser;
    private ModelViewTransform _modelViewTransform;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public LaserElectricFieldNode( Laser laser, ModelViewTransform modelViewTransform ) {
        super();
        
        _laser = laser;
        _laser.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
    }

    public void cleanup() {
        _laser.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        //XXX
    }
}
