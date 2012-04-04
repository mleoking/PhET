// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.intro.model.Shelf;
import edu.umd.cs.piccolo.PNode;

/**
 * Representation of a shelf in the view.
 *
 * @author John Blanco
 */
public class ShelfNode extends PNode {

    private static final boolean SHOW_2D_LOCATION = true;

    public ShelfNode( final Shelf shelf, final ModelViewTransform mvt ) {
        if ( SHOW_2D_LOCATION ) {

            DoubleGeneralPath surfacePath = new DoubleGeneralPath( mvt.modelToView( shelf.getPosition() ) ) {{
                lineTo( mvt.modelToViewX( shelf.getPosition().getX() ) + mvt.modelToViewDeltaX( shelf.getWidth() ),
                        mvt.modelToViewY( shelf.getPosition().getY() ) );
            }};
            addChild( new PhetPPath( surfacePath.getGeneralPath(), Color.RED ) );
        }
    }
}
