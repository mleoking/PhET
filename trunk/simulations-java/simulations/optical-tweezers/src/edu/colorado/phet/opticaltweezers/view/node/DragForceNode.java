/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view.node;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Fluid;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.util.Vector2D;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * DragForceNode displays the fluid drag force acting on a bead.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DragForceNode extends AbstractForceNode implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String UNITS = OTResources.getString( "units.dragForce" );
    private static final Color COLOR = Color.BLUE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Fluid _fluid;
    private Bead _bead;
    private ModelViewTransform _modelViewTransform;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DragForceNode( Fluid fluid, Bead bead, ModelViewTransform modelViewTransform, double modelReferenceMagnitude, double viewReferenceLength ) {
        super( modelReferenceMagnitude, viewReferenceLength, UNITS, COLOR );
        
        _fluid = fluid;
        _fluid.addObserver( this );
        
        _bead = bead;
        _bead.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        updatePosition();
        updateVectors();
    }
    
    public void cleanup() {
        _fluid.deleteObserver( this );
        _bead.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _fluid ) {
            updateVectors();
        }
        else if ( o == _bead ) {
            updatePosition();
            updateVectors();
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updatePosition() {
        Point2D position = _modelViewTransform.modelToView( _bead.getPositionRef() );
        setOffset( position.getX(), position.getY() );
    }
    
    private void updateVectors() {
        // calcuate the drag force at the bead's position
        Vector2D dragForce = _bead.getDragForce();
        // update the vectors
        setForce( dragForce );
    }
}
