// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.common.view.PoolNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class WaterDropNode extends PNode {
    public WaterDropNode( final ModelViewTransform transform, final WaterDrop waterDrop, Property<Double> fluidDensity ) {
        double r = transform.modelToViewDeltaX( waterDrop.radius.getValue() );
        addChild( new PhetPPath( new Ellipse2D.Double( -r, -r, r * 2, r * 2 ), new Color( PoolNode.getTopColor( fluidDensity.getValue() ).getRGB() ) ) {{
            waterDrop.position.addObserver( new SimpleObserver() {
                public void update() {
                    setOffset( transform.modelToView( waterDrop.position.getValue().toPoint2D() ) );
                }
            } );
            waterDrop.radius.addObserver( new SimpleObserver() {
                public void update() {
                    double r = transform.modelToViewDeltaX( waterDrop.radius.getValue() );
                    setPathTo( new Ellipse2D.Double( -r, -r, r * 2, r * 2 ) );
                }
            } );
        }} );
    }
}
