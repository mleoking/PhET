/*PhET, 2004.*/
package edu.colorado.phet.movingman.application;

import edu.colorado.phet.common.view.graphics.ObservingGraphic;
import edu.colorado.phet.movingman.common.GraphicsSetup;
import edu.colorado.phet.movingman.elements.BoxedPlot;
import edu.colorado.phet.movingman.elements.DataSeries;
import edu.colorado.phet.movingman.elements.Timer;
import sun.awt.font.FontDesignMetrics;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Observable;

/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 10:55:09 AM
 * Copyright (c) Jul 1, 2003 by Sam Reid
 */
public class ValueGraphic implements ObservingGraphic {
    private MovingManModule module;
    private Timer recordingTimer;
    private Timer playbackTimer;
    private DataSeries series;
    private String pre;
    private String unitsString;
    private DecimalFormat format = new DecimalFormat( "#0.00" );
    private String output;
    private Font font = new Font( "Lucida Sans", 0, 20 );
    private Color color = Color.black;
    private BoxedPlot offsetSource;
    private FontMetrics fontMetrics;
    private boolean visible = true;
    private String text;
    private int x;
    private int y;

    public ValueGraphic( MovingManModule module, Timer timer, Timer playbackTimer, DataSeries series, String pre, String units, int x, int y, BoxedPlot offsetSource ) {
        this.module = module;
        this.fontMetrics = module.getApparatusPanel().getFontMetrics( font );
        this.recordingTimer = timer;
        this.playbackTimer = playbackTimer;
        this.series = series;
        this.pre = pre;
        this.unitsString = units;
        this.offsetSource = offsetSource;
        timer.addObserver( this );
        playbackTimer.addObserver( this );
        this.fontMetrics = new FontDesignMetrics( font );
    }

    GraphicsSetup gs = new GraphicsSetup();

    public void paint( Graphics2D g ) {
        if( output != null && visible ) {
            gs.saveState( g );
//            g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

            g.setFont( font );
            g.setColor( color );
            g.drawString( text, x, y );
            gs.restoreState( g );
        }

    }

    public void update( Observable o, Object arg ) {
        int index = 0;
        if( module.isTakingData() ) {
            index = series.size() - 1;
        }
        else {
            double time = playbackTimer.getTime() + offsetSource.getxShift();
            index = (int)( time / MovingManModule.TIMER_SCALE );
        }
        if( series.indexInBounds( index ) ) {
            double value = series.pointAt( index );
            String valueString = format.format( value );
            if( valueString.equals( "-0.00" ) ) {
                valueString = "0.00";
            }
            String orig = this.output + "";
            Rectangle r1 = getShape();
            this.output = makeOutput( valueString );

            if( !orig.equals( this.output ) ) {
                this.text = output;
                repaint( r1, getShape() );
            }
        }
    }

    private Rectangle getShape() {
        return determineBounds();
    }

    protected Rectangle determineBounds() {
        if( text == null ) {
            return null;
        }
        int width = fontMetrics.stringWidth( text );//this ignores antialias and fractional metrics.
        int ascent = fontMetrics.getAscent();
        int descent = fontMetrics.getDescent();
        int leading = fontMetrics.getLeading();
        Rectangle bounds = new Rectangle( (int)this.x, (int)this.y - ascent + leading, width, ascent + descent + leading );
        return bounds;
    }

    private String makeOutput( String valueString ) {
        return pre + valueString + " " + unitsString;
    }

    private void repaint( Rectangle r1, Rectangle shape ) {
        Rectangle union = null;
        if( r1 == null && shape == null ) {
            return;
        }
        else if( r1 == null ) {
            union = shape;
        }
        else if( shape == null ) {
            union = r1;
        }
        else {
            union = r1.union( shape );
        }
        module.getApparatusPanel().paintSoon( union );
    }

    public void setPosition( int x, int y ) {
        this.x = x;
        this.y = y;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }
}
