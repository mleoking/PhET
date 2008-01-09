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
    private Rectangle2D _rView; // reusable rectangle

    public PenguinNode( Viewport birdsEyeViewport, Viewport zoomedViewport, ModelViewTransform mvt ) {
        super( GlaciersImages.PENGUIN );
        
        _birdsEyeViewport = birdsEyeViewport;
        
        _zoomedViewport = zoomedViewport;
        _zoomedViewport.addListener( new ViewportListener() {
            public void boundsChanged() {
                updatePosition();
            }
        });
        
        _mvt = mvt;
        
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
             * Constrain dragging to horizontal, update the viewport, keep viewport within the world's bounds.
             */
            protected void drag( PInputEvent event ) {
                Rectangle2D rModel = _zoomedViewport.getBoundsReference();
                _mvt.modelToView( rModel, _rView );
                double x = event.getPosition().getX() - _xOffset;
                _rView.setRect( x, _rView.getY(), _rView.getWidth(), _rView.getHeight() );
                if ( _birdsEyeViewport.contains( _rView ) ) {
                    _zoomedViewport.setBounds( _rView );
                }
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
