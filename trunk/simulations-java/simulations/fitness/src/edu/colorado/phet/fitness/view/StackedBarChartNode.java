package edu.colorado.phet.fitness.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.fitness.FitnessStrings;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Apr 17, 2008 at 6:24:50 PM
 */
public class StackedBarChartNode extends PNode {
    private PNode barLayer = new PNode();
    private Function function;
    private int spacing;
    private StackedBarChartAxisNode axisNode;
    private GradientButtonNode zoomOut;

    public StackedBarChartNode( Function function, String title, int horizontalInset, double minorTickSpacing, double majorTickSpacing, double maxYValue ) {
        this.function = function;
        this.spacing = horizontalInset;
        addChild( barLayer );

        axisNode = new StackedBarChartAxisNode( title, function, minorTickSpacing, majorTickSpacing, maxYValue );
        addChild( axisNode );

        zoomOut = new GradientButtonNode( "-", 14, Color.green );
        zoomOut.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
            }
        } );
        addChild( zoomOut );

        updateLayout();
    }

    public void addStackedBarNode( StackedBarNode node ) {
        barLayer.addChild( node );
        updateLayout();
    }

    //todo: convert to layout strategy pattern
    private void updateLayout() {
        if ( barLayer.getChildrenCount() >= 1 ) {
            StackedBarNode node = (StackedBarNode) barLayer.getChild( 0 );
            node.setOffset( 0, 0 );
            double dx = node.getFullBounds().getMaxX() - axisNode.getFullBounds().getX();
            axisNode.offset( dx + 2, 0 );
            zoomOut.setOffset( axisNode.getFullBounds().getCenterX() + zoomOut.getFullBounds().getWidth() / 2, axisNode.getFullBounds().getMaxY() - zoomOut.getFullBounds().getHeight() );

            double xOffset = axisNode.getFullBounds().getMaxX() + 2;
            for ( int i = 1; i < barLayer.getChildrenCount(); i++ ) {
                StackedBarNode ch = (StackedBarNode) barLayer.getChild( i );
                ch.setOffset( xOffset, 0 );
                xOffset += ch.getBarWidth() + spacing;
            }
        }

    }

    private void updateLayoutDefaultStrategy() {
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

        StackedBarChartNode stackedBarChart = new StackedBarChartNode( new Function.IdentityFunction(), "Calories/Day", 10, 10, 100, 300 );
        StackedBarNode barNode = new StackedBarNode( 100 );
        barNode.addElement( new StackedBarNode.BarChartElement( "BMR", FitnessColorScheme.BMR, 100 ) );
        barNode.addElement( new StackedBarNode.BarChartElement( "Activity", FitnessColorScheme.ACTIVITY, 200 ) );
        barNode.addElement( new StackedBarNode.BarChartElement( "Exercise", FitnessColorScheme.EXERCISE, 50 ) );

        StackedBarNode barNode2 = new StackedBarNode( 100 );
        barNode2.addElement( new StackedBarNode.BarChartElement( FitnessStrings.FATS, FitnessColorScheme.FATS, 150 ) );
        barNode2.addElement( new StackedBarNode.BarChartElement( "Carbs", FitnessColorScheme.CARBS, 75 ) );
        barNode2.addElement( new StackedBarNode.BarChartElement( "Proteins", FitnessColorScheme.PROTEIN, 150 ) );

        stackedBarChart.addStackedBarNode( barNode2 );
        stackedBarChart.addStackedBarNode( barNode );

        contentPane.addScreenChild( stackedBarChart );
        stackedBarChart.setOffset( 100, 400 );

        frame.setVisible( true );
//        System.out.println( "stackedBarChart.getFullBounds() = " + stackedBarChart.getFullBounds() );
    }

}
