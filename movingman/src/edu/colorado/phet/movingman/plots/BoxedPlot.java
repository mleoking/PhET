/*PhET, 2004.*/
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.common.view.graphics.BufferedGraphicForComponent;
import edu.colorado.phet.common.view.graphics.ObservingGraphic;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.common.GraphicsState;
import edu.colorado.phet.movingman.common.TransformJSlider;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Observable;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:54:39 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class BoxedPlot implements ObservingGraphic {
    private MovingManModule module;
    private DataSeries mh;
    private edu.colorado.phet.movingman.Timer recordingTimer;
    private Color color;
    private Stroke stroke;
    private Rectangle2D.Double inputBox;
    private BufferedGraphicForComponent buffer;
    private double xShift;
    boolean showValue = true;
    private GeneralPath path = new GeneralPath( GeneralPath.WIND_EVEN_ODD );
    private boolean started;
    private BoxToBoxInvertY2 transform;
    private boolean visible = true;
    private ArrayList rawData = new ArrayList( 100 );
    private ArrayList transformedData = new ArrayList( 100 );
    private float lastTime;
    private double dt;
    private Graphics2D bufferGraphic;
    private Rectangle2D outputBox;
    private TransformJSlider slider;
    private static final int SLIDER_OFFSET_X = 50;
    private boolean sliderVisible;

    public BoxedPlot( final MovingManModule module, DataSeries mh, edu.colorado.phet.movingman.Timer timer, Color color, Stroke stroke, Rectangle2D.Double inputBox, BufferedGraphicForComponent buffer, double xShift ) {
        this.module = module;
        this.mh = mh;
        this.recordingTimer = timer;
        this.color = color;
        this.stroke = stroke;
        this.inputBox = inputBox;
        this.buffer = buffer;
        this.xShift = xShift;
        mh.addObserver( this );
        mh.updateObservers();
        timer.addObserver( this );
        this.outputBox = new Rectangle2D.Double();
        slider = new TransformJSlider( -10, 10, 100 );
        slider.setOrientation( JSlider.VERTICAL );
        slider.setBackground( module.getBackgroundColor() );
        module.getApparatusPanel().setLayout( null );
        module.getApparatusPanel().add( slider );

        slider.setAlignmentY( 0 );
        slider.reshape( 10, 10, slider.getPreferredSize().width, slider.getPreferredSize().height );
        slider.setVisible( false );
    }

    public TransformJSlider getSlider() {
        return slider;
    }

    public void setSliderVisible( boolean visible ) {
        this.sliderVisible = visible;
        slider.setVisible( visible );
    }

    public void setSliderEnabled( boolean enabled ) {
        slider.setEnabled( enabled );
    }

    public void setInputBox( Rectangle2D.Double inputBox ) {
        this.inputBox = inputBox;
        setOutputBox( outputBox );//redraws everything.
        slider.setModelRange( inputBox.getY(), inputBox.getY() + inputBox.getHeight() );
    }

    public void setOutputBox( Rectangle2D outputBox ) {
        int x = (int)outputBox.getX();
        int y = (int)outputBox.getY();
        slider.setLocation( x - SLIDER_OFFSET_X, y );
        slider.setSize( slider.getWidth(), (int)outputBox.getHeight() );
        this.outputBox = outputBox;
        started = false;
        this.transform = new BoxToBoxInvertY2( inputBox, outputBox );
        //Update plot based on existing data
        path.reset();
        if( rawData.size() <= 1 ) {
            return;
        }
        Point2D.Double pt0 = (Point2D.Double)rawData.get( 1 );
        pt0 = transform.transform( pt0 );
        path.moveTo( (float)pt0.x, (float)pt0.y );
        for( int i = 2; i < rawData.size(); i++ ) {
            Point2D.Double data = (Point2D.Double)rawData.get( i );
            data = transform.transform( data );
            path.lineTo( (float)data.x, (float)data.y );
            started = true;
        }
    }

    GraphicsState graphicsState = new GraphicsState();
    GraphicsState bufferState = new GraphicsState();

    public void paint( Graphics2D g ) {
        if( started && visible ) {
            graphicsState.saveState( g );
            g.setStroke( stroke );
            g.setColor( color );
            g.setClip( getOutputBox() );
            g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
            g.draw( path );
            graphicsState.restoreState( g );
        }
    }

    public void update( Observable o, Object arg ) {
        if( transform == null ) {
            return;
        }
        float time = (float)recordingTimer.getTime();
        if( time == lastTime ) {
            return;
        }
        dt = time - lastTime;
        lastTime = time;
        if( mh.size() <= 1 ) {
            path.reset();
            started = false;
            rawData = new ArrayList();
        }
        else {
            Rectangle s1 = null;
            float position = (float)mh.getLastPoint();// * scale + yoffset;
            Point2D.Double pt = new Point2D.Double( time - xShift, position );
            rawData.add( pt );
            pt = transform.transform( pt );
            transformedData.add( pt );
            if( mh.size() >= 2 && !started ) {
                path.moveTo( (float)pt.x, (float)pt.y );
                started = true;
            }
            else if( started ) {
                if( visible && buffer.getImage() != null ) {
                    Point2D.Double a = (Point2D.Double)transformedData.get( transformedData.size() - 2 );
                    Point2D.Double b = (Point2D.Double)transformedData.get( transformedData.size() - 1 );
                    this.bufferGraphic = (Graphics2D)buffer.getImage().getGraphics();
                    bufferState.saveState( bufferGraphic );
                    bufferGraphic.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                    bufferGraphic.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
                    bufferGraphic.setColor( color );
                    bufferGraphic.setStroke( stroke );
                    bufferGraphic.setClip( outputBox );
                    Line2D.Double line = new Line2D.Double( b.x, b.y, a.x, a.y );
                    bufferGraphic.draw( line );
                    s1 = stroke.createStrokedShape( line ).getBounds();
                    bufferState.restoreState( bufferGraphic );
                }
                try {
                    Point2D curpt = path.getCurrentPoint();
                    path.lineTo( (float)pt.x, (float)pt.y );
                    Point2D pt2 = path.getCurrentPoint();
                    Line2D.Double newLine = new Line2D.Double( curpt, pt2 );

                    Rectangle s2 = stroke.createStrokedShape( newLine ).getBounds();
                    if( s1 != null ) {
                        s2 = s1.union( s2 );
                    }
                    repaint( s2 );
                }
                catch( IllegalPathStateException ipse ) {
                    ipse.printStackTrace();
                }
            }
        }
    }

    private void repaint( Rectangle rect ) {
        module.getApparatusPanel().repaint( rect );
    }

    public Rectangle2D getInputBounds() {
        return transform.getInputBounds();
    }

    public BoxToBoxInvertY2 getTransform() {
        return transform;
    }

    public double getxShift() {
        return xShift;
    }

    public Rectangle2D getOutputBox() {
        if( transform == null ) {
            return null;
        }
        return transform.getOutputBounds();
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
        if( visible ) {
            slider.setVisible( sliderVisible );
        }
        else {
            slider.setVisible( false );
        }
    }

    public void setRangeX( double time ) {
        Rectangle2D input = transform.getInputBounds();
        Rectangle2D.Double newInput = new Rectangle2D.Double( input.getX(), input.getY(), time, input.getHeight() );
        transform.setInputBounds( newInput );
        Rectangle2D outputBounds = transform.getOutputBounds();
        outputBounds = new Rectangle2D.Double( outputBounds.getX(), outputBounds.getY(), time, outputBounds.getHeight() );
        transform.setOutputBounds( outputBounds );
    }

    public void setShift( double x ) {
        this.xShift = x;
    }
}
