/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.Beaker;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of a beaker that is filled to the top with a solution.
 * Origin is at the bottom center.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeakerNode extends PComposite {
    
    private static final double MAX_VOLUME = 1; // L
    
    private static final Stroke STROKE = new BasicStroke( 6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    private static final Color STROKE_COLOR = Color.BLACK;
    
    private static final String[] MAJOR_TICK_LABELS = { null, "1" }; // nothing, 1L
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
    private final PPath outlineNode, solutionNode;
    private final GeneralPath outlinePath; 
    private final Rectangle2D solutionRectangle;
    private final PComposite ticksNode;
    
    public BeakerNode( ABSModel model ) {
        super();
        
        this.beaker = model.getBeaker();

        solutionRectangle = new Rectangle2D.Double();
        solutionNode = new PPath();
        solutionNode.setPaint( model.getSolution().getColor() );
        solutionNode.setStroke( null );
        addChild( solutionNode );
        
        outlinePath = new GeneralPath();
        outlineNode = new PPath();
        outlineNode.setPaint( null );
        outlineNode.setStroke( STROKE );
        outlineNode.setStrokePaint( STROKE_COLOR );
        addChild( outlineNode );
        
        ticksNode = new PComposite();
        addChild( ticksNode );
        
        setOffset( beaker.getLocationReference() );
        setVisible( beaker.isVisible() );
        update();
    }
    
    private void update() {
        updateBeaker();
        updateTicks();
        updateSolution();
    }
    
    /*
     * Where the beaker's origin is in relation to its width.
     */
    private double getOriginXOffset() {
        return beaker.getWidth() / 2; // origin a midpoint
    }
    
    /*
     * Where the beaker's origin is in relation to its height.
     */
    private double getOriginYOffset() {
        return beaker.getHeight(); // origin at bottom
    }
    
    /*
     * Creates the shape for the beaker.
     * Origin is at the bottom center.
     * Start drawing the shape at the upper-left corner.
     */
    private void updateBeaker() {
        outlinePath.reset();
        double xOffset = getOriginXOffset();
        double yOffset = getOriginYOffset();
        outlinePath.moveTo( (float) ( -xOffset - RIM_OFFSET ), (float) ( -yOffset - RIM_OFFSET ) );
        outlinePath.lineTo( (float) -xOffset, (float) -yOffset );
        outlinePath.lineTo( (float) -xOffset, (float)( beaker.getHeight() - yOffset ) );
        outlinePath.lineTo( (float) ( beaker.getWidth() - xOffset ), (float)( beaker.getHeight() - yOffset )  );
        outlinePath.lineTo( (float) ( beaker.getWidth() - xOffset ), (float) -yOffset );
        outlinePath.lineTo( (float) ( beaker.getWidth() - xOffset + RIM_OFFSET ), (float) ( -yOffset - RIM_OFFSET ) );
        outlineNode.setPathTo( outlinePath );
    }
    
    private void updateSolution() {
        double xOffset = getOriginXOffset();
        double yOffset = getOriginYOffset();
        solutionRectangle.setRect( -xOffset, -yOffset, beaker.getWidth(), beaker.getHeight() );
        solutionNode.setPathTo( solutionRectangle );
    }
    
    private void updateTicks() {
        ticksNode.removeAllChildren();
        int numberOfTicks = (int) Math.round( MAX_VOLUME / MINOR_TICK_SPACING );
        final double rightX = getOriginXOffset(); // don't use bounds or position will be off because of stroke width
        final double bottomY = beaker.getHeight() - getOriginYOffset(); // don't use bounds or position will be off because of stroke width
        double deltaY = beaker.getHeight() / numberOfTicks;
        for ( int i = 1; i <= numberOfTicks; i++ ) {
            final double y = bottomY - ( i * deltaY );
            if ( i % MINOR_TICKS_PER_MAJOR_TICK == 0 ) {
                // major tick
                Shape tickPath = new Line2D.Double( rightX - MAJOR_TICK_LENGTH, y, rightX - 2, y );
                PPath tickNode = new PPath( tickPath );
                tickNode.setStroke( MAJOR_TICK_STROKE );
                tickNode.setStrokePaint( TICK_COLOR );
                ticksNode.addChild( tickNode );
                
                int labelIndex = ( i / MINOR_TICKS_PER_MAJOR_TICK ) - 1;
                if ( labelIndex < MAJOR_TICK_LABELS.length && MAJOR_TICK_LABELS[ labelIndex ] != null ) {
                    String label = MessageFormat.format( ABSStrings.PATTERN_VALUE_UNITS, MAJOR_TICK_LABELS[ labelIndex ], ABSStrings.LITERS );
                    PText textNode = new PText( label );
                    textNode.setFont( TICK_LABEL_FONT );
                    textNode.setTextPaint( TICK_COLOR );
                    ticksNode.addChild( textNode );
                    double xOffset = tickNode.getFullBounds().getMinX() - textNode.getFullBoundsReference().getWidth() - TICK_LABEL_X_SPACING;
                    double yOffset = tickNode.getFullBounds().getMinY() - ( textNode.getFullBoundsReference().getHeight() / 2 );
                    textNode.setOffset( xOffset, yOffset );
                }
            }
            else {
                // minor tick
                Shape tickPath = new Line2D.Double( rightX - MINOR_TICK_LENGTH, y, rightX - 2, y );
                PPath tickNode = new PPath( tickPath );
                tickNode.setStroke( MINOR_TICK_STROKE );
                tickNode.setStrokePaint( TICK_COLOR );
                ticksNode.addChild( tickNode );
            }
        }
    }
}