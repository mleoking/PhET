/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.*;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Draws a simple population bar graph with labels
 *
 * @author Jonathan Olson
 */
public class PopulationGraphNode extends PNode {

    // piccolo nodes used for display
    private PPath bar;
    private PPath axis;
    private PText label;
    private PText quantity;

    // current displayed population
    private int population;

    // vertical distance from the origin to the base of the bar chart
    private static float BASELINE = 170;

    // x offset to the bar's left side
    private static float BAR_LEFT = 25;

    // x offset to the bar's right side
    private static float BAR_RIGHT = 55;

    // same with the axis
    private static float AXIS_LEFT = 15;
    private static float AXIS_RIGHT = 65;

    // total available width
    private static float TOTAL_WIDTH = 80;

    // stroke widths
    private static float AXIS_STROKE_WIDTH = 3f;
    private static float BAR_STROKE_WIDTH = 1f;

    // pixels per 1 bunny, can be fractional
    private static float POPULATION_MULTIPLIER = 1f;

    // the maximum top of the bar
    private static float GRAPH_TOP = 20;

    /**
     * Constructor
     *
     * @param startPopulation The initial population
     */
    public PopulationGraphNode( int startPopulation ) {
        axis = new PPath();
        bar = new PPath();
        label = new PText( "# Rabbits" );
        quantity = new PText();

        updatePopulation( startPopulation );

    }

    public void reset() {
        updatePopulation( NaturalSelectionDefaults.DEFAULT_NUMBER_OF_BUNNIES );
    }

    private float getGraphHeight() {
        return ( (float) population ) * POPULATION_MULTIPLIER;
    }

    /**
     * Cap the graph height so it won't display over other componenets.
     *
     * @return The (possibly) capped height
     */
    private float getTopPosition() {
        float top = BASELINE - getGraphHeight();
        if ( top < GRAPH_TOP ) {
            top = GRAPH_TOP;
        }
        return top;
    }

    /**
     * Refresh the display with the new population
     *
     * @param newPopulation New population
     */
    public void updatePopulation( int newPopulation ) {
        population = newPopulation;

        quantity.setText( String.valueOf( population ) );

        drawBar();
        drawAxis();
        drawLabels();
    }

    /**
     * Draws the axis
     */
    private void drawAxis() {
        axis.setStroke( new BasicStroke( AXIS_STROKE_WIDTH ) );
        axis.setStrokePaint( Color.BLACK );

        GeneralPath path = new GeneralPath();
        path.moveTo( AXIS_LEFT, BASELINE );
        path.lineTo( AXIS_RIGHT, BASELINE );
        axis.setPathTo( path );

        conditionalAdd( axis );
    }

    /**
     * Draws the labels (pop above bar)
     */
    private void drawLabels() {
        label.setFont( new PhetFont() );
        label.setOffset( ( TOTAL_WIDTH - label.getWidth() ) / 2, BASELINE + 10 );
        conditionalAdd( label );

        quantity.setFont( new PhetFont() );
        quantity.setOffset( ( TOTAL_WIDTH - quantity.getWidth() ) / 2, getTopPosition() - GRAPH_TOP );
        conditionalAdd( quantity );
    }

    /**
     * Draws the bar of the bar chart
     */
    private void drawBar() {
        bar.setStroke( new BasicStroke( BAR_STROKE_WIDTH ) );
        bar.setStrokePaint( Color.BLACK );
        bar.setPaint( Color.WHITE );

        float top = getTopPosition();

        GeneralPath path = new GeneralPath();
        path.moveTo( BAR_LEFT, top );
        path.lineTo( BAR_LEFT, BASELINE );
        path.lineTo( BAR_RIGHT, BASELINE );
        path.lineTo( BAR_RIGHT, top );

        // if capped, use a dashed line instead
        if ( top == GRAPH_TOP ) {
            bar.setStroke( new BasicStroke( BAR_STROKE_WIDTH, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10.0f, new float[]{2.0f, 2.0f}, 0.0f ) );
        }

        path.closePath();
        bar.setPathTo( path );

        conditionalAdd( bar );
    }

    /**
     * Helper function that only adds the node if it is not already a child
     *
     * @param node The node to possibly add
     */
    private void conditionalAdd( PNode node ) {
        if ( node.getParent() == null ) {
            addChild( node );
        }
    }
}
