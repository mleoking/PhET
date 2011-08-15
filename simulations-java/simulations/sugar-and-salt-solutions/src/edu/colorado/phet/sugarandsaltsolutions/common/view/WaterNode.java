// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication.WATER_COLOR;

/**
 * Node that displays the water flowing out of a faucet, shown behind the faucet node so that it doesn't need to match up perfectly
 *
 * @author Sam Reid
 */
public class WaterNode extends PNode {
    public WaterNode( final ModelViewTransform transform, final Property<Shape> waterShape ) {
        addChild( new PhetPPath( WATER_COLOR ) {{
            waterShape.addObserver( new VoidFunction1<Shape>() {
                public void apply( Shape shape ) {
                    setPathTo( transform.modelToView( shape ) );
                }
            } );
        }} );
    }
}