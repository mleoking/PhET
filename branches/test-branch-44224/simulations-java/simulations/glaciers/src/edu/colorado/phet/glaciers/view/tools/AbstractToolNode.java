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
    private final TrashCanDelegate _trashCan;
    private final MovableListener _movableListener;
    private final Point2D _pModel, _pView; // reusable points
    private boolean _draggingEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractToolNode( AbstractTool tool, ModelViewTransform mvt, TrashCanDelegate trashCan ) {
        super();
        
        _pModel = new Point2D.Double();
        _pView = new Point2D.Double();
        
        _tool = tool;
        _mvt = mvt;
        _trashCan = trashCan;
        
        _movableListener = new MovableListener() {
            public void positionChanged() {
                updatePosition();
            }
            public void orientationChanged() {
                updateOrientation();
            }
        };
        
        _tool.addMovableListener( _movableListener );
        
        _draggingEnabled = true;
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PDragEventHandler() {
            
            private double _xOffset, _yOffset; // distance between mouse press and model origin, in view coordinates
            
            protected void startDrag( PInputEvent event ) {
                if ( _draggingEnabled ) {
                    AbstractToolNode.this.startDrag();
                    getTool().setDragging( true );
                    _mvt.modelToView( _tool.getPosition(), _pView );
                    _xOffset = event.getPosition().getX() - _pView.getX();
                    _yOffset = event.getPosition().getY() - _pView.getY();
                    super.startDrag( event );
                }
            }

            protected void drag( PInputEvent event ) {
                if ( _draggingEnabled ) {
                    double x = event.getPosition().getX() - _xOffset;
                    double y = event.getPosition().getY() - _yOffset;
                    _mvt.viewToModel( x, y, _pModel );
                    _tool.setPosition( _pModel );
                    // do not call super.drag, or tool will wobble
                }
            }
            
            protected void endDrag( PInputEvent event ) {
                if ( _draggingEnabled ) {
                    if ( _trashCan.isInTrash( event.getPosition() ) ) {
                        _trashCan.delete( AbstractToolNode.this, event.getPosition() );
                    }
                    else {
                        getTool().setDragging( false );
                    }
                    super.endDrag( event );
                    AbstractToolNode.this.endDrag();
                }
            }
        } );

        updatePosition();
    }
    
    public void setDraggingEnabled( boolean draggingEnabled ) {
        _draggingEnabled = draggingEnabled;
    }
    
    /*
     * Hook for subclasses to do something when dragging starts.
     */
    protected void startDrag() {}
    
    /*
     * Hook for subclasses to do something when dragging ends.
     */
    protected void endDrag() {}
    
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
    
    /**
     * Subclasses that need to switch between English and metric units should override this.
     * 
     * @param englishUnits true for English units, false for metric units
     */
    public void setEnglishUnits( boolean englishUnits ) {}
    
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
    
    /*
     * Update the node's orientation to match the tool.
     * Default behavior is to do nothing.
     */
    protected void updateOrientation() {}
    
    protected static Font getValueFont() {
        return FONT;
    }
    
    protected static Border getValueBorder() {
        return VALUE_BORDER;
    }
}
