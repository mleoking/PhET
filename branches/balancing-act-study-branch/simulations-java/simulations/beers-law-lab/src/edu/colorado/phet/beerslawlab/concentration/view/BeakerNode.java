// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.beerslawlab.concentration.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.text.MessageFormat;

import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.concentration.model.Beaker;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of a beaker that is filled to the top with a solution.
 * Can be configured with ticks on the left or right edge of the beaker.
 * Origin is at the bottom center.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class BeakerNode extends PComposite {

    // Which edge of the beaker are the ticks on? This flip-flopped a few times during development, so I chose to keep this feature for posterity.
    public enum TicksLocation {LEFT, RIGHT}

    private static final double MAX_VOLUME = 1; // L

    private static final Stroke STROKE = new BasicStroke( 6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    private static final Color STROKE_COLOR = Color.BLACK;

    private static final String[] MAJOR_TICK_LABELS = { "\u00bd", "1" }; // 1/2L, 1L
    private static final Color TICK_COLOR = Color.BLACK;
    private static final double MINOR_TICK_SPACING = 0.1; // L
    private static final int MINOR_TICKS_PER_MAJOR_TICK = 5;
    private static final double MAJOR_TICK_LENGTH = 30;
    private static final double MINOR_TICK_LENGTH = 15;
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
    private static final Stroke MINOR_TICK_STROKE = new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
    private static final Font TICK_LABEL_FONT = new PhetFont( 24 );
    private static final double TICK_LABEL_X_SPACING = 8;
    private static final double RIM_OFFSET = 20;

    private final Beaker beaker;
    private final PPath outlineNode;
    private final GeneralPath outlinePath;
    private final PComposite ticksNode;

    public BeakerNode( final Beaker beaker, TicksLocation ticksLocation ) {
        super();

        // not interactive
        setPickable( false );
        setChildrenPickable( false );

        this.beaker = beaker;

        outlinePath = new GeneralPath();
        outlineNode = new PPath();
        outlineNode.setPaint( null );
        outlineNode.setStroke( STROKE );
        outlineNode.setStrokePaint( STROKE_COLOR );
        addChild( outlineNode );

        ticksNode = new PComposite();
        addChild( ticksNode );

        setOffset( beaker.location.toPoint2D() );
        createOutline();
        updateTicks( ticksLocation );
    }

    // Where the beaker's origin is in relation to its width.
    private double getOriginXOffset() {
        return beaker.size.getWidth() / 2; // origin a midpoint
    }

    // Where the beaker's origin is in relation to its height.
    private double getOriginYOffset() {
        return beaker.size.getHeight(); // origin at bottom
    }

    /*
    * Creates the shape for the beaker.
    * Origin is at the bottom center.
    * Start drawing the shape at the upper-left corner.
    */
    private void createOutline() {
        outlinePath.reset();
        double xOffset = getOriginXOffset();
        double yOffset = getOriginYOffset();
        outlinePath.moveTo( (float) ( -xOffset - RIM_OFFSET ), (float) ( -yOffset - RIM_OFFSET ) );
        outlinePath.lineTo( (float) -xOffset, (float) -yOffset );
        outlinePath.lineTo( (float) -xOffset, (float) ( beaker.size.getHeight() - yOffset ) );
        outlinePath.lineTo( (float) ( beaker.size.getWidth() - xOffset ), (float) ( beaker.size.getHeight() - yOffset ) );
        outlinePath.lineTo( (float) ( beaker.size.getWidth() - xOffset ), (float) -yOffset );
        outlinePath.lineTo( (float) ( beaker.size.getWidth() - xOffset + RIM_OFFSET ), (float) ( -yOffset - RIM_OFFSET ) );
        outlineNode.setPathTo( outlinePath );
    }

    private void updateTicks( TicksLocation ticksLocation ) {

        ticksNode.removeAllChildren();

        final int numberOfTicks = (int) Math.round( MAX_VOLUME / MINOR_TICK_SPACING );
        final double leftX = -getOriginXOffset(); // don't use bounds or position will be off because of stroke width
        final double rightX = getOriginXOffset();
        final double bottomY = beaker.size.getHeight() - getOriginYOffset(); // don't use bounds or position will be off because of stroke width
        double deltaY = beaker.size.getHeight() / numberOfTicks;
        for ( int i = 1; i <= numberOfTicks; i++ ) {
            final double y = bottomY - ( i * deltaY );
            if ( i % MINOR_TICKS_PER_MAJOR_TICK == 0 ) {
                // major tick
                double x1 = ( ticksLocation == TicksLocation.LEFT ) ? leftX : rightX - MAJOR_TICK_LENGTH;
                double x2 = ( ticksLocation == TicksLocation.LEFT ) ? leftX + MAJOR_TICK_LENGTH : rightX;
                Shape tickPath = new Line2D.Double( x1, y, x2, y );
                PPath tickNode = new PPath( tickPath );
                tickNode.setStroke( MAJOR_TICK_STROKE );
                tickNode.setStrokePaint( TICK_COLOR );
                ticksNode.addChild( tickNode );

                // major tick label
                int labelIndex = ( i / MINOR_TICKS_PER_MAJOR_TICK ) - 1;
                if ( labelIndex < MAJOR_TICK_LABELS.length && MAJOR_TICK_LABELS[labelIndex] != null ) {
                    String label = MessageFormat.format( Strings.PATTERN_0VALUE_1UNITS, MAJOR_TICK_LABELS[labelIndex], Strings.UNITS_LITERS );
                    PText textNode = new PText( label );
                    textNode.setFont( TICK_LABEL_FONT );
                    textNode.setTextPaint( TICK_COLOR );
                    ticksNode.addChild( textNode );
                    double xOffset = ( ticksLocation == TicksLocation.LEFT ) ?
                                     ( tickNode.getFullBounds().getMaxX() + TICK_LABEL_X_SPACING ) :
                                     ( tickNode.getFullBounds().getMinX() - textNode.getFullBoundsReference().getWidth() - TICK_LABEL_X_SPACING );
                    double yOffset = tickNode.getFullBounds().getMinY() - ( textNode.getFullBoundsReference().getHeight() / 2 );
                    textNode.setOffset( xOffset, yOffset );
                }
            }
            else {
                // minor tick
                double x1 = ( ticksLocation == TicksLocation.LEFT ) ? leftX : rightX - MINOR_TICK_LENGTH;
                double x2 = ( ticksLocation == TicksLocation.LEFT ) ? leftX + MINOR_TICK_LENGTH : rightX;
                Shape tickPath = new Line2D.Double( x1, y, x2, y );
                PPath tickNode = new PPath( tickPath );
                tickNode.setStroke( MINOR_TICK_STROKE );
                tickNode.setStrokePaint( TICK_COLOR );
                ticksNode.addChild( tickNode );
            }
        }
    }
}