// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Graphic that depicts how the laser may be moved.  It is only shown when the cursor is over the laser and is non-interactive.
 *
 * @author Sam Reid
 */
public class TranslationDragHandle extends PNode {

    public TranslationDragHandle( final ImmutableVector2D tail, final double dx, final double dy, final BooleanProperty showDragHandles ) {
        showDragHandles.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( showDragHandles.get() );
            }
        } );

        //Update the location when laser pivot or emission point change
        final PNode counterClockwiseDragArrow = new PNode() {{
            addChild( new PhetPPath( new Arrow( tail.toPoint2D(), new Point2D.Double( tail.getX() + dx, tail.getY() + dy ), 14, 14, 8 ).getShape(), Color.red, new BasicStroke( 1 ), Color.black ) );
            setPickable( false );
            setChildrenPickable( false );
        }};
        addChild( counterClockwiseDragArrow );
    }
}