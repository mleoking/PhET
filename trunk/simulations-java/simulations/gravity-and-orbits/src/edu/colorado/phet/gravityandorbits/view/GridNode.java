// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * When enabled, shows a grid across the play area that helps the user to make quantitative comparisons between distances.
 *
 * @author Sam Reid
 */
public class GridNode extends PNode {
    private static final int NUM_GRID_LINES = 10;

    public GridNode( final Property<ModelViewTransform> transform, final double spacing, final Point2D.Double center ) {
        setPickable( false );
        setChildrenPickable( false );

        transform.addObserver( new SimpleObserver() {
            public void update() {
                removeAllChildren();
                //horizontal lines
                for ( int i = -NUM_GRID_LINES; i <= NUM_GRID_LINES; i++ ) {
                    double y = i * spacing + center.y;
                    double x1 = NUM_GRID_LINES * spacing + center.x;
                    double x2 = -NUM_GRID_LINES * spacing + center.x;
                    addGridLine( new Line2D.Double( x1, y, x2, y ), transform );
                }

                //vertical lines
                for ( int i = -NUM_GRID_LINES; i <= NUM_GRID_LINES; i++ ) {
                    double x = i * spacing + center.x;
                    double y1 = NUM_GRID_LINES * spacing + center.y;
                    double y2 = -NUM_GRID_LINES * spacing + center.y;
                    addGridLine( new Line2D.Double( x, y1, x, y2 ), transform );
                }
            }
        } );
    }

    private void addGridLine( Line2D.Double line, Property<ModelViewTransform> transform ) {
        PhetPPath path = new PhetPPath( transform.getValue().modelToView( line ), new BasicStroke( 1 ), Color.darkGray );
        addChild( path );
    }
}
