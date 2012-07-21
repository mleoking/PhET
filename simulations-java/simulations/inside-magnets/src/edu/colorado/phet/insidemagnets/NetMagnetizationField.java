// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.insidemagnets;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class NetMagnetizationField extends PNode {
    public NetMagnetizationField( ModelViewTransform2D transform, InsideMagnetsModel model ) {
        final Property<Vector2D> magnetizationProperty = model.getNetMagnetizationProperty();
        final ArrowNode arrowNode = new ArrowNode( new Point2D.Double(), new Point2D.Double( 100, 100 ), 20, 20, 10, 0.5, true ) {{
            setPaint( Color.red );
        }};
        addChild( arrowNode );
        setOffset( transform.modelToViewDouble( model.getLattice().getWidth() / 2, model.getLattice().getHeight() / 2 ) );
        magnetizationProperty.addObserver( new SimpleObserver() {
            public void update() {
                Vector2D magnetization = magnetizationProperty.get();
                magnetization = magnetization.times( -20 );
                arrowNode.setTipAndTailLocations( new Point2D.Double( -magnetization.getX() / 2, -magnetization.getY() / 2 ),
                                                  new Point2D.Double( magnetization.getX() / 2, magnetization.getY() / 2 ) );
            }
        } );
    }
}
