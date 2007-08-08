/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;

/**
 * AbstractChargeNode is the base class for nodes that display charge on the bead.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractChargeNode extends PhetPNode implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Bead _bead;
    private Laser _laser;
    private ModelViewTransform _modelViewTransform;
    private Point2D _pModel; // reusable point
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractChargeNode( Bead bead, Laser laser, ModelViewTransform modelViewTransform ) {
        super();
        
        _bead = bead;
        _bead.addObserver( this );
        
        _laser = laser;
        _laser.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        _pModel = new Point2D.Double();
        
        initialize();
        updatePosition();
    }

    public void cleanup() {
        _bead.deleteObserver( this );
        _laser.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Abstract methods
    //----------------------------------------------------------------------------
    
    protected abstract void initialize();
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    protected Bead getBead() {
        return _bead;
    }
    
    protected Laser getLaser() {
        return _laser;
    }
    
    protected ModelViewTransform getModelViewTransform() {
        return _modelViewTransform;
    }
    
    //----------------------------------------------------------------------------
    // Superclass overrides
    //----------------------------------------------------------------------------
    
    public void setVisible( boolean visible ) {
        if ( visible != isVisible() ) {
            if ( visible ) {
                updatePosition();
            }
            super.setVisible( visible );
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _bead ) {
            if ( arg == Bead.PROPERTY_POSITION ) {
                updatePosition();
            }
        }
        else if ( o == _laser ) {
            
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updatePosition() {
        _modelViewTransform.modelToView( _bead.getPositionReference(), _pModel );
        setOffset( _pModel );
    }
}
