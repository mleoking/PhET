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
                setPathTo( new Rectangle2D.Double( rectangle.x.getValue(), rectangle.y.getValue(), rectangle.width.getValue(), rectangle.height.getValue() ) );
            }
        };
        geometryObserver.observe( rectangle.x, rectangle.y, rectangle.width, rectangle.height );


        // update fill color
        rectangle.fillColor.addObserver( new SimpleObserver() {
            public void update() {
                setPaint( rectangle.fillColor.getValue() );
            }
        } );

        // update stroke color
        rectangle.strokeColor.addObserver( new SimpleObserver() {
            public void update() {
                setStrokePaint( rectangle.strokeColor.getValue() );
            }
        } );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PDragSequenceEventHandler() {

            private double clickXOffset
                    ,
                    clickYOffset;

            // no model-view transform here, assuming model and view are in same coordinate frame
            @Override
            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                Point2D pMouse = event.getPositionRelativeTo( RectangleNode.this.getParent() );
                clickXOffset = pMouse.getX() - rectangle.x.getValue();
                clickYOffset = pMouse.getY() - rectangle.y.getValue();
            }

            // no model-view transform here, assuming model and view are in same coordinate frame
            @Override
            protected void drag( final PInputEvent event ) {
                super.drag( event );
                Point2D pMouse = event.getPositionRelativeTo( RectangleNode.this.getParent() );
                rectangle.x.setValue( (int) ( pMouse.getX() - clickXOffset ) );
                rectangle.y.setValue( (int) ( pMouse.getY() - clickYOffset ) );
            }
        } );

    }
}
