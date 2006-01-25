/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.view.monitors;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.mirror.PartialMirror;
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.CategoryPlot;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.chart.renderer.category.BarRenderer;
//import org.jfree.data.category.DefaultCategoryDataset;
//import org.jfree.ui.RectangleInsets;
//import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;

/**
 * PowerMeter
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
//public class PowerMeter extends GraphicLayerSet {
public class PowerMeter extends JDialog {
//    private DefaultCategoryDataset dataset;
//    private JFreeChart chart;
//    private CategoryPlot plot;
    private String insideSeries;
    private String outsideSeries;
    private String category;
    private PartialMirror rightMirror;
    private Paint belowLasingPaint = Color.green;
    private Paint aboveLasingPaint = Color.red;

//    public PowerMeter( ApparatusPanel apparatusPanel, LaserModel model, final PartialMirror rightMirror ) throws HeadlessException {
    public PowerMeter( Frame owner, Point location, LaserModel model, final PartialMirror rightMirror ) throws HeadlessException {
//        super( apparatusPanel );
//        super( owner, "Power Meter", false );
//
//        setLocation( location );
//        setUndecorated( true );
//        getRootPane().setBorder( BorderFactory.createLineBorder( Color.BLACK, 1 ) );
//
//        this.rightMirror = rightMirror;
//
//        dataset = new DefaultCategoryDataset();
//        chart = ChartFactory.createBarChart( "",
//                                             "",
//                                             "",
//                                             dataset,
//                                             PlotOrientation.HORIZONTAL,
//                                             true,
//                                             false,
//                                             false );
//        chart.setBackgroundPaint( Color.gray );
//        plot = chart.getCategoryPlot();
//        plot.setBackgroundPaint( Color.black );
//        plot.getRangeAxis().setAutoRange( false );
//        plot.getRangeAxis().setRange( 0, LaserConfig.KABOOM_THRESHOLD );
//        plot.getRangeAxis().setTickLabelsVisible( false );
//        plot.getDomainAxis().setTickLabelsVisible( false );
//        plot.setInsets( new RectangleInsets( 0,0,0,0 ));
//
//        insideSeries = "Inside cavity";
//        outsideSeries = "Exiting cavity";
//        category = "Power";
//
//        ChartPanel chartPanel = new ChartPanel( chart );
//        chartPanel.setPreferredSize( new Dimension( 300, 100 ) );
//
//        setContentPane( chartPanel );
//        pack();
////        RefineryUtilities.centerDialogInParent( this );
//
//        // Add the agent that will update the chart when the model changes
//        model.addLaserListener( new ChartUpdater() );
//
////        chartPanel.setLocation( 100, 100 );
////        apparatusPanel.add( chartPanel );
    }

    /**
     * Updates the chart when the model reports changes in the number of lasing photons
     */
    private class ChartUpdater extends LaserModel.ChangeListenerAdapter {
//        final BarRenderer renderer;
//
//        public ChartUpdater() {
//            renderer = (BarRenderer)plot.getRenderer();
//            renderer.setSeriesPaint( 1, aboveLasingPaint );
//        }
//
//        public void lasingPopulationChanged( LaserModel.ChangeEvent event ) {
//            int lasingPopulation = event.getLasingPopulation();
//            dataset.addValue( lasingPopulation, insideSeries, category );
//            if( lasingPopulation < LaserConfig.LASING_THRESHOLD ) {
//                renderer.setSeriesPaint( 0, belowLasingPaint );
//            }
//            else {
//                renderer.setSeriesPaint( 0, aboveLasingPaint );
//            }
//
//            int exitingPhotons = (int)( ( 1 - rightMirror.getReflectivity() ) * lasingPopulation );
//            dataset.addValue( exitingPhotons, outsideSeries, category );
//        }
    }
}
