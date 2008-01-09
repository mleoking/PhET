/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.view.Viewport.ViewportListener;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;


public class PenguinNode extends PImage {
    
    private Viewport _birdsEyeViewport;
    private Viewport _zoomedViewport;
    private ModelViewTransform _mvt;
    private Rectangle2D _rModel, _rView; // reusable rectangles

    public PenguinNode( Viewport birdsEyeViewport, Viewport zoomedViewport, ModelViewTransform mvt ) {
        super( GlaciersImages.PENGUIN );
        
        _birdsEyeViewport = birdsEyeViewport;
        
        _zoomedViewport = zoomedViewport;
        _zoomedViewport.addViewportListener( new ViewportListener() {
            public void boundsChanged() {
                updatePosition();
            }
        });
        
        _mvt = mvt;
        
        _rModel = new Rectangle2D.Double();
        _rView = new Rectangle2D.Double();
        
        addInputEventListener( new CursorHandler() );
        
        addInputEventListener( new PDragEventHandler() {

            private double _xOffset;

            protected void startDrag( PInputEvent event ) {
                Rectangle2D rModel = _zoomedViewport.getBoundsReference();
                _mvt.modelToView( rModel, _rView );
                _xOffset = event.getPosition().getX() - _rView.getX();
                super.startDrag( event );
            }

            /*
             * Constrain dragging to horizontal, update the viewport, 
             * keep left and right edges of of zoomed viewport within the birds-eye viewport.
             */
            protected void drag( PInputEvent event ) {
                Rectangle2D rModel = _zoomedViewport.getBoundsReference();
                _mvt.modelToView( rModel, _rView );
                double xView = event.getPosition().getX() - _xOffset;
                _rView.setRect( xView, _rView.getY(), _rView.getWidth(), _rView.getHeight() );
                _mvt.viewToModel( _rView, _rModel );
                Rectangle2D bb = _birdsEyeViewport.getBoundsReference();
                if ( _rModel.getX() < bb.getX() ) {
                    _rModel.setRect( bb.getX(), _rModel.getY(), _rModel.getWidth(), _rModel.getHeight() );
                }
                else if ( _rModel.getMaxX() > bb.getMaxX() ) {
                    _rModel.setRect( bb.getMaxX() - _rModel.getWidth(), _rModel.getY(), _rModel.getWidth(), _rModel.getHeight() );
                }
                _zoomedViewport.setBounds( _rModel );
            }
        } );
    }
    
    public void cleanup() {}
    
    /*
     * Keeps the penguin centered in the viewport.
     */
    private void updatePosition() {
        Rectangle2D rModel = _zoomedViewport.getBoundsReference();
        _mvt.modelToView( rModel, _rView );
        double xOffset = _rView.getCenterX() - getFullBoundsReference().getWidth()/2;
        setOffset( xOffset, getYOffset() );
    }
}
