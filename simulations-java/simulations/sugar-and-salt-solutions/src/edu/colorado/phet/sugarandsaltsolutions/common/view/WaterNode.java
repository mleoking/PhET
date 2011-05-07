// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Water;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class WaterNode extends PNode {
    public WaterNode( final ModelViewTransform transform, final Water water ) {
        addChild( new PhetPPath( SugarAndSaltSolutionsApplication.WATER_COLOR ) {{
            water.volume.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( water.getShape() ) );
                }
            } );
        }} );
    }
}
