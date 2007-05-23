package edu.colorado.phet.energyskatepark.plots;

import edu.colorado.phet.common.jfreechartphet.piccolo.DynamicJFreeChartNode;
import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartCursorNode;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.view.EnergyLookAndFeel;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private TimeSeriesModel timeSeriesModel;

    private ZoomControlNode zoomControlNode;
    private JFreeChart chart;
    private PSwing clearPSwing;
    private PhetPCanvas graphCanvas;

    public EnergyVsTimePlot( JFrame phetFrame, IClock clock, EnergySkateParkModel model, final TimeSeriesModel timeSeriesModel ) {
        this.model = model;
        this.clock = clock;
        this.timeSeriesModel = timeSeriesModel;
        graphCanvas = new BufferedPhetPCanvas();

        chart = ChartFactory.createXYLineChart(
                EnergySkateParkStrings.getString( "plots.energy-vs-time" ),
                "time (sec)", "Energy (Joules)", new XYSeriesCollection( new XYSeries( "series" ) ),
                PlotOrientation.VERTICAL, false, false, false );
        dynamicJFreeChartNode = new DynamicJFreeChartNode( graphCanvas, chart );
        dynamicJFreeChartNode.addSeries( "Thermal", Color.red );
        dynamicJFreeChartNode.addSeries( "KE", Color.green );
        dynamicJFreeChartNode.addSeries( "PE", Color.blue );

        dynamicJFreeChartNode.addSeries( "Total", new EnergyLookAndFeel().getTotalEnergyColor() );

        chart.getXYPlot().getRangeAxis().setRange( 0, 7000 );
        chart.getXYPlot().getDomainAxis().setRange( 0, 50 );
        dynamicJFreeChartNode.setBufferedImmediateSeries();

        final ReadoutTextNode thermalPText = new ReadoutTextNode( Color.red );
        final ReadoutTextNode keText = new ReadoutTextNode( Color.green );
        final ReadoutTextNode peText = new ReadoutTextNode( Color.blue );
        final ReadoutTextNode totalText = new ReadoutTextNode( new EnergyLookAndFeel().getTotalEnergyColor() );

        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                double thermal = getEnergySkateParkModel().getBody( 0 ).getThermalEnergy();
                double ke = getEnergySkateParkModel().getBody( 0 ).getKineticEnergy();
                double pe = getEnergySkateParkModel().getBody( 0 ).getPotentialEnergy();
                double total = getEnergySkateParkModel().getBody( 0 ).getTotalEnergy();

                DecimalFormat formatter = new DecimalFormat( "0.00" );
                thermalPText.setText( "Thermal = " + formatter.format( thermal ) + " J" );
                keText.setText( "KE = " + formatter.format( ke ) + " J" );
                peText.setText( "PE = " + formatter.format( pe ) + " J" );
                totalText.setText( "Total = " + formatter.format( total ) + " J" );

                double simulationTime = getEnergySkateParkModel().getTime() - initialTime;
                dynamicJFreeChartNode.addValue( 0, simulationTime, thermal );
                dynamicJFreeChartNode.addValue( 1, simulationTime, ke );
                dynamicJFreeChartNode.addValue( 2, simulationTime, pe );
                dynamicJFreeChartNode.addValue( 3, simulationTime, total );
            }
        } );

        dialog = new JDialog( phetFrame, EnergySkateParkStrings.getString( "plots.energy-vs-time" ), false );
        dialog.setContentPane( graphCanvas );
        dialog.setSize( 800, 270 );
        graphCanvas.addScreenChild( dynamicJFreeChartNode );
        graphCanvas.addScreenChild( thermalPText );
        graphCanvas.addScreenChild( keText );
        graphCanvas.addScreenChild( peText );
        graphCanvas.addScreenChild( totalText );
        dynamicJFreeChartNode.setBounds( 0, 0, dialog.getWidth() - 75, dialog.getHeight() - 40 );

        thermalPText.setOffset( dynamicJFreeChartNode.getDataArea().getX()+2, dynamicJFreeChartNode.getDataArea().getY() );
        totalText.setOffset( dynamicJFreeChartNode.getDataArea().getX()+2, thermalPText.getFullBounds().getMaxY() + 5 );
        keText.setOffset( dynamicJFreeChartNode.getDataArea().getCenterX(), dynamicJFreeChartNode.getDataArea().getY() );
        peText.setOffset( dynamicJFreeChartNode.getDataArea().getCenterX(), keText.getFullBounds().getMaxY() + 5 );

        dialog.setLocation( 0, Toolkit.getDefaultToolkit().getScreenSize().height - dialog.getHeight() - 100 );

        final JFreeChartCursorNode jFreeChartCursorNode = new JFreeChartCursorNode( dynamicJFreeChartNode );
        graphCanvas.addScreenChild( jFreeChartCursorNode );
        jFreeChartCursorNode.addListener( new JFreeChartCursorNode.Listener() {
            public void cursorTimeChanged() {
                System.out.println( "jFreeChartCursorNode.getTime() = " + jFreeChartCursorNode.getTime() );
                timeSeriesModel.setPlaybackMode();
                timeSeriesModel.setPlaybackTime( jFreeChartCursorNode.getTime() );
            }
        } );
        timeSeriesModel.addPlaybackTimeChangeListener( new TimeSeriesModel.PlaybackTimeListener() {
            public void timeChanged() {
                updateCursor( jFreeChartCursorNode, timeSeriesModel );
            }
        } );

        timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {

            public void dataSeriesChanged() {
                if( timeSeriesModel.numPlaybackStates() == 0 ) {
                    reset();
                }
            }

            public void modeChanged() {
                updateCursor( jFreeChartCursorNode, timeSeriesModel );
            }

            public void pauseChanged() {
                updateCursor( jFreeChartCursorNode, timeSeriesModel );
            }
        } );

        zoomControlNode = new VerticalZoomControl( chart.getXYPlot().getRangeAxis() );
        graphCanvas.addScreenChild( zoomControlNode );
        zoomControlNode.setOffset( dynamicJFreeChartNode.getDataArea().getMaxX(), dynamicJFreeChartNode.getDataArea().getCenterY() );

        JButton clear = new JButton( "Clear" );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                reset();
            }
        } );
        clearPSwing = new PSwing( clear );
        graphCanvas.addScreenChild( clearPSwing );
        relayout();
    }

    public class ReadoutTextNode extends PhetPNode {
        private ShadowPText text;
        private PPath background;

        public ReadoutTextNode( Color color ) {
            text = new ShadowPText( " " );
            text.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
            text.setTextPaint( color );
            text.setShadowColor( Color.black );
            background = new PhetPPath( text.getFullBounds(), EnergyLookAndFeel.getLegendBackground() );//todo: is this partial transparency a performance problem?
            addChild( background );
            addChild( text );
        }

        public void setText( String s ) {
            text.setText( s );
            background.setPathTo( text.getFullBounds() );
        }
    }

    private void updateCursor( JFreeChartCursorNode jFreeChartCursorNode, TimeSeriesModel timeSeriesModel ) {
        jFreeChartCursorNode.setVisible( timeSeriesModel.isPaused() || timeSeriesModel.isPlaybackMode() );
        jFreeChartCursorNode.setTime( timeSeriesModel.getPlaybackTime() );
    }

    private EnergySkateParkModel getEnergySkateParkModel() {
        return model;
    }

    public void setVisible( boolean visible ) {
        if( visible && !dialog.isVisible() ) {
            reset();
            timeSeriesModel.setRecordMode();
        }

        dialog.setVisible( visible );
        if( visible ) {
            relayout();
        }
    }

    private void relayout() {
        clearPSwing.setOffset( graphCanvas.getWidth() - clearPSwing.getFullBounds().getWidth() - 2, 2 );
    }

    public void reset() {
        initialTime = getEnergySkateParkModel().getTime();
        dynamicJFreeChartNode.clear();
    }
}
