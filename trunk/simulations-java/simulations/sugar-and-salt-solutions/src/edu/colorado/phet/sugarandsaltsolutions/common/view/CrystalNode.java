// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Crystal;
import edu.umd.cs.piccolo.PNode;

import static java.awt.Color.white;

/**
 * Graphical representation of a salt or sugar crystal, shown as a white square
 *
 * @author Sam Reid
 */
public class CrystalNode extends PNode {
    public CrystalNode( final ModelViewTransform transform, final Crystal crystal ) {
        //Draw the shape of the salt crystal at its location
        addChild( new PhetPPath( white ) {{
            crystal.position.addObserver( new VoidFunction1<ImmutableVector2D>() {
                public void apply( ImmutableVector2D modelPosition ) {
                    ImmutableVector2D viewPosition = transform.modelToView( modelPosition );
                    double size = 6;
                    setPathTo( new Rectangle2D.Double( viewPosition.getX() - size / 2, viewPosition.getY() - size / 2, size, size ) );
                }
            } );
        }} );
    }
}
