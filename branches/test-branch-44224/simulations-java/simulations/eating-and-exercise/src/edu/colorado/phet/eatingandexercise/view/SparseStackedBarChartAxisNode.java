package edu.colorado.phet.eatingandexercise.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Created by: Sam
 * Apr 17, 2008 at 9:33:00 PM
 */
public class SparseStackedBarChartAxisNode extends PNode {
    private Function function;
    private double majorTickSpacing;
    private double max;

    private static final double MINOR_TICK_WIDTH = 8;
    private static final double MAJOR_TICK_WIDTH = 10;

    public SparseStackedBarChartAxisNode( String title, Function function, double minorTickSpacing, double majorTickSpacing, double max ) {
        this.function = function;
        this.majorTickSpacing = majorTickSpacing;
        this.max = max;
        for ( double y = 0; y <= max; y += minorTickSpacing ) {
            if ( !isMajorTick( y ) ) {
                MinorTick minorTick = new MinorTick( MINOR_TICK_WIDTH, y );
                addChild( minorTick );
            }
        }

        for ( double y = 0; y <= max; y += majorTickSpacing ) {
            MajorTick majorTick = new MajorTick( MAJOR_TICK_WIDTH, y );
            addChild( majorTick );
        }
    }

    //todo: remove duplicate code with major tick generation, convert to straightforward calculation
    private boolean isMajorTick( double yValue ) {
        for ( double y = 0; y <= max; y += majorTickSpacing ) {
            if ( Math.abs( yValue - y ) < 1E-6 ) {
                return true;
            }
        }
        return false;
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
        public MajorTick( double width, double y ) {
            PhetPPath path = new PhetPPath( new Line2D.Double( -width / 2, -modelToView( y ), width / 2, -modelToView( y ) ), new BasicStroke( 2 ), Color.black );
            addChild( path );
            PText textLabel = new EatingAndExercisePText( new DecimalFormat( "0" ).format( y ) );
            textLabel.setFont( new PhetFont( 12, true ) );
            addChild( textLabel );
            textLabel.setOffset( path.getFullBounds().getCenterX() - textLabel.getFullBounds().getWidth() / 2,
                                 path.getFullBounds().getCenterY() - textLabel.getFullBounds().getHeight() / 2 );
            path.setVisible( false );
        }
    }

}