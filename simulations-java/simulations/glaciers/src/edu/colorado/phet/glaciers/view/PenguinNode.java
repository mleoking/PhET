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

    public PenguinNode( Viewport birdsEyeViewport, Viewport zoomedViewport ) {
        super( GlaciersImages.PENGUIN );
        
        _birdsEyeViewport = birdsEyeViewport;
        
        _zoomedViewport = zoomedViewport;
        _zoomedViewport.addListener( new ViewportListener() {
            public void boundsChanged() {
                updatePosition();
            }
        });
        
        addInputEventListener( new CursorHandler() );
        
        addInputEventListener( new PDragEventHandler() {

            private double _xOffset;

            protected void startDrag( PInputEvent event ) {
                Rectangle2D viewportBounds = _zoomedViewport.getBounds();
                _xOffset = event.getPosition().getX() - viewportBounds.getX();
                super.startDrag( event );
            }

            /*
             * Constrain dragging to horizontal, update the viewport, keep viewport within the world's bounds.
             */
            protected void drag( PInputEvent event ) {
                Rectangle2D viewportBounds = _zoomedViewport.getBounds();
                double x = event.getPosition().getX() - _xOffset;
                double y = viewportBounds.getY();
                double w = viewportBounds.getWidth();
                double h = viewportBounds.getHeight();
                Rectangle2D newViewportBounds = new Rectangle2D.Double( x, y, w, h );
                if ( _birdsEyeViewport.contains( newViewportBounds ) ) {
                    _zoomedViewport.setBounds( newViewportBounds );
                }
            }
        } );
    }
    
    public void cleanup() {}
    
    /*
     * Keeps the penguin centered in the viewport.
     */
    private void updatePosition() {
        Rectangle2D worldBounds = _birdsEyeViewport.getBounds();
        Rectangle2D viewportBounds = _zoomedViewport.getBounds();
        double xOffset = viewportBounds.getCenterX() - getFullBoundsReference().getWidth()/2;
        double yOffset = worldBounds.getY() + worldBounds.getHeight() - getFullBoundsReference().getHeight();
        setOffset( xOffset, yOffset );
    }
}
