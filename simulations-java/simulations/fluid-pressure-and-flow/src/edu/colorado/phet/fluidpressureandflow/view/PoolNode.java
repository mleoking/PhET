package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.model.Pool;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class PoolNode extends PNode {
    private final Property<Double> liquidDensity;

    public PoolNode( final ModelViewTransform2D transform2D, final Pool pool, Property<Double> liquidDensity ) {
        this.liquidDensity = liquidDensity;
        final PhetPPath path = new PhetPPath( transform2D.createTransformedShape( pool.getShape() ), createPaint( transform2D, pool ) );
        addChild( path );
        setPickable( false );
        setChildrenPickable( false );
        liquidDensity.addObserver( new SimpleObserver() {
            public void update() {
                path.setPaint( createPaint( transform2D, pool ) );
            }
        } );
    }

    //must be transparent so objects can submerge
    private GradientPaint createPaint( ModelViewTransform2D transform2D, Pool pool ) {
        int x = 14;
        Function.LinearFunction rTop = new Function.LinearFunction( 1000, 1500, 90, 105 - x );
        Function.LinearFunction gTop = new Function.LinearFunction( 1000, 1500, 212, 227 - x );
        Function.LinearFunction bTop = new Function.LinearFunction( 1000, 1500, 238, 255 - x );
        Function.LinearFunction aTop = new Function.LinearFunction( 1000, 1500, 158, 158 + x );

        Function.LinearFunction rBottom = new Function.LinearFunction( 1000, 1500, 35, 50 - x );
        Function.LinearFunction gBottom = new Function.LinearFunction( 1000, 1500, 136, 151 - x );
        Function.LinearFunction bBottom = new Function.LinearFunction( 1000, 1500, 158, 173 - x );
        Function.LinearFunction aBottom = new Function.LinearFunction( 1000, 1500, 173, 188 + x );

        final double d = liquidDensity.getValue();
        Color topColor = new Color( (int) rTop.evaluate( d ), (int) gTop.evaluate( d ), (int) bTop.evaluate( d ), (int) aTop.evaluate( d ) );
        Color bottomColor = new Color( (int) rBottom.evaluate( d ), (int) gBottom.evaluate( d ), (int) bBottom.evaluate( d ), (int) aBottom.evaluate( d ) );
        double yBottom = transform2D.modelToViewYDouble( -pool.getHeight() );//fade color halfway down
        double yTop = transform2D.modelToViewYDouble( 0 );
        return new GradientPaint( 0, (float) yTop, topColor, 0, (float) yBottom, bottomColor );
    }
}
