/*PhET, 2004.*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.movingman.common.ObservingGraphic;
import edu.colorado.phet.movingman.plots.DataSeries;
import edu.colorado.phet.movingman.plots.MMPlot;

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
    private MMTimer recordingMMTimer;
    private MMTimer playbackMMTimer;
    private DataSeries series;
    private String pre;
    private String unitsString;
    private DecimalFormat format = new DecimalFormat( "#0.00" );
    private String output;
    private Font font = new Font( "Lucida Sans", 0, 20 );
    private Color color = Color.black;
    private MMPlot offsetSource;
    private FontMetrics fontMetrics;
    private boolean visible = true;
    private String text;
    private int x;
    private int y;
    private double value;

    public ValueGraphic( MovingManModule module, MMTimer MMTimer, MMTimer playbackMMTimer, DataSeries series, String pre, String units, int x, int y, MMPlot offsetSource ) {
        this.module = module;
        this.fontMetrics = module.getApparatusPanel().getFontMetrics( font );
        this.recordingMMTimer = MMTimer;
        this.playbackMMTimer = playbackMMTimer;
        this.series = series;
        this.pre = pre;
        this.unitsString = units;
        this.offsetSource = offsetSource;
        MMTimer.addObserver( this );
        playbackMMTimer.addObserver( this );
        this.fontMetrics = module.getApparatusPanel().getFontMetrics( font );
    }

    public void paint( Graphics2D g ) {
        if( output != null && visible ) {
            GraphicsState gs = new GraphicsState( g );
            g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g.setFont( font );
            g.setColor( color );
            g.drawString( text, x, y );
            gs.restoreGraphics();
        }

    }

    public void update( Observable o, Object arg ) {
        int index = 0;
        if( module.isTakingData() ) {
            index = series.size() - 1;
        }
        else {
            double time = playbackMMTimer.getTime() + offsetSource.getxShift();
            index = (int)( time / MovingManModel.TIMER_SCALE );
        }
        if( series.indexInBounds( index ) ) {
            value = series.pointAt( index );
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
        module.getApparatusPanel().repaint( union );
    }

    public void setPosition( int x, int y ) {
        this.x = x;
        this.y = y;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    public double getValue() {
        return value;
    }
}
