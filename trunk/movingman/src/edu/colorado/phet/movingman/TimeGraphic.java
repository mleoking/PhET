/*PhET, 2004.*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:46:15 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class TimeGraphic implements InteractiveGraphic, Observer {
    private String timeStr;
    private MovingManModule module;
    private MMTimer recordingMMTimer;
    private int x;
    private int y;
    private Font f = new Font( "Lucida Sans", 0, 36 );
    private Color c = Color.black;
    private DecimalFormat decimalFormat = new DecimalFormat( "#0.00" );
    private FontRenderContext frc;

    public TimeGraphic( MovingManModule module, MMTimer recordingMMTimer, MMTimer playbackMMTimer, int x, int y ) {
        this.module = module;
        this.recordingMMTimer = recordingMMTimer;
        this.x = x;
        this.y = y;
        recordingMMTimer.addObserver( this );
        recordingMMTimer.updateObservers();
        playbackMMTimer.addObserver( this );
        update( recordingMMTimer, null );
    }

    public void paint( Graphics2D g ) {
        GraphicsState gs = new GraphicsState( g );
        this.frc = g.getFontRenderContext();
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.setFont( f );
        g.drawString( timeStr, x, y );
        gs.restoreGraphics();
    }

    public void update( Observable o, Object arg ) {
//        System.out.println( "recordingMMTimerer.getTime() = " + recordingMMTimerer.getTime() );
        MMTimer tx = (MMTimer)o;
        double scalarTime = tx.getTime();
        double seconds = scalarTime;// * timerDisplayScale; //TIMING
        Rectangle r = getShape();
        this.timeStr = decimalFormat.format( seconds ) + " seconds";
        repaint( r, getShape() );
    }

    private void repaint( Rectangle r, Rectangle r2 ) {
        if( r == null || r2 == null ) {
            return;
        }
        Rectangle union = r2.union( r );
//        System.out.println( "union = " + union );
//        module.getApparatusPanel().repaint( union );
        module.getApparatusPanel().repaint( union );
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return false;
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent event ) {
    }

    public void mouseDragged( MouseEvent event ) {
    }

    public void mouseMoved( MouseEvent e ) {
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

    public boolean contains( int x, int y ) {
        return false;
    }
}
