package edu.colorado.phet.fitness.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Created by: Sam
 * Apr 17, 2008 at 9:33:00 PM
 */
public class StackedBarChartAxisNode extends PNode {
    private double minorTickSpacing;
    private double majorTickSpacing;
    private double max;

    private static final double MINOR_TICK_WIDTH = 8;
    private static final double MAJOR_TICK_WIDTH = 10;

    public StackedBarChartAxisNode( double minorTickSpacing, double majorTickSpacing, double max ) {
        this.minorTickSpacing = minorTickSpacing;
        this.majorTickSpacing = majorTickSpacing;
        this.max = max;
        PPath arrowPath = new PhetPPath( createArrowShape(), Color.black );
        addChild( arrowPath );
        for ( double y = 0; y <= max; y += minorTickSpacing ) {
            addChild( new MinorTick( MINOR_TICK_WIDTH, y ) );
        }
        int count = 0;
        for ( double y = 0; y <= max; y += majorTickSpacing ) {
            addChild( new MajorTick( MAJOR_TICK_WIDTH, y, count % 2 == 0 ) );
            count++;
        }
    }

    public static class MinorTick extends PNode {
        public MinorTick( double width, double y ) {
            PhetPPath path = new PhetPPath( new Line2D.Double( -width / 2, -y, width / 2, -y ), new BasicStroke( 1 ), Color.black );
            addChild( path );
        }
    }

    public static class MajorTick extends PNode {
        public MajorTick( double width, double y, boolean textOnLeft ) {
            PhetPPath path = new PhetPPath( new Line2D.Double( -width / 2, -y, width / 2, -y ), new BasicStroke( 2 ), Color.black );
            addChild( path );
            PText textLabel = new PText( new DecimalFormat( "0.00" ).format( y ) );
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
        return new Arrow( new Point2D.Double( 0, 0 ), new Point2D.Double( 0, -max ), 20, 20, 5 ).getShape();
    }
}
