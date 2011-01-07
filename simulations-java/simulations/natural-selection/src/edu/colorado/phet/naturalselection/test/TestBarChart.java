// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.test;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a dual axis chart based on data
 * from two {@link CategoryDataset} instances.
 */
public class TestBarChart extends ApplicationFrame {

    /**
     * Creates a new demo instance.
     *
     * @param title the frame title.
     */
    public TestBarChart( final String title ) {

        super( title );

        final CategoryDataset dataset1 = createData();

        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart(
                "Bunny Statistics",        // chart title
                "Trait",               // domain axis label
                "Population",                  // range axis label
                dataset1,                 // data
                PlotOrientation.VERTICAL,
                true,                     // include legend
                true,                     // tooltips?
                false                     // URL generator?  Not required...
        );

        final CategoryPlot plot = chart.getCategoryPlot();

        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions( Math.PI / 4.0 )
        );

        // add the chart to a panel...
        final ChartPanel chartPanel = new ChartPanel( chart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 500, 270 ) );
        setContentPane( chartPanel );

    }

    /**
     * Creates a sample dataset.
     *
     * @return The dataset.
     */
    private CategoryDataset createData() {

        // row keys...
        final String furKey = "Fur";
        final String tailKey = "Tail";
        final String teethKey = "Teeth";
        final String allKey = "All";

        // column keys...
        final String whiteFur = "White Fur";
        final String brownFur = "Brown Fur";
        final String regularTail = "Regular Tail";
        final String fuzzyTail = "Fuzzy Tail";
        final String regularTeeth = "Regular Teeth";
        final String longTeeth = "Long Teeth";

        // create the dataset...
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue( 32.0, furKey, whiteFur );
        dataset.addValue( 8.0, furKey, brownFur );

        dataset.addValue( 40.0, tailKey, regularTail );
        dataset.addValue( 0.0, tailKey, fuzzyTail );

        dataset.addValue( 16.0, teethKey, regularTeeth );
        dataset.addValue( 24.0, teethKey, longTeeth );

        return dataset;

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args ignored.
     */
    public static void main( final String[] args ) {

        final TestBarChart demo = new TestBarChart( "Bunny Statistics" );
        demo.pack();
        RefineryUtilities.centerFrameOnScreen( demo );
        demo.setVisible( true );

    }

}
