package edu.colorado.phet.ec2.elements.jchart;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.ec2.elements.car.Car;
import edu.colorado.phet.ec2.elements.energy.EnergyObserver;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryAxis3D;
import org.jfree.chart.axis.NumberAxis3D;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.CategoryItemRenderer;
import org.jfree.chart.renderer.CategoryItemRendererState;
import org.jfree.chart.renderer.StackedBarRenderer3D;
import org.jfree.data.CategoryDataset;
import org.jfree.data.DefaultCategoryDataset;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class EnergyChartGraphic implements Graphic, EnergyObserver {
    private ValueAxis yaxis;
    private SRRDataset dataset;
    private JFreeChart chart;
    private Rectangle2D displayRectangle;
    private static StackedBarRenderer3D renderer;
    private boolean autoRange;

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    private boolean visible = true;

    public EnergyChartGraphic( Car car, Rectangle2D.Double modelCoords ) {
        final double[][] data = new double[][]
        {
            {3.0, 1.0, .5, 3.0},
            {0.0, 0, 0, 1.0},
            {0.0, 0, 0, .5},
        };

        dataset = createCategoryDataset( "Series ", new String[]{"Car Kinetic", "Car Potential", "Thermal", "Total"}, data );
        chart = createChart( dataset );
        car.addEnergyObserver( this );
    }

    public void setValues( double ke, double pe, double fric ) {
        dataset.setValue( ke, dataset.getRowKey( 0 ), dataset.getColumnKey( 0 ) );
        dataset.setValue( pe, dataset.getRowKey( 0 ), dataset.getColumnKey( 1 ) );
        dataset.setValue( fric, dataset.getRowKey( 0 ), dataset.getColumnKey( 2 ) );
        dataset.setValue( ke, dataset.getRowKey( 0 ), dataset.getColumnKey( 3 ) );
        dataset.setValue( pe, dataset.getRowKey( 1 ), dataset.getColumnKey( 3 ) );
        dataset.setValue( fric, dataset.getRowKey( 2 ), dataset.getColumnKey( 3 ) );
        dataset.fireDatasetChanged();
        //fireDatasetChanged used to happens on each.
    }

    public static class SRRDataset extends DefaultCategoryDataset {
//        public void setValue( double v, Comparable comparable, Comparable comparable1 ) {
//            super.set
//            super.data.setValue( new Double( v ), comparable, comparable1 );
//        }

        public void fireDatasetChanged() {
            super.fireDatasetChanged();
        }
    }

    public static SRRDataset createCategoryDataset( String rowKeyPrefix,
                                                    String[] columnLabels,
                                                    double[][] data ) {

        SRRDataset result = new SRRDataset();// {
        for( int r = 0; r < data.length; r++ ) {
            String rowKey = rowKeyPrefix + ( r + 1 );
            for( int c = 0; c < data[r].length; c++ ) {
                String columnKey = columnLabels[c];//columnKeyPrefix + (c + 1);
                result.addValue( new Double( data[r][c] ), rowKey, columnKey );
            }
        }
        return result;
    }

    public static JFreeChart createBarChart3DSRR( String title,
                                                  String categoryAxisLabel,
                                                  String valueAxisLabel,
                                                  CategoryDataset data,
                                                  PlotOrientation orientation,
                                                  boolean legend ) {

        CategoryAxis categoryAxis = new CategoryAxis3D( categoryAxisLabel );
        final ValueAxis valueAxis = new NumberAxis3D( valueAxisLabel );

        renderer = new StackedBarRenderer3D() {
            public void drawItem( Graphics2D graphics2D, CategoryItemRendererState categoryItemRendererState, Rectangle2D rectangle2D, CategoryPlot categoryPlot, CategoryAxis categoryAxis, ValueAxis valueAxis, CategoryDataset categoryDataset, int row, int column ) {
                Color color = lookupColor( row, column );
                super.setPaint( color );
                super.drawItem( graphics2D, categoryItemRendererState, rectangle2D, categoryPlot, categoryAxis, valueAxis, categoryDataset, row, column );
            }
        };
        renderer.setSeriesPaint( 1, Color.black );
        CategoryPlot plot = new CategoryPlot( data, categoryAxis, valueAxis, renderer );
        plot.setOrientation( orientation );
        plot.setForegroundAlpha( 0.75f );

        JFreeChart chart = new JFreeChart( title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend );
        return chart;
    }

    static Color KEColor = Color.red;
    static Color PEColor = Color.blue;
    static Color FrictionColor = Color.magenta;

    private static Color lookupColor( int row, int column ) {
        if( column == 0 ) {
            return KEColor;
        }
        else if( column == 1 ) {
            return PEColor;
        }
        else if( column == 2 ) {
            return FrictionColor;
        }
        else if( column == 3 && row == 0 ) {
            return KEColor;
        }
        else if( column == 3 && row == 1 ) {
            return PEColor;
        }
        else if( column == 3 && row == 2 ) {
            return FrictionColor;
        }
        return null;
    }

    private JFreeChart createChart( CategoryDataset dataset ) {

        JFreeChart chart = createBarChart3DSRR( "Energy Bar Chart", // chart title
                                                "", // domain axis label
                                                "Energy in Joules", // range axis label
                                                dataset, // data
                                                PlotOrientation.VERTICAL, // orientation
                                                false // include legend
        );

        CategoryPlot plot = chart.getCategoryPlot();

        CategoryItemRenderer renderer = plot.getRenderer();
//        System.out.println("renderer.getClass() = " + renderer.getClass());
//        System.out.println("plot.getClass() = " + plot.getClass());

//        renderer.setDefaultPaint( Color.blue );
        renderer.setBasePaint( Color.blue );
        renderer.setSeriesPaint( 0, Color.green );
        renderer.setSeriesPaint( 1, Color.red );
        renderer.setSeriesPaint( 2, Color.blue );
        CategoryAxis axis = plot.getDomainAxis();
        yaxis = plot.getRangeAxis();
        yaxis.setLabelFont( new Font( "Lucida", 0, 22 ) );
//        yaxis.setAutoRange(true);
        yaxis.setRange( -.1, 500 );

        axis.setTickLabelFont( new Font( "Lucida", 0, 22 ) );
//        axis.setTickLabelFont(new Font("dialog", 0, 14));
        axis.setVerticalCategoryLabels( true );
        chart.setBackgroundPaint( new Color( 165, 120, 220, 180 ) );
//        chart.setAntiAlias(false);
        return chart;
    }

    public void setRange( double min, double max ) {
        yaxis.setRange( min, max );
    }

    long lastPaintTime = 0;

    public synchronized void paint( Graphics2D g ) {
//        long currentTime = System.currentTimeMillis();
//        long dt = currentTime-lastPaintTime;

//        if (dt > 30) {
        if( chart != null && displayRectangle != null && visible ) {
            chart.draw( g, displayRectangle );
        }
//            chart.getCategoryPlot().getRenderer().
//            lastPaintTime=currentTime;
//        }

    }

    public synchronized void viewChanged( ModelViewTransform2d transform ) {
        Rectangle viewb = transform.getViewBounds();
        double width = viewb.getWidth() / 5.5;
        double height = viewb.getHeight() * .85;
        int inset = 20;
        int insetX = 15;
//        int width=200;
//        int height=600;
        int x = (int)( viewb.getWidth() - width ) - insetX;
        int y = inset;
        displayRectangle = new Rectangle2D.Double( x, y, width, height );
    }

    public void energyChanged( Car c, double ke, double pe, double fric ) {
        setValues( ke, pe, fric );
    }

    public Area getShape() {
        if( displayRectangle == null ) {
            return new Area();
        }
        return new Area( displayRectangle );
    }

    public void setAutoScale( boolean selected ) {
        this.autoRange = selected;
        if( selected ) {
            yaxis.setAutoRange( true );
        }
        else {
            yaxis.setAutoRange( false );
            yaxis.setRange( -1, 400 );
        }
        //        yaxis.setAutoRange(true);
//        yaxis.setRange(-.1, 400);
//        setAutoScale(selected);
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isAutoRange() {
        return autoRange;
    }

}
