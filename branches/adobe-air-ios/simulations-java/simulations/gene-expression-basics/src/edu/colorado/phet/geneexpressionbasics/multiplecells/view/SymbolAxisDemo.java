package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.Font;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

public class SymbolAxisDemo {
    public static void main( String[] args ) {
        DefaultXYDataset dataset = new DefaultXYDataset();
        dataset.addSeries( "Values 1", new double[][] { { 1, 2, 3 }, { 2, 4, 1 } } );
        dataset.addSeries( "Values 2", new double[][] { { 4, 5, 6 }, { 0, 3, 2 } } );
        dataset.addSeries( "Values 3", new double[][] { { 1.5, 3.5, 5.5 }, { 0.5, 3.5, 2.5 } } );
        ValueAxis xAxis = new NumberAxis( "x" );
//      ValueAxis yAxis = new SymbolAxis("Symbol", new String[]{"One","Two","Three","Four","Five"});
        SymbolAxis yAxis = new SymbolAxis( "Symbol", new String[] { "None", "One", "Two", "Three", "Four", "Five", "Six" } );
        yAxis.setRange( 2, 10 );
        yAxis.setTickUnit( new NumberTickUnit( 2 ) );
        XYItemRenderer renderer = new XYLineAndShapeRenderer();
        XYPlot plot = new XYPlot( dataset, xAxis, yAxis, renderer );
        JFreeChart chart = new JFreeChart( "Symbol Axis Demo", new Font( "Tahoma", 0, 18 ), plot, true );
        JFrame frame = new JFrame( "XY Plot Demo" );
        frame.setContentPane( new ChartPanel( chart ) );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        frame.setVisible( true );
    }
}