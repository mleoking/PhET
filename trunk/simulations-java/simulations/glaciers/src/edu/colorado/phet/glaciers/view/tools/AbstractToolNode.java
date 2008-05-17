/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.Movable.MovableAdapter;
import edu.colorado.phet.glaciers.model.Movable.MovableListener;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * AbstractToolNode is the base class for all tool nodes.
 * Base functionality includes updating the tool's position as the node is dragged.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractToolNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font FONT = new PhetFont( 10 );
    private static final Border VALUE_BORDER = BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( Color.BLACK, 1 ), BorderFactory.createEmptyBorder( 1, 2, 1, 2 ) );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final AbstractTool _tool;
    private final ModelViewTransform _mvt;
    private final TrashCanIconNode _trashCanIconNode;
    private final MovableListener _movableListener;
    private final Point2D _pModel, _pView; // reusable points
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractToolNode( AbstractTool tool, ModelViewTransform mvt, TrashCanIconNode trashCanIconNode ) {
        super();
        
        _pModel = new Point2D.Double();
        _pView = new Point2D.Double();
        
        _tool = tool;
        _mvt = mvt;
        _trashCanIconNode = trashCanIconNode;
        
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
                getTool().setDragging( true );
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
                // do not call super.drag, or tool will wobble
            }
            
            protected void endDrag( PInputEvent event ) {
                if ( _trashCanIconNode.isInTrash( AbstractToolNode.this ) ) {
                    _trashCanIconNode.delete( AbstractToolNode.this );
                }
                else {
                    getTool().setDragging( false );
                }
                super.endDrag( event );
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
    
    protected static Font getValueFont() {
        return FONT;
    }
    
    protected static Border getValueBorder() {
        return VALUE_BORDER;
    }
}
