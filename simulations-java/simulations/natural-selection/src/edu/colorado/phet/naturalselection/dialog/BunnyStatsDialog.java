package edu.colorado.phet.naturalselection.dialog;

import java.awt.*;

import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.*;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;

public class BunnyStatsDialog extends JDialog {

    private DefaultCategoryDataset data;

    public BunnyStatsDialog( Frame frame, NaturalSelectionModel model ) {

        super( frame );

        data = createData();

        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart(
                "Bunny Population",        // chart title
                "Trait",               // domain axis label
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

        /*
        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions( Math.PI / 4.0 )
        );
        */

        // add the chart to a panel...
        final ChartPanel chartPanel = new ChartPanel( chart );
        chartPanel.setPreferredSize( NaturalSelectionDefaults.BUNNY_STATS_SIZE );
        //setPreferredSize( NaturalSelectionDefaults.BUNNY_STATS_SIZE );
        setContentPane( chartPanel );

        pack();

        GeneListener listener = new GeneListener() {
            public void onChangeDominantAllele( Gene gene, boolean primary ) {

            }

            public void onChangeDistribution( Gene gene, int primary, int secondary ) {
                data.setValue( primary, gene.getName(), gene.getPrimaryAllele().getName() );
                data.setValue( secondary, gene.getName(), gene.getSecondaryAllele().getName() );
            }

            public void onChangeMutatable( Gene gene, boolean mutatable ) {

            }
        };

        ColorGene.getInstance().addListener( listener );
        TailGene.getInstance().addListener( listener );
        TeethGene.getInstance().addListener( listener );

    }

    private DefaultCategoryDataset createData() {

        // row keys...
        final String colorKey = ColorGene.getInstance().getName();
        final String tailKey = TailGene.getInstance().getName();
        final String teethKey = TeethGene.getInstance().getName();

        // column keys...
        final String whiteColor = ColorGene.getInstance().getPrimaryAllele().getName();
        final String brownColor = ColorGene.getInstance().getSecondaryAllele().getName();
        final String regularTail = TailGene.getInstance().getPrimaryAllele().getName();
        final String fuzzyTail = TailGene.getInstance().getSecondaryAllele().getName();
        final String regularTeeth = TeethGene.getInstance().getPrimaryAllele().getName();
        final String longTeeth = TeethGene.getInstance().getSecondaryAllele().getName();

        // create the dataset...
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue( ColorGene.getInstance().getPrimaryPhenotypeCount(), colorKey, whiteColor );
        dataset.addValue( ColorGene.getInstance().getSecondaryPhenotypeCount(), colorKey, brownColor );

        dataset.addValue( TailGene.getInstance().getPrimaryPhenotypeCount(), tailKey, regularTail );
        dataset.addValue( TailGene.getInstance().getSecondaryPhenotypeCount(), tailKey, fuzzyTail );

        dataset.addValue( TeethGene.getInstance().getPrimaryPhenotypeCount(), teethKey, regularTeeth );
        dataset.addValue( TeethGene.getInstance().getSecondaryPhenotypeCount(), teethKey, longTeeth );

        return dataset;

    }

}