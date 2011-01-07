// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.plots;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartCursorNode;
import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.DynamicJFreeChartNode;
import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.DynamicJFreeChartNodeControlPanel;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.PopupMenuHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.view.EnergyLookAndFeel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel;
import edu.colorado.phet.energyskatepark.view.swing.EnergySkateParkPlaybackPanel;
import edu.umd.cs.piccolo.nodes.PPath;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 22, 2007, 2:18:52 AM
 */
public class EnergyTimePlot {
    private EnergySkateParkModel model;
    private TimeSeriesModel timeSeriesModel;
    private ConstantDtClock clock;

    private JDialog dialog;
    private PhetPCanvas phetPCanvas;
    private DynamicJFreeChartNode dynamicJFreeChartNode;
    private JFreeChart chart;

    private ZoomControlNode zoomControlNode;
    private ReadoutTextNode thermalPText;
    private ReadoutTextNode keText;
    private ReadoutTextNode peText;
    private ReadoutTextNode totalText;

    private ArrayList listeners = new ArrayList();
    private JFreeChartCursorNode jFreeChartCursorNode;

    //    public static final double MAX_TIME = 50.0;
    //    public static final double MAX_TIME = 30.0;
    public static final double MAX_TIME = 20.0;
    private JDialog developerControlDialog;
    private EnergySkateParkPlaybackPanel playbackPanel;
//    public static final double MAX_TIME = 5.0;

    public EnergyTimePlot( EnergySkateParkModule module, JFrame parentFrame, ConstantDtClock clock, EnergySkateParkModel model, final TimeSeriesModel timeSeriesModel ) {
        this.model = model;
        this.clock = clock;
        this.timeSeriesModel = timeSeriesModel;
        phetPCanvas = new BufferedPhetPCanvas();
        phetPCanvas.setBackground( EnergySkateParkLookAndFeel.backgroundColor );

        chart = ChartFactory.createXYLineChart(
                EnergySkateParkStrings.getString( "plots.energy-vs-time" ),
                EnergySkateParkStrings.getString( "plots.energy-vs-time.time" ),
                EnergySkateParkStrings.getString( "plots.energy-vs-time.energy" ),
                new XYSeriesCollection( new XYSeries( "series" ) ),
                PlotOrientation.VERTICAL, false, false, false );
        dynamicJFreeChartNode = new DynamicJFreeChartNode( phetPCanvas, chart );
        dynamicJFreeChartNode.setBuffered( true );

        dynamicJFreeChartNode.setBufferedImmediateSeries();
//        dynamicJFreeChartNode.setPiccoloSeries();

        dynamicJFreeChartNode.addSeries( EnergySkateParkStrings.getString( "energy.thermal" ), Color.red );
        dynamicJFreeChartNode.addSeries( EnergySkateParkStrings.getString( "energy.kinetic.abbreviation" ), Color.green );
        dynamicJFreeChartNode.addSeries( EnergySkateParkStrings.getString( "energy.potential.abbreviation" ), Color.blue );
        dynamicJFreeChartNode.addSeries( EnergySkateParkStrings.getString( "energy.total" ), new EnergyLookAndFeel().getTotalEnergyColor() );

        chart.getXYPlot().getRangeAxis().setRange( 0, 7000 );
        chart.getXYPlot().getDomainAxis().setRange( 0, MAX_TIME );

        thermalPText = new ReadoutTextNode( Color.red );
        keText = new ReadoutTextNode( Color.green );
        peText = new ReadoutTextNode( Color.blue );
        totalText = new ReadoutTextNode( new EnergyLookAndFeel().getTotalEnergyColor() );

        timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {
            public void dataSeriesChanged() {
                if( getEnergySkateParkModel().getNumBodies() > 0 ) {
                    double thermal = getEnergySkateParkModel().getBody( 0 ).getThermalEnergy();
                    double ke = getEnergySkateParkModel().getBody( 0 ).getKineticEnergy();
                    double pe = getEnergySkateParkModel().getBody( 0 ).getPotentialEnergy();
                    double total = getEnergySkateParkModel().getBody( 0 ).getTotalEnergy();

                    updateReadouts();

                    double time = timeSeriesModel.getRecordTime();
                    dynamicJFreeChartNode.addValue( 0, time, thermal );
                    dynamicJFreeChartNode.addValue( 1, time, ke );
                    dynamicJFreeChartNode.addValue( 2, time, pe );
                    dynamicJFreeChartNode.addValue( 3, time, total );

                    jFreeChartCursorNode.setMaxDragTime( time );
                }
            }

        } );
        timeSeriesModel.addPlaybackTimeChangeListener( new TimeSeriesModel.PlaybackTimeListener() {
            public void timeChanged() {
                if( getEnergySkateParkModel().getNumBodies() > 0 ) {
                    updateReadouts();
                }
            }
        } );
        //this listener updates the readouts even when mode is live
//        model.addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
//            public void primaryBodyChanged() {
//                updateReadouts();
//            }
//        } );

