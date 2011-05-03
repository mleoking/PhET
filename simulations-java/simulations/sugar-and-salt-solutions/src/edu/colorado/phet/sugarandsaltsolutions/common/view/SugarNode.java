// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property2.Observer;
import edu.colorado.phet.common.phetcommon.model.property2.UpdateEvent;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Sugar;
import edu.umd.cs.piccolo.PNode;

import static java.awt.Color.lightGray;

/**
 * Graphical representation of a sugar crystal
 *
 * @author Sam Reid
 */
public class SugarNode extends PNode {
    public SugarNode( final ModelViewTransform transform, final Sugar sugar ) {
        //Draw the shape of the sugar crystal at its location
        addChild( new PhetPPath( lightGray ) {{
            sugar.position.addObserver( new Observer<ImmutableVector2D>() {
                @Override public void update( UpdateEvent<ImmutableVector2D> e ) {
                    ImmutableVector2D position = transform.modelToView( e.value );
                    setPathTo( new Rectangle2D.Double( position.getX(), position.getY(), 10, 10 ) );
                }
            } );
        }} );
    }
}
