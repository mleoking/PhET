package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.jfreechartphet.piccolo.DynamicJFreeChartNode;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Author: Sam Reid
 * May 22, 2007, 2:18:52 AM
 */
public class EnergyVsTimePlot {
    private EnergySkateParkModel model;
    private JDialog dialog;
    private IClock clock;
    private double initialTime;
    private DynamicJFreeChartNode dynamicJFreeChartNode;

    public EnergyVsTimePlot( JFrame phetFrame, IClock clock, EnergySkateParkModel model ) {
        this.model = model;
        this.clock = clock;
        PhetPCanvas graphCanvas = new BufferedPhetPCanvas();

        JFreeChart chart = ChartFactory.createXYLineChart(
                EnergySkateParkStrings.getString( "plots.energy-vs-time" ),
                "time (sec)", "Energy (Joules)", new XYSeriesCollection( new XYSeries( "series" ) ),
                PlotOrientation.VERTICAL, false, false, false );
        dynamicJFreeChartNode = new DynamicJFreeChartNode( graphCanvas, chart );
        dynamicJFreeChartNode.addSeries( "Thermal", Color.red );
        dynamicJFreeChartNode.addSeries( "KE", Color.green );
        dynamicJFreeChartNode.addSeries( "PE", Color.blue );

        chart.getXYPlot().getRangeAxis().setRange( 0, 7000 );
        chart.getXYPlot().getDomainAxis().setRange( 0, 50 );
        dynamicJFreeChartNode.setBufferedImmediateSeries();

        final ShadowPText thermalPText = new ShadowPText( " " );
        thermalPText.setTextPaint( Color.red );

        final ShadowPText keText = new ShadowPText( " " );
        keText.setTextPaint( Color.green );

        final ShadowPText peText = new ShadowPText( " " );
        peText.setTextPaint( Color.blue );

        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                double thermal = getEnergySkateParkModel().getBody( 0 ).getThermalEnergy();
                double ke = getEnergySkateParkModel().getBody( 0 ).getKineticEnergy();
                double pe = getEnergySkateParkModel().getBody( 0 ).getPotentialEnergy();

                DecimalFormat formatter = new DecimalFormat( "0.00" );
                thermalPText.setText( "Thermal = " + formatter.format( thermal ) + " J" );
                keText.setText( "KE = " + formatter.format( ke ) + " J" );
                peText.setText( "PE = " + formatter.format( pe ) + " J" );

//                double simulationTime = clockEvent.getSimulationTime() - initialTime;
                double simulationTime=getEnergySkateParkModel().getTime()-initialTime;
                dynamicJFreeChartNode.addValue( 0, simulationTime, thermal );
                dynamicJFreeChartNode.addValue( 1, simulationTime, ke );
                dynamicJFreeChartNode.addValue( 2, simulationTime, pe );
            }
        } );

        dialog = new JDialog( phetFrame, EnergySkateParkStrings.getString( "plots.energy-vs-time" ), false );
        dialog.setContentPane( graphCanvas );
        dialog.setSize( 800, 270 );
        graphCanvas.addScreenChild( dynamicJFreeChartNode );
        graphCanvas.addScreenChild( thermalPText );
        graphCanvas.addScreenChild( keText );
        graphCanvas.addScreenChild( peText );
        dynamicJFreeChartNode.setBounds( 0, 0, dialog.getWidth() - 50, dialog.getHeight() - 40 );

        thermalPText.setOffset( dynamicJFreeChartNode.getDataArea().getX(), dynamicJFreeChartNode.getDataArea().getY() );
        keText.setOffset( dynamicJFreeChartNode.getDataArea().getCenterX(), dynamicJFreeChartNode.getDataArea().getY() );
        peText.setOffset( dynamicJFreeChartNode.getDataArea().getCenterX(), keText.getFullBounds().getMaxY() + 5 );

        dialog.setVisible( true );
        dialog.setLocation( 0, Toolkit.getDefaultToolkit().getScreenSize().height - dialog.getHeight() - 100 );
    }

    private EnergySkateParkModel getEnergySkateParkModel() {
        return model;
    }

    public void setVisible( boolean visible ) {
        if( visible && !dialog.isVisible() ) {
            reset();
        }
        dialog.setVisible( visible );
    }

    public void reset() {
        initialTime = getEnergySkateParkModel().getTime();
        dynamicJFreeChartNode.clear();
    }
}
