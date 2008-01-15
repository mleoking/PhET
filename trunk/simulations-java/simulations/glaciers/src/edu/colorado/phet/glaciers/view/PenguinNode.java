/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.model.Viewport;
import edu.colorado.phet.glaciers.model.Viewport.ViewportListener;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;


public class PenguinNode extends PImage {
    
    private Viewport _birdsEyeViewport;
    private Viewport _zoomedViewport;
    private ModelViewTransform _mvt;

    public PenguinNode( Viewport birdsEyeViewport, Viewport zoomedViewport, ModelViewTransform mvt ) {
        super( GlaciersImages.PENGUIN );
        
        _birdsEyeViewport = birdsEyeViewport;
        _birdsEyeViewport.addViewportListener( new ViewportListener() {
            public void boundsChanged() {
                updateScale();
                updateOffset();
            }
        });
        
        _zoomedViewport = zoomedViewport;
        _zoomedViewport.addViewportListener( new ViewportListener() {
            public void boundsChanged() {
                updateOffset();
            }
        });
        
        _mvt = mvt;
        
        addInputEventListener( new CursorHandler() );
        
        addInputEventListener( new PDragEventHandler() {

            private double _xOffset;

            protected void startDrag( PInputEvent event ) {
                Rectangle2D rModel = _zoomedViewport.getBoundsReference();
                Rectangle2D rView = _mvt.modelToView( rModel  );
                _xOffset = event.getPosition().getX() - rView.getX();
                super.startDrag( event );
            }

            /*
             * Constrain dragging to horizontal, update the viewport, 
             * keep left and right edges of of zoomed viewport within the birds-eye viewport.
             */
            protected void drag( PInputEvent event ) {
                Rectangle2D rModel = _zoomedViewport.getBoundsReference();
                Rectangle2D rView = _mvt.modelToView( rModel );
                double xView = event.getPosition().getX() - _xOffset;
                rView.setRect( xView, rView.getY(), rView.getWidth(), rView.getHeight() );
                rModel = _mvt.viewToModel( rView );
                Rectangle2D bb = _birdsEyeViewport.getBoundsReference();
                if ( rModel.getX() < bb.getX() ) {
                    rModel.setRect( bb.getX(), rModel.getY(), rModel.getWidth(), rModel.getHeight() );
                }
                else if ( rModel.getMaxX() > bb.getMaxX() ) {
                    rModel.setRect( bb.getMaxX() - rModel.getWidth(), rModel.getY(), rModel.getWidth(), rModel.getHeight() );
                }
                _zoomedViewport.setBounds( rModel );
            }
        } );
    }
    
    public void cleanup() {}
    
    /*
     * Centered at the bottom of the birds-eye viewport.
     */
    private void updateOffset() {
        Rectangle2D rModel = _zoomedViewport.getBoundsReference();
        Rectangle2D rView = _mvt.modelToView( rModel );
        double xOffset = rView.getCenterX() - ( getFullBoundsReference().getWidth() / 2 );
        double yOffset = _mvt.modelToView( _birdsEyeViewport.getBoundsReference() ).getMaxY() - getFullBoundsReference().getHeight();
        setOffset( xOffset, yOffset );
    }
    
    /*
     * Scales the penguin to fit into the birds-eye viewport.
     */
    private void updateScale() {
        setScale( 1 );
        final double portionOfViewportToFill = 0.75; // percent of birds-eye view height to be filled by the penguin
        double desiredHeight = portionOfViewportToFill * ( _mvt.modelToView( _birdsEyeViewport.getBoundsReference() ).getMaxY() - _mvt.modelToView( 0, _birdsEyeViewport.getBoundsReference().getY() ).getY() );
        double penguinHeight = getFullBoundsReference().getHeight();
        double yScale = 1;
        if ( penguinHeight > desiredHeight ) {
            // scale the penguin down
            yScale = 1 - ( penguinHeight - desiredHeight ) / penguinHeight;
            System.out.println( "scaling penguin down " + yScale + " ph=" + penguinHeight + " dh=" + desiredHeight );//XXX
        }
        else {
            // scale the penguin up
            yScale = 1 + ( desiredHeight - penguinHeight ) / desiredHeight;
            System.out.println( "scaling penguin up " + yScale );//XXX
        }
        setScale( yScale );
    }
}
