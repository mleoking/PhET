// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Shows a graphical representation of the molecule.
 *
 * @author Sam Reid
 */
public class SucroseNode extends PNode {
    public SucroseNode( final ModelViewTransform transform, Sucrose sodiumIon, VoidFunction1<VoidFunction0> addFrameListener, S3Element naIon ) {
        addChild( new PImage( SugarAndSaltSolutionsApplication.RESOURCES.getImage( "sucrose.png" ) ) );
        scale( 0.5 );
//        sodiumIon.position.addObserver( new VoidFunction1<ImmutableVector2D>() {
//            public void apply( ImmutableVector2D immutableVector2D ) {
//                setOffset( transform.modelToView( immutableVector2D ).getX() - getFullBounds().getWidth() / 2,
//                           transform.modelToView( immutableVector2D ).getY() - getFullBounds().getHeight() / 2 );
//            }
//        } );
    }
}
