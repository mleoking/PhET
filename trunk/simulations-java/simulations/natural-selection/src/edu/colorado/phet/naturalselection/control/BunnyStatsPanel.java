package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.ColorGene;
import edu.colorado.phet.naturalselection.model.TailGene;
import edu.colorado.phet.naturalselection.model.TeethGene;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;

public class BunnyStatsPanel extends JPanel {

    private DefaultCategoryDataset data;
    private int col = 0;
    private int delayCount = 0;
    private static final int MAX_DELAY_COUNT = 5;

    public BunnyStatsPanel( NaturalSelectionModel model ) {

        super( new GridLayout( 1, 1 ) );

        setMaximumSize( NaturalSelectionDefaults.BUNNY_STATS_SIZE );

        createData();

        // create the chart...
        final JFreeChart chart = ChartFactory.createLineChart(
                "Bunny Population",        // chart title
                "Time",               // domain axis label
                "Population",                  // range axis label
                data,                 // data
                PlotOrientation.VERTICAL,
                true,                     // include legend
                true,                     // tooltips?
                false                     // URL generator?  Not required...
        );


        final CategoryPlot plot = chart.getCategoryPlot();

        NumberAxis axis = new NumberAxis();
        axis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        plot.setRangeAxis( axis );


        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setVisible( false );
        /*
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions( Math.PI / 4.0 )
        );
        */
        chart.setBackgroundPaint( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        //setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );


        // add the chart to a panel...
        final ChartPanel chartPanel = new ChartPanel( chart );
        chartPanel.setPreferredSize( NaturalSelectionDefaults.BUNNY_STATS_SIZE );

        add( chartPanel );

        model.addListener( new NaturalSelectionModel.Listener() {
            public void onEvent( NaturalSelectionModel.Event event ) {
                if ( event.getType() == NaturalSelectionModel.Event.TYPE_NEW_GENERATION ) {
                    //newData();
                }
            }
        } );

        model.getClock().addClockListener( new ClockListener() {
            public void clockTicked( ClockEvent clockEvent ) {

            }

            public void clockStarted( ClockEvent clockEvent ) {

            }

            public void clockPaused( ClockEvent clockEvent ) {

            }

            public void simulationTimeChanged( ClockEvent clockEvent ) {
                if ( ++delayCount % MAX_DELAY_COUNT == 0 ) {
                    newData();
                }
            }

            public void simulationTimeReset( ClockEvent clockEvent ) {

            }
        } );
    }

    private void newData() {
        final String colorPrimary = ColorGene.getInstance().getPrimaryAllele().getName();
        final String colorSecondary = ColorGene.getInstance().getSecondaryAllele().getName();
        final String tailPrimary = TailGene.getInstance().getPrimaryAllele().getName();
        final String tailSecondary = TailGene.getInstance().getSecondaryAllele().getName();
        final String teethPrimary = TeethGene.getInstance().getPrimaryAllele().getName();
        final String teethSecondary = TeethGene.getInstance().getSecondaryAllele().getName();

        data.addValue( ColorGene.getInstance().getPrimaryPhenotypeCount(), colorPrimary, String.valueOf( col ) );
        data.addValue( ColorGene.getInstance().getSecondaryPhenotypeCount(), colorSecondary, String.valueOf( col ) );

        data.addValue( TailGene.getInstance().getPrimaryPhenotypeCount(), tailPrimary, String.valueOf( col ) );
        data.addValue( TailGene.getInstance().getSecondaryPhenotypeCount(), tailSecondary, String.valueOf( col ) );

        data.addValue( TeethGene.getInstance().getPrimaryPhenotypeCount(), teethPrimary, String.valueOf( col ) );
        data.addValue( TeethGene.getInstance().getSecondaryPhenotypeCount(), teethSecondary, String.valueOf( col ) );

        final int MAX_COL = 100;

        if ( col >= MAX_COL ) {
            data.removeColumn( String.valueOf( col - MAX_COL ) );
        }

        col++;
    }

    private void createData() {
        // create the dataset...
        data = new DefaultCategoryDataset();

        newData();
    }

    public void reset() {
        col = 0;
        data.clear();
    }
}