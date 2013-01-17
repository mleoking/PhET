// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.NumberFormat;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PadBoundsNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.model.LineFormsModel;
import edu.colorado.phet.linegraphing.slopeintercept.model.SlopeInterceptModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Slope indicator that the design team referred to as the "slope tool".
 * It displays the rise and run values of the slope.
 * Drawn in the global coordinate frame of the view.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeToolNode extends PComposite {

    // lines
    private static final Color LINE_COLOR = LGColors.SLOPE;
    private static final float STROKE_WIDTH = 1.25f;

    // values
    private static final int VALUE_X_SPACING = 6;
    private static final int VALUE_Y_SPACING = 6;
    private static final NumberFormat VALUE_FORMAT = new DefaultDecimalFormat( "0" );
    private static final PhetFont VALUE_FONT = new PhetFont( Font.BOLD, 16 );
    private static final Color VALUE_TEXT_COLOR = Color.BLACK;
    private static final double VALUE_X_MARGIN = 3;
    private static final double VALUE_Y_MARGIN = 3;
    private static final double VALUE_CORNER_RADIUS = 5;

    public SlopeToolNode( Property<Line> line, final ModelViewTransform mvt ) {

        // not interactive
        setPickable( false );
        setChildrenPickable( false );

        line.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {
                update( line, mvt );
            }
        } );
    }

    private void update( Line line, ModelViewTransform mvt ) {

        removeAllChildren();

        // Show nothing for horizontal or vertical lines.
        if ( line.rise == 0 || line.run == 0 ) {
            return;
        }

        // view coordinates
        final double gridXSpacing = mvt.modelToViewDeltaX( 1 );
        final double gridYSpacing = mvt.modelToViewDeltaY( 1 );
        Point2D p1View = mvt.modelToView( line.x1, line.y1 );
        Point2D p2View = mvt.modelToView( line.x2, line.y2 );

        // rise
        final double offsetFactor = 0.6;
        final double delimiterLengthFactor = 0.5;
        final PNode riseLineNode, riseTailDelimiterNode, riseTipDelimiterNode, riseValueNode;
        {
            riseValueNode = new NumberBackgroundNode( line.rise, VALUE_FORMAT,
                                                      VALUE_FONT, VALUE_TEXT_COLOR, LGColors.SLOPE,
                                                      VALUE_X_MARGIN, VALUE_Y_MARGIN, VALUE_CORNER_RADIUS );
            final double xOffset = offsetFactor * gridXSpacing;
            final double riseDelimiterLength = delimiterLengthFactor * gridXSpacing;
            final double tipFudgeY = ( line.rise > 0 ) ? STROKE_WIDTH : -STROKE_WIDTH;
            final double arrowX;
            if ( line.run > 0 ) {
                // value to left of line
                arrowX = p1View.getX() - xOffset;
                riseLineNode = new ArrowNode( arrowX, p1View.getY(), arrowX, p2View.getY() + tipFudgeY );
                riseValueNode.setOffset( riseLineNode.getFullBoundsReference().getMinX() - riseValueNode.getFullBoundsReference().getWidth() - VALUE_X_SPACING,
                                         riseLineNode.getFullBoundsReference().getCenterY() - ( riseValueNode.getFullBoundsReference().getHeight() / 2 ) );
            }
            else {
                // value to right of line
                arrowX = p1View.getX() + xOffset;
                riseLineNode = new ArrowNode( arrowX, p1View.getY(), arrowX, p2View.getY() + tipFudgeY );
                riseValueNode.setOffset( riseLineNode.getFullBoundsReference().getMaxX() + VALUE_X_SPACING,
                                         riseLineNode.getFullBoundsReference().getCenterY() - ( riseValueNode.getFullBoundsReference().getHeight() / 2 ) );
            }
            riseTailDelimiterNode = new DimensionalDelimiterNode( arrowX - ( riseDelimiterLength / 2 ), p1View.getY(), arrowX + ( riseDelimiterLength / 2 ), p1View.getY() );
            riseTipDelimiterNode = new DimensionalDelimiterNode( arrowX - ( riseDelimiterLength / 2 ), p2View.getY(), arrowX + ( riseDelimiterLength / 2 ), p2View.getY() );
        }

        // run
        final PNode runLineNode, runTailDelimiterNode, runTipDelimiterNode, runValueNode;
        {
            runValueNode = new NumberBackgroundNode( line.run, VALUE_FORMAT, VALUE_FONT,
                                                     VALUE_TEXT_COLOR, LGColors.SLOPE,
                                                     VALUE_X_MARGIN, VALUE_Y_MARGIN, VALUE_CORNER_RADIUS );
            final double yOffset = offsetFactor * gridYSpacing;
            final double runDelimiterLength = delimiterLengthFactor * gridYSpacing;
            final double tipFudgeX = ( line.run > 0 ) ? -1 : 1;
            final double arrowY;
            if ( line.rise > 0 ) {
                // value above line
                arrowY = p2View.getY() + yOffset;
                runLineNode = new ArrowNode( p1View.getX(), arrowY, p2View.getX() + tipFudgeX, arrowY );
                runValueNode.setOffset( runLineNode.getFullBoundsReference().getCenterX() - ( runValueNode.getFullBoundsReference().getWidth() / 2 ),
                                        runLineNode.getFullBoundsReference().getMinY() - riseValueNode.getFullBoundsReference().getHeight() - VALUE_Y_SPACING );
            }
            else {
                // value below line
                arrowY = p2View.getY() - yOffset;
                runLineNode = new ArrowNode( p1View.getX(), arrowY, p2View.getX() + tipFudgeX, arrowY );
                runValueNode.setOffset( runLineNode.getFullBoundsReference().getCenterX() - ( runValueNode.getFullBoundsReference().getWidth() / 2 ),
                                        runLineNode.getFullBoundsReference().getMaxY() + VALUE_Y_SPACING );
            }
            runTailDelimiterNode = new DimensionalDelimiterNode( p1View.getX(), arrowY - ( runDelimiterLength / 2 ), p1View.getX(), arrowY + ( runDelimiterLength / 2 ) );
            runTipDelimiterNode = new DimensionalDelimiterNode( p2View.getX(), arrowY - ( runDelimiterLength / 2 ), p2View.getX(), arrowY + ( runDelimiterLength / 2 ) );
        }

        // rendering order
        addChild( riseTailDelimiterNode );
        addChild( riseTipDelimiterNode );
        addChild( riseLineNode );
        addChild( runTailDelimiterNode );
        addChild( runTipDelimiterNode );
        addChild( runLineNode );
        addChild( riseValueNode );
        addChild( runValueNode );
    }

    // Can't use common-code ArrowNode because we want a different tip style.
    private static class ArrowNode extends PComposite {

        private static final PDimension TIP_SIZE = new PDimension( 6, 8 ); // use even-number dimensions, or tip will look asymmetrical due to rounding

        public ArrowNode( double tailX, double tailY, double tipX, double tipY ) {

            // nodes
            PNode lineNode = new PhetPPath( new Line2D.Double( tailX, tailY, tipX, tipY ), new BasicStroke( STROKE_WIDTH ), LINE_COLOR );
            DoubleGeneralPath tipPath = new DoubleGeneralPath();
            if ( tailX == tipX ) {
                // vertical arrow
                if ( tipY > tailY ) {
                    // pointing down
                    tipPath.moveTo( tipX - ( TIP_SIZE.getWidth() / 2 ), tipY - TIP_SIZE.getHeight() );
                    tipPath.lineTo( tipX, tipY );
                    tipPath.lineTo( tipX + ( TIP_SIZE.getWidth() / 2 ), tipY - TIP_SIZE.getHeight() );
                }
                else {
                    // pointing up
                    tipPath.moveTo( tipX - ( TIP_SIZE.getWidth() / 2 ), tipY + TIP_SIZE.getHeight() );
                    tipPath.lineTo( tipX, tipY );
                    tipPath.lineTo( tipX + ( TIP_SIZE.getWidth() / 2 ), tipY + TIP_SIZE.getHeight() );
                }
            }
            else if ( tailY == tipY ) {
                // horizontal arrow
                if ( tailX > tipX ) {
                    // pointing left
                    tipPath.moveTo( tipX + TIP_SIZE.getHeight(), tipY - ( TIP_SIZE.getWidth() / 2 ) );
                    tipPath.lineTo( tipX, tipY );
                    tipPath.lineTo( tipX + TIP_SIZE.getHeight(), tipY + ( TIP_SIZE.getWidth() / 2 ) );
                }
                else {
                    // pointing right
                    tipPath.moveTo( tipX - TIP_SIZE.getHeight(), tipY - ( TIP_SIZE.getWidth() / 2 ) );
                    tipPath.lineTo( tipX, tipY );
                    tipPath.lineTo( tipX - TIP_SIZE.getHeight(), tipY + ( TIP_SIZE.getWidth() / 2 ) );
                }
            }
            else {
                throw new UnsupportedOperationException( "this implementation supports only horizontal and vertical arrows" );
            }
            PNode tipNode = new PhetPPath( tipPath.getGeneralPath(), new BasicStroke( STROKE_WIDTH ), LINE_COLOR );

            // rendering order
            addChild( tipNode );
            addChild( lineNode );
        }
    }

    // Delimiter line that is at the end of a length line in a dimensional drawing.
    private static class DimensionalDelimiterNode extends PPath {

        public DimensionalDelimiterNode( double x1, double y1, double x2, double y2 ) {
            setStroke( new BasicStroke( STROKE_WIDTH ) );
            setStrokePaint( LINE_COLOR );
            setPathTo( new Line2D.Double( x1, y1, x2, y2 ) );
        }
    }

    // Return an icon that represents this feature.
    public static Icon createIcon( double width ) {

        PNode parentNode = new PadBoundsNode();

        LineFormsModel model = new SlopeInterceptModel();
        model.interactiveLine.set( Line.createSlopeIntercept( 1, 2, 0 ) ); // bigger values will make slope tool look smaller in icon

        // slope tool
        SlopeToolNode slopeToolNode = new SlopeToolNode( model.interactiveLine, model.mvt );
        parentNode.addChild( slopeToolNode );

        // dashed line where the line would be, tweaked visually
        PPath lineNode = new PPath( new Line2D.Double( slopeToolNode.getFullBoundsReference().getMinX() + ( 0.4 * slopeToolNode.getFullBoundsReference().getWidth() ), slopeToolNode.getFullBoundsReference().getMaxY(),
                                                       slopeToolNode.getFullBoundsReference().getMaxX(), slopeToolNode.getFullBoundsReference().getMinY() + ( 0.5 * slopeToolNode.getFullBoundsReference().getHeight() ) ) );
        lineNode.setStroke( new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 6, 6 }, 0 ) );
        parentNode.addChild( lineNode );

        // scale and convert to image
        parentNode.scale( width / parentNode.getFullBoundsReference().getWidth() );
        return new ImageIcon( parentNode.toImage() );
    }
}
