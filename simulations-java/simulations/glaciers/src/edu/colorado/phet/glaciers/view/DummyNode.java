/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.glaciers.model.ExampleModelElement;
import edu.colorado.phet.glaciers.model.ModelViewTransform;


public class DummyNode extends PhetPNode implements Observer {

    private ExampleModelElement _modelElement;
    private ModelViewTransform _modelViewTransform;
    
    public DummyNode( ExampleModelElement modelElement, ModelViewTransform modelViewTransform ) {
        super();
        
        _modelElement = modelElement;
        _modelElement.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
    }
    
    public void cleanup() {
        _modelElement.deleteObserver( this );
    }

    public void update( Observable o, Object arg ) {
        if ( o == _modelElement ) {
            updateNode();
        }
    }
    
    private void updateNode() {
        Point2D modelPosition = _modelElement.getPositionReference();
        double orientation = _modelElement.getOrientation();
        Point2D viewPosition = _modelViewTransform.modelToView( modelPosition );
        setOffset( viewPosition );
        setRotation( orientation );
    }
}
