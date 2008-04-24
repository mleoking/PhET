/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.Movable.MovableAdapter;
import edu.colorado.phet.glaciers.model.Movable.MovableListener;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * AbstractToolNode is the base class for all tool nodes.
 * Base functionality includes updating the tool's position as the node is dragged.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractToolNode extends PComposite {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractTool _tool;
    private ModelViewTransform _mvt;
    private MovableListener _movableListener;
    private Point2D _pModel, _pView; // reusable points
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractToolNode( AbstractTool tool, ModelViewTransform mvt ) {
        super();
        
        _pModel = new Point2D.Double();
        _pView = new Point2D.Double();
        
        _tool = tool;
        _mvt = mvt;
        
        _movableListener = new MovableAdapter() {
            public void positionChanged() {
                updatePosition();
            }
        };
        
        _tool.addMovableListener( _movableListener );
        
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PDragEventHandler() {
            
            private double _xOffset, _yOffset; // distance between mouse press and model origin, in view coordinates
            
            protected void startDrag( PInputEvent event ) {
                _mvt.modelToView( _tool.getPosition(), _pView );
                _xOffset = event.getPosition().getX() - _pView.getX();
                _yOffset = event.getPosition().getY() - _pView.getY();
                super.startDrag( event );
            }

            protected void drag( PInputEvent event ) {
                double x = event.getPosition().getX() - _xOffset;
                double y = event.getPosition().getY() - _yOffset;
                _mvt.viewToModel( x, y, _pModel );
                _tool.setPosition( _pModel );
            }
        } );

        updatePosition();
    }
    
    /**
     * Call this before releasing all references to an object of this type.
     */
    public void cleanup() {
        _tool.removeMovableListener( _movableListener );
    }
    
    /**
     * Gets the tool model element that is associated with this node.
     * @return
     */
    public AbstractTool getTool() {
        return _tool;
    }
    
    /*
     * Provides access to the model-view transform for subclasses.
     */
    protected ModelViewTransform getModelViewTransform() {
        return _mvt;
    }
    
    /*
     * Updates the node's position to match the tool.
     */
    protected void updatePosition() {
        _mvt.modelToView( _tool.getPositionReference(), _pView );
        setOffset( _pView );
    }
}
