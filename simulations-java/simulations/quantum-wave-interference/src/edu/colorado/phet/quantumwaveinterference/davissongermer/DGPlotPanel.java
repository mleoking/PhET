/*  */
package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.model.Wavefunction;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Feb 5, 2006
 * Time: 2:41:14 PM
 */

public class DGPlotPanel extends PSwingCanvas {
    private XYSeriesCollection dataset;
    private int width = 750;
    private int height = 350;
    private DGModule dgModule;
    private XYSeries series;
    private JFreeChartNode jFreeChartNode;
    private IndicatorGraphic indicatorGraphic;
    private JFreeChart chart;
    private DGIntensityReader intensityReader;
    private double indicatorAngle;

    public DGPlotPanel( DGModule dgModule ) {
        this.dgModule = dgModule;
//        intensityReader = new RadialIntensityReader( dgModule.getDGModel() );
        intensityReader = new EdgeIntensityReader( dgModule.getDGModel() );
        series = new XYSeries( QWIResources.getString( "live.data" ) );
        dataset = new XYSeriesCollection( series );

        chart = ChartFactory.createScatterPlot( QWIResources.getString( "intensity.plot" ), QWIResources.getString( "angle.degrees" ), QWIResources.getString( "intensity" ), dataset, PlotOrientation.VERTICAL, true, false, false );
        chart.getXYPlot().getDomainAxis().setRange( 0, 90 );
        chart.getXYPlot().getRangeAxis().setRange( 0, 0.1 );
        chart.getXYPlot().getRangeAxis().setTickLabelsVisible( false );
        jFreeChartNode = new JFreeChartNode( chart );
        jFreeChartNode.setBounds( 0, 0, width, height );
        setPreferredSize( new Dimension( width, height ) );
        getLayer().addChild( jFreeChartNode );
        setPanEventHandler( null );
        setZoomEventHandler( null );
        new Timer( 100, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                replotAll();
            }
        } ).start();
        indicatorGraphic = new IndicatorGraphic();
        getLayer().addChild( indicatorGraphic );
        addComponentListener( new ComponentListener() {
            public void componentHidden( ComponentEvent e ) {
            }

            public void componentMoved( ComponentEvent e ) {
            }

            public void componentResized( ComponentEvent e ) {
                updateIndicator();
            }

            public void componentShown( ComponentEvent e ) {
                updateIndicator();
            }
        } );
    }

    protected void updateIndicator() {
        Point2D top = jFreeChartNode.plotToNode( new Point2D.Double( indicatorAngle, chart.getXYPlot().getRangeAxis().getUpperBound() ) );
        Point2D bottom = jFreeChartNode.plotToNode( new Point2D.Double( indicatorAngle, chart.getXYPlot().getRangeAxis().getLowerBound() ) );
        double height = top.getY() - bottom.getY();
        if( !indicatorGraphic.getOffset().equals( bottom ) || indicatorGraphic.getIndicatorHeight() != height ) {
            indicatorGraphic.setOffset( bottom );
            indicatorGraphic.setIndicatorHeight( height );
//        jFreeChartNode.setVisible( false );
            paintImmediately( 0, 0, getWidth(), getHeight() );
            repaint( 0, 0, getWidth(), getHeight() );
        }
    }

    public void setEdgeIntensityReader() {
        this.intensityReader = new EdgeIntensityReader( dgModule.getDGModel() );
        replotAll();
    }

    public void setRadialIntensityReader() {
        this.intensityReader = new RadialIntensityReader( dgModule.getDGModel() );
        replotAll();
    }

    public void setIndicatorVisible( boolean visible ) {
        indicatorGraphic.setVisible( visible );
        updateIndicator();
    }

    public void setIndicatorAngle( double angle ) {
        this.indicatorAngle = angle;
        updateIndicator();
    }

    public boolean isIntensityReaderEdge() {
        return intensityReader instanceof EdgeIntensityReader;
    }

    public boolean isIntensityReaderRadial() {
        return intensityReader instanceof RadialIntensityReader;
    }

    static int savedSeriesIndex = 0;

    public void saveDataAsLayer( String text ) {
//        XYSeries savedSeries = new XYSeries( "[" + savedSeriesIndex+"] "+text, false, true );
        XYSeries savedSeries = new XYSeries( text, false, true );
        copy( series, savedSeries );
        dataset.addSeries( savedSeries );
        savedSeriesIndex++;
    }

    private void copy( XYSeries source, XYSeries dest ) {
        dest.clear();
        for( int i = 0; i < source.getItemCount(); i++ ) {
            dest.add( source.getX( i ), source.getY( i ) );
        }
    }

    public void clearSnapshots() {
        dataset.removeAllSeries();
        dataset.addSeries( series );
    }

    public void visibilityChanged( boolean b ) {
        updateIndicator();
    }

    class IndicatorGraphic extends PhetPNode {
        private PPath path;
        private double height;

        public IndicatorGraphic() {
            path = new PPath();
            setIndicatorHeight( 0 );
            addChild( path );
        }

        public void setIndicatorHeight( double height ) {
            this.height = height;
            path.setPathTo( new Line2D.Double( 0, 0, 0, height ) );
        }

        public double getIndicatorHeight() {
            return height;
        }
    }

    public void replotAll() {
        updateIndicator();
        series.clear();
        double dAngle = 1;
        for( double angle = 0; angle <= 90; angle += dAngle ) {
            double intensity = getIntensity( angle );
            series.add( angle, intensity );
        }
        jFreeChartNode.repaint();
    }

    protected Wavefunction getWavefunction() {
        return dgModule.getQWIModel().getWavefunction();
    }

    private double getIntensity( double angle ) {
        return intensityReader.getIntensity( angle );
    }

}
