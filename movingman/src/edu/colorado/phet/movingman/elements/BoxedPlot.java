/*PhET, 2004.*/
package edu.colorado.phet.movingman.elements;

import edu.colorado.phet.common.math.transforms.BoxToBoxInvertY;
import edu.colorado.phet.common.view.graphics.BufferedGraphicForComponent;
import edu.colorado.phet.common.view.graphics.ObservingGraphic;
import edu.colorado.phet.movingman.application.MovingManModule;

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
    private Timer recordingTimer;
    private Color color;
    private Stroke stroke;
    private Rectangle2D.Double inputBox;
    private BufferedGraphicForComponent buffer;
    private double xShift;
    boolean showValue = true;
    private GeneralPath path = new GeneralPath( GeneralPath.WIND_EVEN_ODD );
    private boolean started;
    private BoxToBoxInvertY transform;
    private boolean visible = true;
    private ArrayList rawData = new ArrayList( 100 );
    private ArrayList transformedData = new ArrayList( 100 );
    private float lastTime;
    private double dt;
    private Graphics2D bufferGraphic;
    private Rectangle2D.Double outputBox;

    public BoxedPlot( MovingManModule module, DataSeries mh, Timer timer, Color color, Stroke stroke, Rectangle2D.Double inputBox, BufferedGraphicForComponent buffer, double xShift ) {
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
    }

    public void setInputBox( Rectangle2D.Double inputBox ) {
        this.inputBox = inputBox;
        setOutputBox( outputBox );//redraws everything.
    }

    public void setOutputBox( Rectangle2D.Double outputBox ) {
        this.outputBox = outputBox;
        started = false;
        this.transform = new BoxToBoxInvertY( inputBox, outputBox );
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

    public void paint( Graphics2D g ) {
        if( started && visible ) {
            Stroke origStroke = g.getStroke();
            g.setStroke( stroke );
            g.setColor( color );
            Shape origClip = g.getClip();
            g.setClip( getOutputBox() );
            g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g.draw( path );
            g.setClip( origClip );
            g.setStroke( origStroke );
        }
    }

    public void update( Observable o, Object arg ) {
//        Rectangle repaintRect = null;
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
                    Stroke origStroke = bufferGraphic.getStroke();
                    bufferGraphic.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                    bufferGraphic.setColor( color );
                    bufferGraphic.setStroke( stroke );
                    bufferGraphic.setClip( outputBox );
                    Line2D.Double line = new Line2D.Double( b.x, b.y, a.x, a.y );
                    bufferGraphic.draw( line );
                    s1 = stroke.createStrokedShape( line ).getBounds();
                    bufferGraphic.setStroke( origStroke );
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
                    paintImmediately( s2 );
                }
                catch( IllegalPathStateException ipse ) {
                    ipse.printStackTrace();
                }
            }
        }
    }

    private Rectangle expand( Rectangle repaintRect, int dx, int dy ) {
        return new Rectangle( repaintRect.x - dx / 2, repaintRect.y - dy / 2, repaintRect.width + dx, repaintRect.height + dy );
    }

    private void paintImmediately( Rectangle rect ) {
        module.getApparatusPanel().paintImmediately( rect );
    }

    public Rectangle2D.Double getInputBounds() {
        return transform.getInputBounds();
    }

    public BoxToBoxInvertY getTransform() {
        return transform;
    }

    public double getxShift() {
        return xShift;
    }

    public Rectangle2D.Double getOutputBox() {
        return transform.getOutputBounds();
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    public void setRangeX( double time ) {
        Rectangle2D.Double input = transform.getInputBounds();
        Rectangle2D.Double newInput = new Rectangle2D.Double( input.x, input.y, time, input.height );
        transform.setInputBounds( newInput );
        Rectangle2D.Double outputBounds = transform.getOutputBounds();
        outputBounds.width = time;
        transform.setOutputBounds( outputBounds );
    }

    public void setShift( double x ) {
        this.xShift = x;
    }
}
