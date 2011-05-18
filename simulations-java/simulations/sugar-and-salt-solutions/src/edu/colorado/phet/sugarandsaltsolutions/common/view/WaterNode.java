// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo graphic that shows the water in the beaker.  It may be displaced upward by solid precipitate.
 *
 * @author Sam Reid
 */
public class WaterNode extends PNode {
    public WaterNode( final ModelViewTransform transform, final ObservableProperty<Double> displacedWaterVolume, final Beaker beaker, Color color ) {
        addChild( new PhetPPath( color ) {{
            displacedWaterVolume.addObserver( new VoidFunction1<Double>() {
                public void apply( Double displacedVolume ) {
                    setPathTo( transform.modelToView( beaker.getFluidShape( displacedVolume ) ) );
                }
            } );
        }} );
    }
}
