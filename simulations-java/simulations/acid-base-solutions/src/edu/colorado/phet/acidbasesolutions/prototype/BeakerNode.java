/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of a beaker that is filled to the top with a solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class BeakerNode extends PComposite {
    
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
    
    private final Beaker beaker;
    private final WeakAcid solution;
    private final PPath outlineNode, solutionNode;
    private final GeneralPath outlinePath; 
    private final Rectangle2D solutionRectangle;
    private final PComposite ticksNode;
    
    public BeakerNode( Beaker beaker, WeakAcid solution, MagnifyingGlassNode magnifyingGlassNode, boolean dev ) {
        super();
        
        this.beaker = beaker;
        beaker.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        });
        
        this.solution = solution;
        solution.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        } );
        
        solutionRectangle = new Rectangle2D.Double();
        solutionNode = new PPath();
        solutionNode.setPaint( solution.getColor() );
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
        
        update();
    }
    
    private void update() {
        updateBeaker();
        updateTicks();
        updateSolution();
    }
    
    private void updateBeaker() {
        double width = beaker.getWidth();
        double height = beaker.getHeight();
        double rimOffset = 20;
        outlinePath.reset();
        outlinePath.moveTo( (float) ( -width/2 - rimOffset ), (float) ( -height/2 - rimOffset ) ); // origin at center
        outlinePath.lineTo( (float) -width/2, (float) -height/2 );
        outlinePath.lineTo( (float) -width/2, (float) +height/2 );
        outlinePath.lineTo( (float) +width/2, (float) +height/2 );
        outlinePath.lineTo( (float) +width/2, (float) -height/2 );
        outlinePath.lineTo( (float) ( +width/2 + rimOffset ), (float) ( -height/2 - rimOffset ) );
        outlineNode.setPathTo( outlinePath );
    }
    
    private void updateSolution() {
        solutionNode.setPaint( solution.getColor() );
        double width = beaker.getWidth();
        double height = beaker.getHeight();
        solutionRectangle.setRect( -width/2, -height/2, width, height );
        solutionNode.setPathTo( solutionRectangle );
    }
    
    private void updateTicks() {
        ticksNode.removeAllChildren();
        int numberOfTicks = (int) Math.round( MAX_VOLUME / MINOR_TICK_SPACING );
        final double rightX = beaker.getWidth() / 2; // don't use bounds or position will be off because of stroke width
        final double bottomY = beaker.getHeight() / 2; // don't use bounds or position will be off because of stroke width
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
                    String label = MAJOR_TICK_LABELS[ labelIndex ] + MGPConstants.UNITS_LITERS;
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