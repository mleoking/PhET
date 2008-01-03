/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.model.Viewport;
import edu.colorado.phet.glaciers.model.World;
import edu.colorado.phet.glaciers.model.Viewport.ViewportListener;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;


public class PenguinNode extends PImage {
    
    private World _world;
    private Viewport _viewport;

    public PenguinNode( World world, Viewport viewport ) {
        super( GlaciersImages.PENGUIN );
        
        _world = world;
        
        _viewport = viewport;
        _viewport.addListener( new ViewportListener() {
            public void boundsChanged() {
                updatePosition();
            }
        });
        
        addInputEventListener( new CursorHandler() );
        
        addInputEventListener( new PDragEventHandler() {

            private double _xOffset;

            protected void startDrag( PInputEvent event ) {
                Rectangle2D viewportBounds = _viewport.getBounds();
                _xOffset = event.getPosition().getX() - viewportBounds.getX();
                super.startDrag( event );
            }

            /*
             * Constrain dragging to horizontal, update the viewport, keep viewport within the world's bounds.
             */
            protected void drag( PInputEvent event ) {
                Rectangle2D viewportBounds = _viewport.getBounds();
                double x = event.getPosition().getX() - _xOffset;
                double y = viewportBounds.getY();
                double w = viewportBounds.getWidth();
                double h = viewportBounds.getHeight();
                Rectangle2D newViewportBounds = new Rectangle2D.Double( x, y, w, h );
                if ( _world.contains( newViewportBounds ) ) {
                    _viewport.setBounds( newViewportBounds );
                }
            }
        } );
    }
    
    /*
     * Keeps the penguin centered in the viewport.
     */
    private void updatePosition() {
        Rectangle2D worldBounds = _world.getBounds();
        Rectangle2D viewportBounds = _viewport.getBounds();
        double xOffset = viewportBounds.getCenterX() - getFullBoundsReference().getWidth()/2;
        double yOffset = worldBounds.getY() + worldBounds.getHeight() - getFullBoundsReference().getHeight();
        setOffset( xOffset, yOffset );
    }
}
