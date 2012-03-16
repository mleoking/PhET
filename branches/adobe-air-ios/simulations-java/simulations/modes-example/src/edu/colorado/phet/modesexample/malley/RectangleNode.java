// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.modesexample.malley;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Visual representation of a rectangle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RectangleNode extends PPath {

    public RectangleNode( final Rectangle rectangle ) {

        // update geometry
        RichSimpleObserver geometryObserver = new RichSimpleObserver() {
            @Override
            public void update() {
                setPathTo( new Rectangle2D.Double( rectangle.x.get(), rectangle.y.get(), rectangle.width.get(), rectangle.height.get() ) );
            }
        };
        geometryObserver.observe( rectangle.x, rectangle.y, rectangle.width, rectangle.height );


        // update fill color
        rectangle.fillColor.addObserver( new SimpleObserver() {
            public void update() {
                setPaint( rectangle.fillColor.get() );
            }
        } );

        // update stroke color
        rectangle.strokeColor.addObserver( new SimpleObserver() {
            public void update() {
                setStrokePaint( rectangle.strokeColor.get() );
            }
        } );

        // interactivity
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PDragSequenceEventHandler() {

            private double clickXOffset;
            private double clickYOffset;

            /*
             * Note our offset from the rectangles origin when we start dragging.
             * No model-view transform here, assuming model and view are in same coordinate frame.
             */
            @Override
            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                Point2D pMouse = event.getPositionRelativeTo( RectangleNode.this.getParent() );
                clickXOffset = pMouse.getX() - rectangle.x.get();
                clickYOffset = pMouse.getY() - rectangle.y.get();
            }

            /*
             * Change the rectangle model's location as we drag.
             * No model-view transform here, assuming model and view are in same coordinate frame.
             */
            @Override
            protected void drag( final PInputEvent event ) {
                super.drag( event );
                Point2D pMouse = event.getPositionRelativeTo( RectangleNode.this.getParent() );
                rectangle.x.set( (int) ( pMouse.getX() - clickXOffset ) );
                rectangle.y.set( (int) ( pMouse.getY() - clickYOffset ) );
            }
        } );

    }
}