        dialog = new JDialog( parentFrame, EnergySkateParkStrings.getString( "plots.energy-vs-time" ), false );
        JPanel contentPane = new JPanel( new BorderLayout() );
        contentPane.add( phetPCanvas, BorderLayout.CENTER );
        playbackPanel = new EnergySkateParkPlaybackPanel( module, timeSeriesModel, clock );
        contentPane.add( playbackPanel, BorderLayout.SOUTH );
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

        jFreeChartCursorNode = new JFreeChartCursorNode( dynamicJFreeChartNode );
        phetPCanvas.addScreenChild( jFreeChartCursorNode );
        jFreeChartCursorNode.addListener( new JFreeChartCursorNode.Listener() {
            public void cursorTimeChanged() {
//                EnergySkateParkLogging.println( "jFreeChartCursorNode.getTime() = " + jFreeChartCursorNode.getTime() );
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
                    clear();
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

        developerControlDialog = new JDialog( parentFrame, EnergySkateParkStrings.getString( "plots.energy-vs-time" ) + " (developer controls)", false );
        developerControlDialog.setContentPane( new DynamicJFreeChartNodeControlPanel( dynamicJFreeChartNode ) );
        developerControlDialog.pack();
        developerControlDialog.setLocation( dialog.getLocation().x, dialog.getLocation().y - developerControlDialog.getHeight() );

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem item = new JMenuItem( "Show Renderers" );
        item.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                developerControlDialog.setVisible( true );
            }
        } );
        popupMenu.add( item );
        phetPCanvas.addInputEventListener( new PopupMenuHandler( phetPCanvas, popupMenu ) );
    }

    private void updateReadouts() {
        double thermal = getEnergySkateParkModel().getBody( 0 ).getThermalEnergy();
        double ke = getEnergySkateParkModel().getBody( 0 ).getKineticEnergy();
        double pe = getEnergySkateParkModel().getBody( 0 ).getPotentialEnergy();
        double total = getEnergySkateParkModel().getBody( 0 ).getTotalEnergy();

        DecimalFormat formatter = new DecimalFormat( "0.00" );
        thermalPText.setText( EnergySkateParkStrings.getString( "energy.thermal" ) + " = " + formatter.format( thermal ) + " J" );
        keText.setText( EnergySkateParkStrings.getString( "energy.kinetic.abbreviation" ) + " = " + formatter.format( ke ) + " J" );
        peText.setText( EnergySkateParkStrings.getString( "energy.potential.abbreviation" ) + " = " + formatter.format( pe ) + " J" );
        totalText.setText( EnergySkateParkStrings.getString( "energy.total" ) + " = " + formatter.format( total ) + " J" );
    }

    public class ReadoutTextNode extends PhetPNode {
        private ShadowPText text;
        private PPath background;

        public ReadoutTextNode( Color color ) {
            text = new ShadowPText( " " );
            text.setFont( new PhetFont( Font.BOLD, 14 ) );
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

    public boolean isVisible() {
        return dialog.isVisible();
    }

    public void setVisible( boolean visible ) {
        if( visible != dialog.isVisible() ) {
            dialog.setVisible( visible );
//            developerControlDialog.setVisible( visible );
            relayout();
            notifyVisibilityChanged();
        }
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
        dialog.setVisible( false );
        clear();
    }

    private void clear() {
        dynamicJFreeChartNode.clear();
        playbackPanel.reset();
    }

    public static interface Listener {
        void visibilityChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    private void notifyVisibilityChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener)listeners.get( i ) ).visibilityChanged();
        }
    }
}
