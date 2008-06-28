package edu.colorado.phet.eatingandexercise.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Jun 27, 2008 at 8:37:27 AM
 */
public class HealthBar extends PNode {
    private Function.LinearFunction modelToView;
    private double min;
    private double max;
    private double optimal;
    private double viewHeight;
    private double viewWidth = 20;

    public HealthBar( String name, double min, double max, double optimal, double viewHeight ) {
        this.modelToView = new Function.LinearFunction( max, min, 0, viewHeight );//flip y axis
        this.min = min;
        this.max = max;
        this.optimal = optimal;
        this.viewHeight = viewHeight;
        PhetPPath boundary = new PhetPPath( new Rectangle2D.Double( 0, 0, viewWidth, viewHeight ), new BasicStroke( 1 ), Color.black );

        HTMLNode label = new HTMLNode( name );
        addChild( label );
        label.rotate( -Math.PI / 2 );
        label.translate( -label.getFullBounds().getHeight() - boundary.getFullBounds().getHeight() - 4, 0 );
        addChild( getPath( max ) );
        addChild( getPath( min ) );

        addChild( boundary );
    }

    private PhetPPath getPath( double extremum ) {
        Point2D optimalCorner = new Point2D.Double( 0, modelToView.evaluate( optimal ) );
        Point2D extremumCorner = new Point2D.Double( 0, modelToView.evaluate( extremum ) );

        GradientPaint gradientPaint = new GradientPaint( optimalCorner, Color.green, extremumCorner, Color.red );
        Rectangle2D.Double rec = new Rectangle2D.Double();
        rec.setFrameFromDiagonal( optimalCorner, new Point2D.Double( extremumCorner.getX() + viewWidth, extremumCorner.getY() ) );
        PhetPPath bottom = new PhetPPath( rec, gradientPaint );
        return bottom;
    }

    public double getViewY( double value ) {
        return modelToView.evaluate( value );
    }

}
