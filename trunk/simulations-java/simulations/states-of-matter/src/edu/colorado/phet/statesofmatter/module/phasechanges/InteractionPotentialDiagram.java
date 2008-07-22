/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.phasechanges;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D.Double;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

// TODO: JPB TBD - Make the labels into translatable strings.

/**
 * This class displays a phase diagram suitable for inclusion on the control
 * panel of a PhET simulation.
 *
 * @author John Blanco
 */
public class InteractionPotentialDiagram extends PhetPCanvas {

    // Constants that control the size of the canvas.
    private static final int WIDTH = 200;
    private static final int HEIGHT = (int)((double)WIDTH * 0.8);
    
    // Constants that control the look of the axes, lines, and arrows.
    private static final float AXIS_LINE_WIDTH = 1;
    private static final Stroke AXIS_LINE_STROKE = new BasicStroke(AXIS_LINE_WIDTH);
    private static final Color AXIS_LINE_COLOR = Color.LIGHT_GRAY;
    private static final double ARROW_LINE_WIDTH = 0.50;
    private static final double ARROW_HEAD_WIDTH = 8 * ARROW_LINE_WIDTH;
    private static final double ARROW_HEAD_HEIGHT = 10 * ARROW_LINE_WIDTH;
    private static final float POTENTIAL_ENERGY_LINE_WIDTH = 1.5f;
    private static final Stroke POTENTIAL_ENERGY_LINE_STROKE = new BasicStroke(POTENTIAL_ENERGY_LINE_WIDTH);
    private static final Color POTENTIAL_ENERGY_LINE_COLOR = Color.red;
    private static final int NUM_HORIZ_TICK_MARKS = 4;
    private static final int NUM_VERT_TICK_MARKS = 3;
    private static final double TICK_MARK_LENGTH = 2;
    private static final float TICK_MARK_WIDTH = 1;
    private static final Stroke TICK_MARK_STROKE = new BasicStroke(TICK_MARK_WIDTH);
    
    // Constants used for the Lennard-Jones potential calculation.
    private static final double SIGMA = 3.3; 
    private static final double EPSILON = 120;
    private static final double MAX_POTENTIAL = 1E4;
    private static final double HORIZONTAL_INDEX_MULTIPLIER = 0.05;  // Empirically determined so curve will look reasonable.
    private static final double VERTICAL_SCALING_FACTOR = 0.5;       // Empirically determined so curve will fit graph.
    
    // Constants that control the location and size of the graph.
    private static final double HORIZ_AXIS_SIZE_PROPORTION = 0.80;
    private static final double VERT_AXIS_SIZE_PROPORTION = 0.80;
    private static final double GRAPH_OFFSET_X = 0.10 * (double)WIDTH;
    private static final double GRAPH_OFFSET_Y = 0;
    private static final double GRAPH_WIDTH = WIDTH * HORIZ_AXIS_SIZE_PROPORTION;
    private static final double GRAPH_HEIGHT = HEIGHT * VERT_AXIS_SIZE_PROPORTION;
    
    // Font for the labels used on the axes and within the graph.
    private static final int AXIS_LABEL_FONT_SIZE = 14;
    private static final Font AXIS_LABEL_FONT = new PhetFont(AXIS_LABEL_FONT_SIZE);
    private static final int GREEK_LETTER_FONT_SIZE = 16;
    private static final Font GREEK_LETTER_FONT = new PhetFont(GREEK_LETTER_FONT_SIZE);
    
