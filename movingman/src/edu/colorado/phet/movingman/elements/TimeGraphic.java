/*PhET, 2004.*/
package edu.colorado.phet.movingman.elements;

import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.ObservingGraphic;
import edu.colorado.phet.movingman.application.MovingManModule;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Observable;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:46:15 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class TimeGraphic implements InteractiveGraphic, ObservingGraphic {
    String timeStr;
    private MovingManModule module;
    Timer recordingTimer;
    int x;
    int y;
    Font f = new Font( "Lucida Sans", 0, 36 );
    Color c = Color.black;
    DecimalFormat decimalFormat = new DecimalFormat( "#0.00" );
    private FontRenderContext frc;

    public TimeGraphic( MovingManModule module, Timer recordingTimer, Timer playbackTimer, int x, int y ) {
        this.module = module;
        this.recordingTimer = recordingTimer;
        this.x = x;
        this.y = y;
        recordingTimer.addObserver( this );
        recordingTimer.updateObservers();
        playbackTimer.addObserver( this );
        update( recordingTimer, null );
    }

    public void paint( Graphics2D g ) {
        this.frc = g.getFontRenderContext();
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.setFont( f );
        g.drawString( timeStr, x, y );
        g.setColor( Color.green );
        g.setStroke( new BasicStroke() );
        g.draw( getShape() );
    }

    public void update( Observable o, Object arg ) {
//        System.out.println( "recordingTimer.getTime() = " + recordingTimer.getTime() );
        Timer tx = (Timer)o;
        double scalarTime = tx.getTime();
        double seconds = scalarTime;// * timerDisplayScale; //TIMING
        Rectangle r = getShape();
        this.timeStr = decimalFormat.format( seconds ) + " seconds";
        paintImmediately( r, getShape() );
    }

    private void paintImmediately( Rectangle r, Rectangle r2 ) {
        if( r == null || r2 == null ) {
            return;
        }
        Rectangle union = r2.union( r );
//        System.out.println( "union = " + union );
        module.getApparatusPanel().paintImmediately( union );
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return false;
    }

    public void mousePressed( MouseEvent event ) {
    }

    public void mouseDragged( MouseEvent event ) {
    }

    public void mouseReleased( MouseEvent event ) {
    }

    public void mouseEntered( MouseEvent event ) {
    }

    public void mouseExited( MouseEvent event ) {
    }

    public Rectangle getShape() {
        if( frc == null ) {
            return null;
        }
        else {
            Rectangle2D bound = f.getStringBounds( this.timeStr, frc );
            Rectangle2D.Double out = new Rectangle2D.Double( bound.getX(), bound.getY(), bound.getWidth(), bound.getHeight() );
            out.x = x;
            out.y = y - out.height;
            return new Rectangle( (int)out.x, (int)out.y, (int)out.width, (int)out.height );
        }
    }

}
