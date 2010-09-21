package edu.colorado.phet.eatingandexercise.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Created by: Sam
 * Apr 17, 2008 at 9:33:00 PM
 */
public class DefaultStackedBarChartAxisNode extends PNode {
    private Function function;
    private double minorTickSpacing;
    private double majorTickSpacing;
    private double max;

    private static final double MINOR_TICK_WIDTH = 8;
    private static final double MAJOR_TICK_WIDTH = 10;

    public DefaultStackedBarChartAxisNode( String title, Function function, double minorTickSpacing, double majorTickSpacing, double max ) {
        this.function = function;
        this.minorTickSpacing = minorTickSpacing;
        this.majorTickSpacing = majorTickSpacing;
        this.max = max;
        PPath arrowPath = new PhetPPath( createArrowShape(), Color.black );
        addChild( arrowPath );
        ArrayList minorTicks = new ArrayList();
        for ( double y = 0; y <= max; y += minorTickSpacing ) {
            MinorTick minorTick = new MinorTick( MINOR_TICK_WIDTH, y );
            addChild( minorTick );
            minorTicks.add( minorTick );
        }

        ArrayList majorTicks = new ArrayList();
        int count = 0;
        for ( double y = 0; y <= max; y += majorTickSpacing ) {
            MajorTick majorTick = new MajorTick( MAJOR_TICK_WIDTH, y, count % 2 == 0 );
            addChild( majorTick );
            majorTicks.add( majorTick );
            count++;
        }
        PText titleNode = new EatingAndExercisePText( title );
        titleNode.setFont( new PhetFont( 18, true ) );
        addChild( titleNode );

        PNode bottomMajor = (PNode) minorTicks.get( 0 );
        titleNode.setOffset( arrowPath.getFullBounds().getMinX() - titleNode.getFullBounds().getHeight(), bottomMajor.getFullBounds().getMinY() - titleNode.getFullBounds().getHeight() );
        titleNode.rotate( -Math.PI / 2 );
    }

    public class MinorTick extends PNode {
        public MinorTick( double width, double y ) {
            PhetPPath path = new PhetPPath( new Line2D.Double( -width / 2, -modelToView( y ), width / 2, -modelToView( y ) ), new BasicStroke( 1 ), Color.black );
            addChild( path );
        }
    }

    private double modelToView( double y ) {
        return function.evaluate( y );
    }

    public class MajorTick extends PNode {
        public MajorTick( double width, double y, boolean textOnLeft ) {
            PhetPPath path = new PhetPPath( new Line2D.Double( -width / 2, -modelToView( y ), width / 2, -modelToView( y ) ), new BasicStroke( 2 ), Color.black );
            addChild( path );
            PText textLabel = new EatingAndExercisePText( new DecimalFormat( "0" ).format( y ) );
            addChild( textLabel );
            textLabel.setOffset( path.getFullBounds().getX() - textLabel.getFullBounds().getWidth(),
                                 path.getFullBounds().getCenterY() - textLabel.getFullBounds().getHeight() / 2 );
            if ( !textOnLeft ) {
                textLabel.setOffset( path.getFullBounds().getMaxX(),
                                     path.getFullBounds().getCenterY() - textLabel.getFullBounds().getHeight() / 2 );
            }
        }
    }

    private Shape createArrowShape() {
        return new Arrow( new Point2D.Double( 0, 0 ), new Point2D.Double( 0, -modelToView( max ) ), 20, 20, 5 ).getShape();
    }
}
