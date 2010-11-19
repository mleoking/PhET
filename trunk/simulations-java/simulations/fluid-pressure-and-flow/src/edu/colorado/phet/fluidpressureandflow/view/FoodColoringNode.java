package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.modules.fluidflow.FoodColoring;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class FoodColoringNode extends PNode {
    public FoodColoringNode( final ModelViewTransform transform, final FoodColoring foodColoring ) {
        addChild( new PhetPPath( Color.red ) {{
            foodColoring.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.createTransformedShape( foodColoring.getShape() ) );
                }
            } );
        }} );
    }
}
