/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.util.WIStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

/**
 * User: Sam Reid
 * Date: Apr 12, 2006
 * Time: 8:01:59 PM
 * Copyright (c) Apr 12, 2006 by Sam Reid
 */

public class WaveChartGraphic extends PNode {
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private WaveModel waveModel;
    private MutableColor strokeColor;
    private JFreeChart jFreeChart;
    private JFreeChartNode jFreeChartNode;
    private PPath path;
    private boolean colorized = true;

    public WaveChartGraphic( String title, LatticeScreenCoordinates latticeScreenCoordinates, WaveModel waveModel, MutableColor strokeColor, String distanceUnits, double minX, double maxX ) {
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        this.waveModel = waveModel;
        this.strokeColor = strokeColor;
        XYSeries series = new XYSeries( "0" );
        XYDataset dataset = new XYSeriesCollection( series );
        jFreeChart = ChartFactory.createXYLineChart( title, WIStrings.getString( "position" ), title, dataset, PlotOrientation.VERTICAL, false, false, false );
        jFreeChart.getXYPlot().getRangeAxis().setTickLabelsVisible( false );
        jFreeChart.getXYPlot().getRangeAxis().setRange( -1.0, 1.0 );
        jFreeChartNode = new JFreeChartNode( jFreeChart, true );
        jFreeChartNode.setBounds( 0, 0, 500, 200 );

//        String hello = MessageFormat.format( SimStrings.get( "hello.0" ), new Object[]{units} );
//        setHorizontalLabel( WIStrings.getString( "position.0" ) + distanceUnits );
        setHorizontalLabel( MessageFormat.format( WIStrings.getString( "position.0" ), new Object[]{distanceUnits} ) );
        setHorizontalRange( minX, maxX );

        jFreeChartNode.updateChartRenderingInfo();
        path = new PPath();
        path.setStroke( new BasicStroke( 3 ) );
        path.setStrokePaint( Color.blue );

        addChild( jFreeChartNode );
        addChild( path );
        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                updateLocation();
            }
        } );
        updateLocation();

        strokeColor.addListener( new MutableColor.Listener() {
            public void colorChanged() {
                updateColor();
            }
        } );
        updateColor();
    }

    public void setHorizontalLabel( String horizontalUnits ) {
        jFreeChart.getXYPlot().getDomainAxis().setLabel( horizontalUnits );
    }

    public void setHorizontalRange( double min, double max ) {
        jFreeChart.getXYPlot().getDomainAxis().setRange( min, max );
    }

    public Rectangle2D getChartBounds() {
        Rectangle2D bounds = jFreeChartNode.getFullBounds();
        localToParent( bounds );
        return bounds;
    }

    protected void updateLocation() {
        synchronizeWidth();
        synchronizeWidth();//in case the first one changed the insets of the graph

        Rectangle2D r2 = jFreeChartNode.getBounds();
        Rectangle2D data = jFreeChartNode.getDataArea();
        double dataX = latticeScreenCoordinates.toScreenCoordinates( 0, 0 ).getX();
        double dataY = latticeScreenCoordinates.toScreenCoordinates( 0, waveModel.getHeight() ).getY() + getChartOffset();
        double dataInsetX = data.getX() - r2.getX();
        jFreeChartNode.setBounds( 0, 0, r2.getWidth(), r2.getHeight() );
        jFreeChartNode.setOffset( dataX - dataInsetX, dataY );
    }

    protected double getChartOffset() {
        return 0;
    }

    private void synchronizeWidth() {
        double diff = getDesiredDataWidth() - getActualDataWidth();
        changeDataWidth( (int)diff );
    }

    private void changeDataWidth( int increase ) {
        Rectangle2D r2 = jFreeChartNode.getBounds();
        jFreeChartNode.setBounds( r2.getX(), r2.getY(), r2.getWidth() + increase, r2.getHeight() );
        jFreeChartNode.updateChartRenderingInfo();
    }

    private double getActualDataWidth() {
        return jFreeChartNode.getDataArea().getWidth();
    }

    public JFreeChartNode getjFreeChartNode() {
        return jFreeChartNode;
    }

    private double getDesiredDataWidth() {
        Point2D min = latticeScreenCoordinates.toScreenCoordinates( 0, 0 );
        Point2D max = latticeScreenCoordinates.toScreenCoordinates( waveModel.getWidth() - 1, waveModel.getHeight() - 1 );
        return max.getX() - min.getX();
    }

    public void updateChart() {
        GeneralPath generalPath = new GeneralPath();
        Point2D[]pts = readValues();//todo this just assumes the chart transform matches perfectly
        if( pts.length > 0 ) {
            generalPath.moveTo( (float)pts[0].getX(), (float)pts[0].getY() );//todo crop to fit inside data area.
        }
        for( int i = 1; i < pts.length; i++ ) {
            generalPath.lineTo( (float)pts[i].getX(), (float)pts[i].getY() );
        }
        path.setPathTo( generalPath );
        path.setOffset( getPathLocation() );
    }

    protected Point2D getPathLocation() {
        Point2D nodeLoc = jFreeChartNode.plotToNode( new Point2D.Double( 0, 0 ) );
        jFreeChartNode.localToParent( nodeLoc );
        return nodeLoc;
    }

    protected Point2D[] readValues() {
        return new WaveSampler( waveModel, -60, latticeScreenCoordinates.getCellWidth() ).readValues();
    }

    public void setCurveColor( Color color ) {
        strokeColor.setColor( color );
        updateColor();
    }

    protected void updateColor() {

        path.setStrokePaint( colorized ? strokeColor.getColor() : Color.black );
//        setCurveColor( strokeColor.getColor() );
    }

    public double getChartHeight() {
        return jFreeChartNode.getFullBounds().getHeight();
    }

    public LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return latticeScreenCoordinates;
    }

    public WaveModel getWaveModel() {
        return waveModel;
    }

    public MutableColor getStrokeColor() {
        return strokeColor;
    }

    public void setCurveVisible( boolean visible ) {
        path.setVisible( visible );
    }

    public boolean isCurveVisible() {
        return path.getVisible();
    }

    public void setColorized( boolean colorized ) {
        this.colorized = colorized;
        updateColor();
    }
}
