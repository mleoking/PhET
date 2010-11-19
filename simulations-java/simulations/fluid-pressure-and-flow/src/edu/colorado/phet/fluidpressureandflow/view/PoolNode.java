package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.model.Pool;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class PoolNode extends PNode {
    private final Property<Double> liquidDensity;

    public PoolNode( final ModelViewTransform transform2D, final Pool pool, Property<Double> liquidDensity ) {
        this.liquidDensity = liquidDensity;
        final PhetPPath path = new PhetPPath( transform2D.modelToView( pool.getShape() ), createPaint( transform2D, pool ) );
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

    private GradientPaint createPaint( ModelViewTransform transform2D, Pool pool ) {
        Color topColor = getTopColor( liquidDensity.getValue() );
        Color bottomColor = getBottomColor( liquidDensity.getValue() );
        double yBottom = transform2D.modelToViewY( -pool.getHeight() );//fade color halfway down
        double yTop = transform2D.modelToViewY( 0 );
        return new GradientPaint( 0, (float) yTop, topColor, 0, (float) yBottom, bottomColor );
    }

    public static Color getBottomColor( double d ) {
        Function.LinearFunction r = new Function.LinearFunction( 1000, 1500, 35, 15 );
        Function.LinearFunction g = new Function.LinearFunction( 1000, 1500, 136, 120 );
        Function.LinearFunction b = new Function.LinearFunction( 1000, 1500, 158, 140 );
        Function.LinearFunction a = new Function.LinearFunction( 1000, 1500, 173, 174 );
        return new Color( (int) r.evaluate( d ), (int) g.evaluate( d ), (int) b.evaluate( d ), (int) a.evaluate( d ) );
    }

    public static Color getTopColor( double d ) {
        Function.LinearFunction rTop = new Function.LinearFunction( 1000, 1500, 90, 91 );
        Function.LinearFunction gTop = new Function.LinearFunction( 1000, 1500, 212, 213 );
        Function.LinearFunction bTop = new Function.LinearFunction( 1000, 1500, 238, 241 );
        Function.LinearFunction aTop = new Function.LinearFunction( 1000, 1500, 158, 172 );
        return new Color( (int) rTop.evaluate( d ), (int) gTop.evaluate( d ), (int) bTop.evaluate( d ), (int) aTop.evaluate( d ) );
    }
}