    /**
     * Constructor.
     */
    public InteractionPotentialDiagram(){

        setPreferredSize( new Dimension(WIDTH, HEIGHT) );
        setBackground( StatesOfMatterConstants.CONTROL_PANEL_COLOR );
        setBorder( null );

        // Create and add the node that will contain the graph.
        PPath ljPotentialGraph = new PPath(new Rectangle2D.Double(0, 0, GRAPH_WIDTH, GRAPH_HEIGHT));
        ljPotentialGraph.setOffset( GRAPH_OFFSET_X, 0 );
        ljPotentialGraph.setPaint( Color.WHITE );
        addWorldChild( ljPotentialGraph );
        
        // Create and add the axis line for the graph.
        PPath horizontalAxis = new PPath(new Line2D.Double(new Point2D.Double(0, 0), 
                new Point2D.Double(GRAPH_WIDTH, 0)));
        horizontalAxis.setStroke( AXIS_LINE_STROKE );
        horizontalAxis.setStrokePaint( AXIS_LINE_COLOR );
        ljPotentialGraph.addChild( horizontalAxis );
        horizontalAxis.setOffset( 0, GRAPH_HEIGHT / 2 );
        
        // Create and add the tick marks for the graph.
        double horizTickMarkSpacing = GRAPH_WIDTH / (NUM_HORIZ_TICK_MARKS + 1);
        Line2D tickMarkShape = new Line2D.Double();
        Point2D endpoint1 = new Point2D.Double();
        Point2D endpoint2 = new Point2D.Double();
        for (int i = 0; i < NUM_HORIZ_TICK_MARKS; i++){
            // Top tick mark
            endpoint1.setLocation( horizTickMarkSpacing * (i + 1), 0 );
            endpoint2.setLocation( horizTickMarkSpacing * (i + 1), TICK_MARK_LENGTH );
            tickMarkShape.setLine( endpoint1, endpoint2 );
            PPath topTickMark = new PPath(tickMarkShape);
            topTickMark.setStroke( TICK_MARK_STROKE );
            ljPotentialGraph.addChild( topTickMark );

            // Bottom tick mark
            endpoint1.setLocation( horizTickMarkSpacing * (i + 1), GRAPH_HEIGHT );
            endpoint2.setLocation( horizTickMarkSpacing * (i + 1), GRAPH_HEIGHT - TICK_MARK_LENGTH );
            tickMarkShape.setLine( endpoint1, endpoint2 );
            PPath bottomTickMark = new PPath(tickMarkShape);
            bottomTickMark.setStroke( TICK_MARK_STROKE );
            ljPotentialGraph.addChild( bottomTickMark );
        }
        double vertTickMarkSpacing = GRAPH_HEIGHT / (NUM_VERT_TICK_MARKS + 1);
        for (int i = 0; i < NUM_VERT_TICK_MARKS; i++){
            // Left tick mark
            endpoint1.setLocation( 0, vertTickMarkSpacing * (i + 1) );
            endpoint2.setLocation( TICK_MARK_LENGTH, vertTickMarkSpacing * (i + 1) );
            tickMarkShape.setLine( endpoint1, endpoint2 );
            PPath leftTickMark = new PPath(tickMarkShape);
            leftTickMark.setStroke( TICK_MARK_STROKE );
            ljPotentialGraph.addChild( leftTickMark );

            // Right tick mark
            endpoint1.setLocation( GRAPH_WIDTH, vertTickMarkSpacing * (i + 1) );
            endpoint2.setLocation( GRAPH_WIDTH - TICK_MARK_LENGTH, vertTickMarkSpacing * (i + 1) );
            tickMarkShape.setLine( endpoint1, endpoint2 );
            PPath rightTickMark = new PPath(tickMarkShape);
            rightTickMark.setStroke( TICK_MARK_STROKE );
            ljPotentialGraph.addChild( rightTickMark );
        }
        
        // Create and add the potential energy line to the graph.
        PPath potentialEnergyLine = new PPath();
        potentialEnergyLine.setStroke( POTENTIAL_ENERGY_LINE_STROKE );
        potentialEnergyLine.setStrokePaint( POTENTIAL_ENERGY_LINE_COLOR );
        GeneralPath potentialEnergyLineShape = new GeneralPath();
        potentialEnergyLineShape.moveTo( 0, (float)(GRAPH_OFFSET_Y - (2 * GRAPH_HEIGHT) ));
        Point2D graphMin = new Point2D.Double(0, 0);
        Point2D zeroCrossingPoint = new Point2D.Double(0, 0);
        for (int i = 1; i < (int)GRAPH_WIDTH; i++){
            double potential = calculateLennardJonesPotential( i * HORIZONTAL_INDEX_MULTIPLIER);
            if ((potential < MAX_POTENTIAL) && (potential > -MAX_POTENTIAL)){
                double yPos = (GRAPH_HEIGHT / 2) - (potential * VERTICAL_SCALING_FACTOR);
                potentialEnergyLineShape.lineTo( (float)i, (float)yPos);
                if (yPos > graphMin.getY()){
                    // A new minimum has been found.  If you're wondering why
                    // the test is for greater than rather than less than, it
                    // is because the Y direction is down rather than up
                    // within a PNode.
                    graphMin.setLocation( i, yPos );
                }
                if (potential > 0){
                    // The potential hasn't become negative yet, so update the
                    // zero crossing point.
                    zeroCrossingPoint.setLocation( i, GRAPH_HEIGHT / 2 );
                }
            }
        }
        potentialEnergyLine.setPathTo( potentialEnergyLineShape );
        
        // Put in the arrows that depict sigma and epsilon.
        DoubleArrowNode epsilonArrow = new DoubleArrowNode(graphMin, 
                new Point2D.Double( graphMin.getX(), GRAPH_HEIGHT / 2 ), ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_LINE_WIDTH);
        epsilonArrow.setPaint( Color.BLACK );
        ljPotentialGraph.addChild( epsilonArrow );
        
        PText epsilon = new PText("\u03B5");
        epsilon.setFont( GREEK_LETTER_FONT );
        epsilon.setOffset( graphMin.getX() + epsilon.getFullBoundsReference().width * 0.5, 
                GRAPH_HEIGHT / 2 + epsilon.getFullBoundsReference().height * 0.5 );
        ljPotentialGraph.addChild( epsilon );

        PText sigma = new PText("\u03C3");
        sigma.setFont( GREEK_LETTER_FONT );
        sigma.setOffset( zeroCrossingPoint.getX() / 2 - sigma.getFullBoundsReference().width / 2, 
                GRAPH_HEIGHT / 2 );
        ljPotentialGraph.addChild( sigma );

        DoubleArrowNode sigmaArrow = new DoubleArrowNode(new Point2D.Double(0, GRAPH_HEIGHT / 2), zeroCrossingPoint, 
                ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_LINE_WIDTH);
        sigmaArrow.setPaint( Color.BLACK );
        ljPotentialGraph.addChild( sigmaArrow );

        // Add the potential energy line here so that it is above the arrows
        // in the layering.
        ljPotentialGraph.addChild( potentialEnergyLine );

        // Create and add the labels for the axes.
        // TODO: JPB TBD - Make these into translatable strings if kept.
        PText horizontalAxisLabel = new PText("Distance Between Molecules");
        horizontalAxisLabel.setOffset( GRAPH_OFFSET_X + (GRAPH_WIDTH / 2) - 
                (horizontalAxisLabel.getFullBoundsReference().width / 2), 
                GRAPH_OFFSET_Y + GRAPH_HEIGHT + (horizontalAxisLabel.getFullBoundsReference().height * 0.3));
        addWorldChild( horizontalAxisLabel );
        
        PText verticalAxisLabel = new PText("Potential Energy");
        verticalAxisLabel.setOffset( 0, 
                (GRAPH_OFFSET_Y + GRAPH_HEIGHT) / 2 + (verticalAxisLabel.getFullBoundsReference().width / 2) );
        verticalAxisLabel.rotate( 3 * Math.PI / 2 );
        addWorldChild( verticalAxisLabel );
    }
    
    /**
     * Calculate the normalized Lennard-Jones potential, meaning that the 
     * sigma and epsilon values are assumed to be equal to 1.
     * 
     * @param radius
     * @return
     */
    private double calculateLennardJonesPotential(double radius){
        
        return (4 * EPSILON * (Math.pow( SIGMA / radius, 12 ) - Math.pow( SIGMA / radius, 6 )));
        
    }
}
