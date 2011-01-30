// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class WaterDropNode extends PNode {
    public WaterDropNode( final ModelViewTransform transform, final WaterDrop waterDrop ) {
        double r = transform.modelToViewDeltaX( waterDrop.getRadius() );
        addChild( new PhetPPath( new Ellipse2D.Double( -r, -r, r * 2, r * 2 ), WaterTowerNode.WATER_COLOR ) {{
            waterDrop.position.addObserver( new SimpleObserver() {
                public void update() {
                    setOffset( transform.modelToView( waterDrop.position.getValue().toPoint2D() ) );
                }
            } );
        }} );
    }
}
