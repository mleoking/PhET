// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.pressure.view.WaterColor;
import edu.colorado.phet.fluidpressureandflow.watertower.model.WaterDrop;
import edu.umd.cs.piccolo.PNode;

/**
 * Graphic for a single drop of water
 *
 * @author Sam Reid
 */
public class WaterDropNode extends PNode {
    public WaterDropNode( final ModelViewTransform transform, final WaterDrop waterDrop, Property<Double> fluidDensity ) {
        double r = transform.modelToViewDeltaX( waterDrop.radius.get() );
        addChild( new PhetPPath( new Ellipse2D.Double( -r, -r, r * 2, r * 2 ), new Color( WaterColor.getTopColor( fluidDensity.get() ).getRGB() ) ) {{

            //Update when size or position change
            waterDrop.position.addObserver( new SimpleObserver() {
                public void update() {
                    setOffset( transform.modelToView( waterDrop.position.get().toPoint2D() ) );
                }
            } );
            waterDrop.radius.addObserver( new SimpleObserver() {
                public void update() {
                    double r = transform.modelToViewDeltaX( waterDrop.radius.get() );
                    setPathTo( new Ellipse2D.Double( -r, -r, r * 2, r * 2 ) );
                }
            } );
        }} );
    }
}
