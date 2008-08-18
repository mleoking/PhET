package edu.colorado.phet.eatingandexercise.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseStrings;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Apr 17, 2008 at 6:24:50 PM
 */
public class StackedBarChartNode extends PNode {
    private PNode barLayer = new PNode();
    private Function function;
    private String title;
    private int spacing;
    private double minorTickSpacing;
    private double majorTickSpacing;
    private double maxYValue;
    private StackedBarChartAxisNode axisNode;
    private GradientButtonNode zoomOut;
    private GradientButtonNode zoomIn;

    public StackedBarChartNode( Function function, String title, int horizontalInset, double minorTickSpacing, double majorTickSpacing, double maxYValue ) {
        this.function = function;
        this.title = title;
        this.spacing = horizontalInset;
        this.minorTickSpacing = minorTickSpacing;
        this.majorTickSpacing = majorTickSpacing;
        this.maxYValue = maxYValue;
        addChild( barLayer );

        axisNode = new StackedBarChartAxisNode( title, function, minorTickSpacing, majorTickSpacing, maxYValue );
        addChild( axisNode );

        zoomOut = new GradientButtonNode( "-", 14, Color.green );
        zoomOut.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( StackedBarChartNode.this.function instanceof Function.LinearFunction ) {
                    Function.LinearFunction linearFunction = (Function.LinearFunction) StackedBarChartNode.this.function;
                    setFunction( new Function.LinearFunction( linearFunction.getMinInput(), linearFunction.getMaxInput(),
                                                              linearFunction.getMinOutput(), linearFunction.getMaxOutput() / 2 ) );
                }
            }
        } );


        zoomIn = new GradientButtonNode( "+", 14, Color.green );
        zoomIn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( StackedBarChartNode.this.function instanceof Function.LinearFunction ) {
                    Function.LinearFunction linearFunction = (Function.LinearFunction) StackedBarChartNode.this.function;
                    setFunction( new Function.LinearFunction( linearFunction.getMinInput(), linearFunction.getMaxInput(),
                                                              linearFunction.getMinOutput(), linearFunction.getMaxOutput() * 2 ) );
                }
            }
        } );

        addChild( zoomOut );
        addChild( zoomIn );

        updateLayout();
    }

    public void setFunction( Function function ) {
        this.function = function;
        removeChild( axisNode );

        //todo: convert the following two lines to use axisNode.setFunction instead
        axisNode = new StackedBarChartAxisNode( title, function, minorTickSpacing, majorTickSpacing, maxYValue );
        addChild( indexOfChild( barLayer ) + 1, axisNode );

        for ( int i = 0; i < barLayer.getChildrenCount(); i++ ) {
            PNode child = barLayer.getChild( i );
            if ( child instanceof StackedBarNode ) {
                ( (StackedBarNode) child ).setFunction( function );
            }
        }

        updateLayout();
    }

    public void addStackedBarNode( final StackedBarNode node ) {
        barLayer.addChild( node );
        node.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateZoomVisibility();
            }
        } );
        updateLayout();
    }

    private void updateZoomVisibility() {
        boolean visible = false;
        for ( int i = 0; i < barLayer.getChildrenCount(); i++ ) {
            PNode node = barLayer.getChild( i );
            if ( node instanceof StackedBarNode ) {
                StackedBarNode stackedBarNode = (StackedBarNode) node;
                visible = visible || stackedBarNode.getTotal() >= 4000;
            }
        }
        zoomOut.setVisible( visible );
        zoomIn.setVisible( visible );
    }

    //todo: convert to layout strategy pattern
    private void updateLayout() {
        if ( barLayer.getChildrenCount() >= 1 ) {
            StackedBarNode node = (StackedBarNode) barLayer.getChild( 0 );
            node.setOffset( 0, 0 );
            double dx = node.getFullBounds().getMaxX() - axisNode.getFullBounds().getX();
            axisNode.offset( dx + 2, 0 );
            zoomOut.setOffset( axisNode.getFullBounds().getCenterX() + zoomOut.getFullBounds().getWidth() / 2, axisNode.getFullBounds().getMaxY() - zoomOut.getFullBounds().getHeight() * 2 );
            zoomIn.setOffset( zoomOut.getFullBounds().getX(), zoomOut.getFullBounds().getMaxY() );

            double xOffset = axisNode.getFullBounds().getMaxX() + 2;
            for ( int i = 1; i < barLayer.getChildrenCount(); i++ ) {
                StackedBarNode ch = (StackedBarNode) barLayer.getChild( i );
                ch.setOffset( xOffset, 0 );
                xOffset += ch.getBarWidth() + spacing;
            }
        }
        updateZoomVisibility();
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
        barNode.addElement( new BarChartElement( "BMR", EatingAndExerciseColorScheme.BMR, 100, new BufferedImage( 50, 50, BufferedImage.TYPE_INT_RGB ) ) );
        barNode.addElement( new BarChartElement( "Activity", EatingAndExerciseColorScheme.ACTIVITY, 200 ) );
        barNode.addElement( new BarChartElement( "Exercise", EatingAndExerciseColorScheme.EXERCISE, 50 ) );

        StackedBarNode barNode2 = new StackedBarNode( 100 );
        barNode2.addElement( new BarChartElement( EatingAndExerciseStrings.FATS, EatingAndExerciseColorScheme.FATS, 150 ,new BufferedImage( 50, 50, BufferedImage.TYPE_INT_RGB )), StackedBarNode.LEFT );
        barNode2.addElement( new BarChartElement( "Carbs", EatingAndExerciseColorScheme.CARBS, 75 ), StackedBarNode.RIGHT );
        barNode2.addElement( new BarChartElement( "Proteins", EatingAndExerciseColorScheme.PROTEIN, 150 ), StackedBarNode.LEFT );

        stackedBarChart.addStackedBarNode( barNode2 );
        stackedBarChart.addStackedBarNode( barNode );

        contentPane.addScreenChild( stackedBarChart );
        stackedBarChart.setOffset( 100, 400 );

        frame.setVisible( true );
//        System.out.println( "stackedBarChart.getFullBounds() = " + stackedBarChart.getFullBounds() );
    }

}
