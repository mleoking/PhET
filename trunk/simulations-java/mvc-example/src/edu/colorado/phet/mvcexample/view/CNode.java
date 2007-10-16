/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample.view;

import java.awt.Color;

import edu.colorado.phet.mvcexample.model.CModelElement;
import edu.colorado.phet.mvcexample.model.CModelElement.CModelElementListener;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;

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
    
    public CNode( CModelElement modelElement ) {
        super( modelElement.getSize(), modelElement.getColor() );
        
        _modelElement = modelElement;
        _modelElement.addListener( this );
        
        addInputEventListener( new PBasicInputEventHandler() {
            //XXX this was wrong, try again
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
