/*PhET, 2004.*/
package edu.colorado.phet.movingman.application;

import edu.colorado.phet.common.view.graphics.ObservingGraphic;
import edu.colorado.phet.movingman.common.PhetTextGraphic;
import edu.colorado.phet.movingman.elements.BoxedPlot;
import edu.colorado.phet.movingman.elements.DataSeries;
import edu.colorado.phet.movingman.elements.Timer;

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
    private boolean visible = true;
//    private HTMLGraphic htmlGraphic;
    private PhetTextGraphic htmlGraphic;

    public ValueGraphic( MovingManModule module, Timer timer, Timer playbackTimer, DataSeries series, String pre, String units, int x, int y, BoxedPlot offsetSource ) {
        this.module = module;
        this.recordingTimer = timer;
        this.playbackTimer = playbackTimer;
        this.series = series;
        this.pre = pre;
        this.unitsString = units;
        this.offsetSource = offsetSource;
        timer.addObserver( this );
        playbackTimer.addObserver( this );
//        htmlGraphic = new HTMLGraphic( "", font, color, x, y );
        htmlGraphic = new PhetTextGraphic( module.getApparatusPanel(), font, "", color, x, y );
    }

    public void paint( Graphics2D g ) {
        if( output != null && visible ) {
//            htmlGraphic.paint( g );
            g.setFont( font );
            g.setColor( color );
            g.drawString( htmlGraphic.getText(), htmlGraphic.getX(), htmlGraphic.getY() );
        }
        Shape s1 = getShape();
        g.setStroke( new BasicStroke() );
        g.setColor( Color.blue );
        g.draw( s1 );
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
                htmlGraphic.setText( output );
                paintImmediately( r1, getShape() );
            }
        }
    }

    private String makeOutput( String valueString ) {
        return pre + valueString + " " + unitsString;
    }

    private void paintImmediately( Rectangle r1, Rectangle shape ) {
        Rectangle union = r1.union( shape );
        module.getApparatusPanel().paintImmediately( union );
    }

    private Rectangle getShape() {
        return htmlGraphic.getBounds();
    }

    public void setPosition( int x, int y ) {
        htmlGraphic.setPosition( x, y );
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }
}
