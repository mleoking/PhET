/*PhET, 2004.*/
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.common.view.graphics.BufferedGraphicForComponent;
import edu.colorado.phet.common.view.graphics.ObservingGraphic;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.movingman.MMTimer;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.common.TransformJSlider;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:54:39 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class BoxedPlot implements ObservingGraphic {
    private String title;
    private MovingManModule module;
    private DataSeries dataSeries;
    private MMTimer timer;
    private Color color;
    private Stroke stroke;
    private BufferedGraphicForComponent buffer;
    private double xShift;

    private boolean visible = true;
    private TransformJSlider slider;
    private Chart chart;
    private static final int SLIDER_OFFSET_X = 50;
    private DataSet dataSet;
    private float lastTime;
    private Font font = new Font( "Lucida Sans", Font.BOLD, 14 );
    private Font titleFont = new Font( "Lucida Sans", Font.BOLD, 16 );

    public BoxedPlot( String title, final MovingManModule module, DataSeries dataSeries, MMTimer timer, Color color, Stroke stroke, Rectangle2D.Double inputBox, BufferedGraphicForComponent buffer, double xShift ) {
        this.title = title;
        this.module = module;
        this.dataSeries = dataSeries;
        this.timer = timer;
        this.color = color;
        this.stroke = stroke;
        this.buffer = buffer;
        this.xShift = xShift;
        slider = new TransformJSlider( -10, 10, 100 );
        setupSlider();
        chart = new Chart( module.getApparatusPanel(), new Range2D( inputBox ), new Rectangle( 0, 0, 100, 100 ) );
        chart.setBackground( createGradient() );
        dataSet = new DataSet();
        setInputRange( inputBox );
        timer.addObserver( this );
        chart.getHorizontalTicks().setVisible( false );
        chart.getHorizonalGridlines().setMinorGridlinesVisible( false );
        chart.getHorizonalGridlines().setMajorGridlinesColor( Color.darkGray );
        chart.getVerticalGridlines().setMajorGridlinesColor( Color.darkGray );
        chart.getXAxis().setMajorTickFont( font );
        chart.getVerticalTicks().setVisible( false );
        chart.getYAxis().setMinorTicksVisible( false );
        chart.getYAxis().setMajorTickFont( font );
        chart.getVerticalGridlines().setMinorGridlinesVisible( false );
        chart.getXAxis().setMajorGridlines( new double[]{2, 4, 6, 8, 10, 12, 14, 16, 18, 20} ); //to ignore the 0.0
        chart.getXAxis().setStroke( new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{6, 6}, 0 ) );
    }

    private Paint createGradient() {
        return Color.yellow;
//        Rectangle rect = chart.getViewBounds();
//        Color topleft = Color.yellow;
//        System.out.println( "topleft = " + topleft );
////        Color bottomRight=new Color( 1.0f,.8f,.6f);
////        Color bottomRight=new Color( 255,255,250);
////        Color bottomRight=new Color( 60,90,250);
//        Paint g = new GradientPaint( rect.x, rect.y, topleft, rect.x + rect.width, rect.y + rect.height, bottomRight );
//
//        return g;
    }

    private void setupSlider() {
        slider.setOrientation( JSlider.VERTICAL );
        slider.setBackground( module.getBackgroundColor() );
        module.getApparatusPanel().setLayout( null );
        module.getApparatusPanel().add( slider );

        slider.setAlignmentY( 0 );
        slider.reshape( 10, 10, slider.getPreferredSize().width, slider.getPreferredSize().height );
        slider.setVisible( false );
    }

    public void paint( Graphics2D g ) {
        if( visible ) {
            chart.paint( g );
            Point pt = chart.getTransform().modelToView( 15, 0 );
            pt.y -= 3;
            PhetTextGraphic ptt = new PhetTextGraphic( module.getApparatusPanel(), new Font( "Lucida Sans", Font.BOLD, 16 ), "Time", Color.red, pt.x, pt.y );
            ptt.paint( g );
            Rectangle bounds = ptt.getBounds();
            Point2D tail = RectangleUtils.getRightCenter( bounds );
            tail = new Point2D.Double( tail.getX() + 5, tail.getY() );
            Point2D tip = new Point2D.Double( tail.getX() + 30, tail.getY() );
            Arrow arrow = new Arrow( tail, tip, 9, 9, 5 );
            PhetShapeGraphic psg = new PhetShapeGraphic( module.getApparatusPanel(), arrow.getShape(), Color.red, new BasicStroke( 1 ), Color.black );
            psg.paint( g );

            PhetTextGraphic ptg = new PhetTextGraphic( module.getApparatusPanel(), titleFont, title, color, 0, 0 );
            Rectangle rect = ptg.getBounds();

        }
    }

    public void setSliderVisible( boolean sliderVisible ) {
        slider.setVisible( sliderVisible );
    }

    public double getxShift() {
        return xShift;
    }

    public TransformJSlider getSlider() {
        return slider;
    }

    public ModelViewTransform2D getTransform() {
        return chart.getTransform();
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    public void setShift( double xShift ) {
        this.xShift = xShift;
    }

    public void setInputRange( Rectangle2D.Double inputBox ) {
        Range2D range = new Range2D( inputBox );
        chart.setRange( range );
    }

    public void setPaintYLines( double[] lines ) {
        double[] full = new double[lines.length * 2 + 1];
        for( int i = 0; i < lines.length; i++ ) {
            full[i] = lines[i];
            full[full.length - 1 - i] = -lines[i];
        }
        full[lines.length] = 0;

        double[] half = new double[lines.length * 2];
        for( int i = 0; i < lines.length; i++ ) {
            half[i] = lines[i];
            half[half.length - 1 - i] = -lines[i];
        }
        chart.getHorizonalGridlines().setMajorGridlines( half );
        chart.getVerticalTicks().setMajorGridlines( full );
        chart.getYAxis().setMajorGridlines( full );
    }

    public void setViewBounds( Rectangle rectangle ) {
        chart.setViewBounds( rectangle );
        slider.setLocation( rectangle.x - SLIDER_OFFSET_X, rectangle.y );
        slider.setSize( slider.getWidth(), (int)rectangle.getHeight() );
        chart.setBackground( createGradient() );
    }

    public void update( Observable o, Object arg ) {
        float time = (float)timer.getTime();
        if( time == lastTime ) {
            return;
        }
        lastTime = time;
        if( dataSeries.size() <= 1 ) {
            dataSet.clear();
        }
        else {
            float position = (float)dataSeries.getLastPoint();// * scale + yoffset;
            Point2D.Double pt = new Point2D.Double( time - xShift, position );
            dataSet.addPoint( pt );
            if( visible && buffer.getImage() != null && dataSet.size() >= 2 ) {
                int size = dataSet.size();
                Point2D a = chart.getTransform().modelToView( dataSet.pointAt( size - 2 ) );
                Point2D b = chart.getTransform().modelToView( dataSet.pointAt( size - 1 ) );
                Line2D.Double line = new Line2D.Double( a, b );
                Graphics2D g2 = module.getBackground().getImage().createGraphics();
                g2.setStroke( new BasicStroke( 2 ) );
                g2.setColor( color );
                g2.setClip( chart.getViewBounds() );
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
                g2.draw( line );
                Shape shape = stroke.createStrokedShape( line );
                module.getApparatusPanel().repaint( shape.getBounds() );
            }
        }
    }
}
