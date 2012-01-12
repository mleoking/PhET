// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.mvcexample.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.mvcexample.model.CModelElement;
import edu.colorado.phet.mvcexample.model.CModelElement.CModelElementListener;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * CNode is the visual representation of a CModelElement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CNode extends PointerNode implements CModelElementListener {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private CModelElement _modelElement;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public CNode( final CModelElement modelElement ) {
        super( modelElement.getSize(), modelElement.getColor() );
        
        _modelElement = modelElement;
        _modelElement.addListener( this );

        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( CNode.this.getParent() );
                Point2D p = _modelElement.getPosition();
                Point2D pNew = new Point2D.Double( p.getX() + delta.getWidth(), p.getY() + delta.getHeight() );
                modelElement.setPosition( pNew );
            }
        } );
        
        positionChanged();
        orientationChanged();
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        _modelElement.removeListener( this );
    }

    //----------------------------------------------------------------------------
    // CModelElementListener implementation
    //----------------------------------------------------------------------------
    
    public void orientationChanged() {
        setRotation( _modelElement.getOrientation() );
    }

    public void positionChanged() {
        setOffset( _modelElement.getPosition() );
    }
}
