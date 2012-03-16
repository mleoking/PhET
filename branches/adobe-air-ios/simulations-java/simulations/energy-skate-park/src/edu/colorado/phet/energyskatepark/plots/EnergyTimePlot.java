// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.plots;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartCursorNode;
import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.DynamicJFreeChartNode;
import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.DynamicJFreeChartNodeControlPanel;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.PopupMenuHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.view.EnergyLookAndFeel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel;
import edu.colorado.phet.energyskatepark.view.swing.EnergySkateParkPlaybackPanel;
import edu.umd.cs.piccolo.nodes.PPath;

import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.UserComponents.energyTimePlot;

/**
 * Author: Sam Reid
 * May 22, 2007, 2:18:52 AM
 */
public class EnergyTimePlot {
    private final EnergySkateParkModel model;
    private final TimeSeriesModel timeSeriesModel;
    private final ConstantDtClock clock;

    private final JDialog dialog;
    private final PhetPCanvas phetPCanvas;
    private final DynamicJFreeChartNode dynamicJFreeChartNode;
    private final JFreeChart chart;

    private final ZoomControlNode zoomControlNode;
    private final ReadoutTextNode thermalPText;
    private final ReadoutTextNode keText;
    private final ReadoutTextNode peText;
    private final ReadoutTextNode totalText;

    private final ArrayList listeners = new ArrayList();
    private JFreeChartCursorNode jFreeChartCursorNode;

    public static final double MAX_TIME = 20.0;
    private final JDialog developerControlDialog;
    private final EnergySkateParkPlaybackPanel playbackPanel;

    public EnergyTimePlot( AbstractEnergySkateParkModule module, JFrame parentFrame, ConstantDtClock clock, EnergySkateParkModel model, final TimeSeriesModel timeSeriesModel ) {
        this.model = model;
        this.clock = clock;
        this.timeSeriesModel = timeSeriesModel;
        phetPCanvas = new BufferedPhetPCanvas();
        phetPCanvas.setBackground( EnergySkateParkLookAndFeel.backgroundColor );

        chart = ChartFactory.createXYLineChart(
                EnergySkateParkResources.getString( "plots.energy-vs-time" ),
                EnergySkateParkResources.getString( "plots.energy-vs-time.time" ),
                EnergySkateParkResources.getString( "plots.energy-vs-time.energy" ),
                new XYSeriesCollection( new XYSeries( "series" ) ),
                PlotOrientation.VERTICAL, false, false, false );
        dynamicJFreeChartNode = new DynamicJFreeChartNode( phetPCanvas, chart );
        dynamicJFreeChartNode.setBuffered( true );

        dynamicJFreeChartNode.setBufferedImmediateSeries();

        dynamicJFreeChartNode.addSeries( EnergySkateParkResources.getString( "energy.thermal" ), PhetColorScheme.RED_COLORBLIND );
        dynamicJFreeChartNode.addSeries( EnergySkateParkResources.getString( "energy.kinetic.abbreviation" ), Color.green );
        dynamicJFreeChartNode.addSeries( EnergySkateParkResources.getString( "energy.potential.abbreviation" ), Color.blue );
        dynamicJFreeChartNode.addSeries( EnergySkateParkResources.getString( "energy.total" ), new EnergyLookAndFeel().getTotalEnergyColor() );

        chart.getXYPlot().getRangeAxis().setRange( 0, 7000 );
        chart.getXYPlot().getDomainAxis().setRange( 0, MAX_TIME );

        thermalPText = new ReadoutTextNode( PhetColorScheme.RED_COLORBLIND );
        keText = new ReadoutTextNode( Color.green );
        peText = new ReadoutTextNode( Color.blue );
        totalText = new ReadoutTextNode( new EnergyLookAndFeel().getTotalEnergyColor() );

        timeSeriesModel.addListener( new TimeSeriesModel.Adapter() {
            public void dataSeriesChanged() {
                if ( getEnergySkateParkModel().getNumBodies() > 0 ) {
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
                if ( getEnergySkateParkModel().getNumBodies() > 0 ) {
                    updateReadouts();
                }
            }
        } );

        dialog = new JDialog( parentFrame, EnergySkateParkResources.getString( "plots.energy-vs-time" ), false );
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
                if ( timeSeriesModel.numPlaybackStates() == 0 ) {
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

        developerControlDialog = new JDialog( parentFrame, EnergySkateParkResources.getString( "plots.energy-vs-time" ) + " (developer controls)", false );
        developerControlDialog.setContentPane( new DynamicJFreeChartNodeControlPanel( dynamicJFreeChartNode ) );
        developerControlDialog.pack();
        developerControlDialog.setLocation( dialog.getLocation().x, dialog.getLocation().y - developerControlDialog.getHeight() );

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem item = new JMenuItem( "Show Renderers" );//TODO: not interationalized, but maybe nobody even knows about this feature!
        item.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                developerControlDialog.setVisible( true );
            }
        } );
        popupMenu.add( item );
        phetPCanvas.addInputEventListener( new PopupMenuHandler( energyTimePlot, phetPCanvas, popupMenu ) );
    }

    private void updateReadouts() {
        double thermal = getEnergySkateParkModel().getBody( 0 ).getThermalEnergy();
        double ke = getEnergySkateParkModel().getBody( 0 ).getKineticEnergy();
        double pe = getEnergySkateParkModel().getBody( 0 ).getPotentialEnergy();
        double total = getEnergySkateParkModel().getBody( 0 ).getTotalEnergy();

        DecimalFormat formatter = new DecimalFormat( "0.00" );
        thermalPText.setText( EnergySkateParkResources.getString( "energy.thermal" ) + " = " + formatter.format( thermal ) + " J" );
        keText.setText( EnergySkateParkResources.getString( "energy.kinetic.abbreviation" ) + " = " + formatter.format( ke ) + " J" );
        peText.setText( EnergySkateParkResources.getString( "energy.potential.abbreviation" ) + " = " + formatter.format( pe ) + " J" );
        totalText.setText( EnergySkateParkResources.getString( "energy.total" ) + " = " + formatter.format( total ) + " J" );
    }

    public class ReadoutTextNode extends PhetPNode {
        private final ShadowPText text;
        private final PPath background;

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
        if ( visible != dialog.isVisible() ) {
            dialog.setVisible( visible );
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
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).visibilityChanged();
        }
    }
}
