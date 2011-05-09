// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.view;

import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Fluid;
import edu.colorado.phet.opticaltweezers.model.OTModelViewTransform;
import edu.colorado.phet.opticaltweezers.util.OTVector2D;

/**
 * FluidDragForceNode displays the fluid drag force acting on a bead.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FluidDragForceNode extends AbstractForceNode implements Observer {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Fluid _fluid;
    private Bead _bead;
    private OTModelViewTransform _modelViewTransform;
    private Point2D _pModel; // reusable point
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public FluidDragForceNode( Fluid fluid, Bead bead, OTModelViewTransform modelViewTransform, double modelReferenceMagnitude, double viewReferenceLength ) {
        super( modelReferenceMagnitude, viewReferenceLength, OTResources.getString( "units.force" ), OTConstants.FLUID_DRAG_FORCE_COLOR );
        
        _fluid = fluid;
        _fluid.addObserver( this );
        
        _bead = bead;
        _bead.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        _pModel = new Point2D.Double();
        
        updatePosition();
        updateVectors();
    }
    
    public void cleanup() {
        _fluid.deleteObserver( this );
        _bead.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Superclass overrides
    //----------------------------------------------------------------------------
    
    public void setVisible( boolean visible ) {
        if ( visible != isVisible() ) {
            if ( visible ) {
                updatePosition();
                updateVectors();
            }
            super.setVisible( visible );
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( isVisible() ) {
            if ( o == _fluid ) {
                updateVectors();
            }
            else if ( o == _bead ) {
                updatePosition();
                updateVectors();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updatePosition() {
        _modelViewTransform.modelToView( _bead.getPositionReference(), _pModel );
        setOffset( _pModel );
    }
    
    private void updateVectors() {
        // calcuate the drag force at the bead's position
        OTVector2D dragForce = _bead.getDragForce();
        // update the vectors
        setForce( dragForce );
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    public static Icon createIcon() {
        return AbstractForceNode.createIcon( OTConstants.FLUID_DRAG_FORCE_COLOR );
    }
}
