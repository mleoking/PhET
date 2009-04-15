package edu.colorado.phet.naturalselection.view;

import java.awt.*;
import java.awt.geom.GeneralPath;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

public class PopulationGraphNode extends PNode {

    public PPath bar;
    public PPath axis;
    public PText label;
    public PText quantity;

    private int population;

    private static float BASELINE = 170;
    private static float BAR_LEFT = 25;
    private static float BAR_RIGHT = 55;
    private static float AXIS_LEFT = 15;
    private static float AXIS_RIGHT = 65;

    private static float TOTAL_WIDTH = 80;

    private static float AXIS_STROKE_WIDTH = 3f;
    private static float BAR_STROKE_WIDTH = 1f;

    private static float POPULATION_MULTIPLIER = 1f;

    public PopulationGraphNode( int startPopulation ) {
        axis = new PPath();
        bar = new PPath();
        label = new PText( "# Rabbits" );
        quantity = new PText();

        updatePopulation( startPopulation );

    }

    private float getGraphHeight() {
        return ( (float) population ) * POPULATION_MULTIPLIER;
    }

    private void updatePopulation( int newPopulation ) {
        population = newPopulation;

        quantity.setText( String.valueOf( population ) );

        drawBar();
        drawAxis();
        drawLabels();
    }

    private void drawAxis() {
        axis.setStroke( new BasicStroke( AXIS_STROKE_WIDTH ) );
        axis.setStrokePaint( Color.BLACK );

        GeneralPath path = new GeneralPath();
        path.moveTo( AXIS_LEFT, BASELINE );
        path.lineTo( AXIS_RIGHT, BASELINE );
        axis.setPathTo( path );

        conditionalAdd( axis );
    }

    private void drawLabels() {
        label.setFont( new PhetFont() );
        label.setOffset( ( TOTAL_WIDTH - label.getWidth() ) / 2, BASELINE + 10 );
        conditionalAdd( label );

        quantity.setFont( new PhetFont() );
        quantity.setOffset( ( TOTAL_WIDTH - quantity.getWidth() ) / 2, BASELINE - getGraphHeight() - 20 );
        conditionalAdd( quantity );
    }

    private void drawBar() {
        bar.setStroke( new BasicStroke( BAR_STROKE_WIDTH ) );
        bar.setStrokePaint( Color.BLACK );
        bar.setPaint( Color.WHITE );

        float height = getGraphHeight();

        GeneralPath path = new GeneralPath();
        path.moveTo( BAR_LEFT, BASELINE - height );
        path.lineTo( BAR_LEFT, BASELINE );
        path.lineTo( BAR_RIGHT, BASELINE );
        path.lineTo( BAR_RIGHT, BASELINE - height );
        path.closePath();
        bar.setPathTo( path );

        conditionalAdd( bar );
    }

    private void conditionalAdd( PNode node ) {
        if ( node.getParent() == null ) {
            addChild( node );
        }
    }
}
