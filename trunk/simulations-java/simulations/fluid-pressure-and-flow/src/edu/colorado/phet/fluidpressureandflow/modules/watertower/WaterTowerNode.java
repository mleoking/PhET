package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class WaterTowerNode extends PNode {
    public WaterTowerNode( final ModelViewTransform transform, final WaterTower waterTower ) {
        addChild( new PhetPPath( Color.gray ) {{
            setPathTo( transform.modelToView( waterTower.getTank() ) );

        }} );
    }
}
