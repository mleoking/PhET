package edu.colorado.phet.fitness.view;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Apr 17, 2008 at 6:24:50 PM
 */
public class StackedBarChartNode extends PNode {
    private PNode barLayer = new PNode();
    private int spacing;

    public StackedBarChartNode( int spacing ) {
        this.spacing = spacing;
        addChild( barLayer );
    }

    public void addStackedBarNode( StackedBarNode node ) {
        barLayer.addChild( node );
        updateLayout();
    }

    private void updateLayout() {
        double xOffset = 0;
        for ( int i = 0; i < barLayer.getChildrenCount(); i++ ) {
            StackedBarNode node = (StackedBarNode) barLayer.getChild( i );
            node.setOffset( xOffset, 0 );
            xOffset += node.getBarWidth() + spacing;
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test Frame" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );
        PhetPCanvas contentPane = new BufferedPhetPCanvas();
        frame.setContentPane( contentPane );

        StackedBarChartNode stackedBarChart = new StackedBarChartNode( 10 );
        StackedBarNode barNode = new StackedBarNode( 100 );
        barNode.addElement( new StackedBarNode.BarChartElement( "BMR", FitnessColorScheme.BMR, 100 ) );
        barNode.addElement( new StackedBarNode.BarChartElement( "Activity", FitnessColorScheme.ACTIVITY, 200 ) );
        barNode.addElement( new StackedBarNode.BarChartElement( "Exercise", FitnessColorScheme.EXERCISE, 50 ) );

        StackedBarNode barNode2 = new StackedBarNode( 100 );
        barNode2.addElement( new StackedBarNode.BarChartElement( "Lipids", FitnessColorScheme.FATS, 150 ) );
        barNode2.addElement( new StackedBarNode.BarChartElement( "Carbs", FitnessColorScheme.CARBS, 75 ) );
        barNode2.addElement( new StackedBarNode.BarChartElement( "Proteins", FitnessColorScheme.PROTEIN, 150 ) );

        stackedBarChart.addStackedBarNode( barNode );
        stackedBarChart.addStackedBarNode( barNode2 );

        contentPane.addScreenChild( stackedBarChart );
        stackedBarChart.setOffset( 100, 400 );

        frame.setVisible( true );
//        System.out.println( "stackedBarChart.getFullBounds() = " + stackedBarChart.getFullBounds() );
    }

}
