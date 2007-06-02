package edu.colorado.phet.energyskatepark.plots;

import edu.colorado.phet.common.jfreechartphet.piccolo.DynamicJFreeChartNode;
import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartCursorNode;
import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.view.EnergyLookAndFeel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkPlaybackPanel;
import edu.umd.cs.piccolo.nodes.PPath;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;

/**
 * Author: Sam Reid
 * May 22, 2007, 2:18:52 AM
 */
public class EnergyVsTimePlot {
    private EnergySkateParkModel model;
    private TimeSeriesModel timeSeriesModel;
    private IClock clock;
//    private double initialTime = Double.NEGATIVE_INFINITY;

    private JDialog dialog;
    private PhetPCanvas phetPCanvas;
    private DynamicJFreeChartNode dynamicJFreeChartNode;
    private JFreeChart chart;

    private ZoomControlNode zoomControlNode;
    private ReadoutTextNode thermalPText;
    private ReadoutTextNode keText;
    private ReadoutTextNode peText;
    private ReadoutTextNode totalText;
//    private double recordTime = 0.0;

    public EnergyVsTimePlot( JFrame parentFrame, Clock clock, EnergySkateParkModel model, final TimeSeriesModel timeSeriesModel ) {
        this.model = model;
        this.clock = clock;
        this.timeSeriesModel = timeSeriesModel;
        phetPCanvas = new BufferedPhetPCanvas();
        phetPCanvas.setBackground( EnergySkateParkLookAndFeel.backgroundColor );

        chart = ChartFactory.createXYLineChart(
                EnergySkateParkStrings.getString( "plots.energy-vs-time" ),
                "time (sec)", "Energy (Joules)", new XYSeriesCollection( new XYSeries( "series" ) ),
                PlotOrientation.VERTICAL, false, false, false );
        dynamicJFreeChartNode = new DynamicJFreeChartNode( phetPCanvas, chart );

        dynamicJFreeChartNode.addSeries( "Thermal", Color.red );
        dynamicJFreeChartNode.addSeries( "KE", Color.green );
        dynamicJFreeChartNode.addSeries( "PE", Color.blue );
        dynamicJFreeChartNode.addSeries( "Total", new EnergyLookAndFeel().getTotalEnergyColor() );

        chart.getXYPlot().getRangeAxis().setRange( 0, 7000 );
        chart.getXYPlot().getDomainAxis().setRange( 0, 50 );
        dynamicJFreeChartNode.setBufferedImmediateSeries();

        thermalPText = new ReadoutTextNode( Color.red );
        keText = new ReadoutTextNode( Color.green );
        peText = new ReadoutTextNode( Color.blue );
        totalText = new ReadoutTextNode( new EnergyLookAndFeel().getTotalEnergyColor() );

        timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {
            public void dataSeriesChanged() {
                double thermal = getEnergySkateParkModel().getBody( 0 ).getThermalEnergy();
                double ke = getEnergySkateParkModel().getBody( 0 ).getKineticEnergy();
                double pe = getEnergySkateParkModel().getBody( 0 ).getPotentialEnergy();
                double total = getEnergySkateParkModel().getBody( 0 ).getTotalEnergy();

                DecimalFormat formatter = new DecimalFormat( "0.00" );
                thermalPText.setText( "Thermal = " + formatter.format( thermal ) + " J" );
                keText.setText( "KE = " + formatter.format( ke ) + " J" );
                peText.setText( "PE = " + formatter.format( pe ) + " J" );
                totalText.setText( "Total = " + formatter.format( total ) + " J" );

//                recordTime = getEnergySkateParkModel().getTime() - initialTime;
//                System.out.println( "simulationTime = " + recordTime );
                double time=timeSeriesModel.getRecordTime();
                dynamicJFreeChartNode.addValue( 0, time, thermal );
                dynamicJFreeChartNode.addValue( 1, time, ke );
                dynamicJFreeChartNode.addValue( 2, time, pe );
                dynamicJFreeChartNode.addValue( 3, time, total );

            }
        } );
        dialog = new JDialog( parentFrame, EnergySkateParkStrings.getString( "plots.energy-vs-time" ), false );
        JPanel contentPane = new JPanel( new BorderLayout() );
        contentPane.add( phetPCanvas, BorderLayout.CENTER );
        contentPane.add( new EnergySkateParkPlaybackPanel( timeSeriesModel, clock ), BorderLayout.SOUTH );
        dialog.setContentPane( contentPane );
        dialog.setSize( 800, 400 );
        dialog.addComponentListener( new ComponentAdapter() {
            public void componentHidden( ComponentEvent e ) {
                timeSeriesModel.setLiveMode();
            }
        } );
        phetPCanvas.addScreenChild( dynamicJFreeChartNode );
        phetPCanvas.addScreenChild( thermalPText );
        phetPCanvas.addScreenChild( keText );
        phetPCanvas.addScreenChild( peText );
        phetPCanvas.addScreenChild( totalText );

        dialog.setLocation( 0, Toolkit.getDefaultToolkit().getScreenSize().height - dialog.getHeight() - 100 );

        final JFreeChartCursorNode jFreeChartCursorNode = new JFreeChartCursorNode( dynamicJFreeChartNode );
        phetPCanvas.addScreenChild( jFreeChartCursorNode );
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
        phetPCanvas.addScreenChild( zoomControlNode );

        dialog.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );
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
            timeSeriesModel.setRecordMode();
        }
        else if( !visible && dialog.isVisible() ) {
            timeSeriesModel.setLiveMode();
        }

        dialog.setVisible( visible );
        relayout();
    }

    private void relayout() {
        dynamicJFreeChartNode.setBounds( 0, 0, phetPCanvas.getWidth() - zoomControlNode.getFullBounds().getWidth(), phetPCanvas.getHeight() );
        zoomControlNode.setOffset( dynamicJFreeChartNode.getDataArea().getMaxX(), dynamicJFreeChartNode.getDataArea().getCenterY() );
        thermalPText.setOffset( dynamicJFreeChartNode.getDataArea().getX() + 2, dynamicJFreeChartNode.getDataArea().getY() );
        totalText.setOffset( dynamicJFreeChartNode.getDataArea().getX() + 2, thermalPText.getFullBounds().getMaxY() + 5 );
        keText.setOffset( dynamicJFreeChartNode.getDataArea().getCenterX(), dynamicJFreeChartNode.getDataArea().getY() );
        peText.setOffset( dynamicJFreeChartNode.getDataArea().getCenterX(), keText.getFullBounds().getMaxY() + 5 );
    }

    public void reset() {
        dynamicJFreeChartNode.clear();
    }
}
