/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module.example;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.defaults.ExampleDefaults;
import edu.colorado.phet.glaciers.model.ExampleModelElement;
import edu.colorado.phet.glaciers.module.GlaciersAbstractCanvas;
import edu.colorado.phet.glaciers.view.ExampleNode;
import edu.umd.cs.piccolo.PNode;

/**
 * ExampleCanvas is the canvas for ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleCanvas extends GlaciersAbstractCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private ExampleModel _model;
    
    // View 
    private ExampleNode _exampleNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleCanvas( ExampleModel model ) {
        super( ExampleDefaults.VIEW_SIZE );
        
        _model = model;
        
        setBackground( GlaciersConstants.CANVAS_BACKGROUND );
        
        ExampleModelElement exampleModelElement = model.getExampleModelElement();
        _exampleNode = new ExampleNode( _model.getModelViewTransform() );
        _exampleNode.addPropertyChangeListener( new ViewObserver( _exampleNode, model ) );
        exampleModelElement.addListener( _exampleNode );
        
        addNode( _exampleNode );
    }
    
    private class ViewObserver implements PropertyChangeListener {
    	
    	private PNode _node;
    	private ExampleModel _model;
    	
    	public ViewObserver( ExampleNode node, ExampleModel model ) {
    		_node = node;
    		_model = model;
    	}
    	
        public void propertyChange( PropertyChangeEvent event ) {
            if ( event.getPropertyName().equals( PNode.PROPERTY_TRANSFORM ) ) {
                updateModelPosition();
            }
        }
        
        private void updateModelPosition() {
            Point2D viewPoint = _node.getOffset();
            Point2D modelPoint = _model.getModelViewTransform().viewToModel( viewPoint );
            _model.getExampleModelElement().setPosition( modelPoint );
        }
    }
    

    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public ExampleNode getExampleNode() {
        return _exampleNode;
    }
    
    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Updates the layout of stuff on the canvas.
     */
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( GlaciersConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "PhysicsCanvas.updateLayout worldSize=" + worldSize );//XXX
        }
        
        //XXX lay out nodes
    }
}
