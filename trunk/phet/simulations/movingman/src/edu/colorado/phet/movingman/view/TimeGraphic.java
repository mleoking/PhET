/*PhET, 2004.*/
package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.util.DefaultDecimalFormat;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.movingman.MMFontManager;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.model.MMTimer;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:46:15 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class TimeGraphic extends PhetGraphic {
    private String timeStr;
    private MovingManModule module;
//    private MMTimer recordingMMTimer;
    private int x;
    private int y;
    private Font f = MMFontManager.getFontSet().getTimeFont();
//    private DecimalFormat decimalFormat = new DecimalFormat( "#0.00" );
    private DefaultDecimalFormat decimalFormat = new DefaultDecimalFormat( "#0.00" );
    private FontRenderContext frc;
    private ApparatusPanel apparatusPanel;

    public TimeGraphic( MovingManModule module, MovingManApparatusPanel apparatusPanel,
                        final MMTimer recordingMMTimer, final MMTimer playbackMMTimer, int x, int y ) {
        super( apparatusPanel );
        this.apparatusPanel = apparatusPanel;
        this.module = module;
//        this.recordingMMTimer = recordingMMTimer;
        this.x = x;
        this.y = y;
        MMTimer.Listener recordListener = new MMTimer.Listener() {
            public void timeChanged() {
                update( recordingMMTimer );
            }
        };
        MMTimer.Listener playListener = new MMTimer.Listener() {
            public void timeChanged() {
                update( playbackMMTimer );
            }
        };
        recordingMMTimer.addListener( recordListener );
//        Observer( this );
//        recordingMMTimer.updateObservers();
        playbackMMTimer.addListener( playListener );
        update( recordingMMTimer );
    }

    public void paint( Graphics2D g ) {
        GraphicsState gs = new GraphicsState( g );
        this.frc = g.getFontRenderContext();
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.setFont( f );
        g.drawString( timeStr, x, y );
        gs.restoreGraphics();
    }

    public void update( MMTimer tx ) {
//        System.out.println( "recordingMMTimerer.getTime() = " + recordingMMTimerer.getTime() );
//        MMTimer tx = (MMTimer)o;
        double scalarTime = tx.getTime();
        double seconds = scalarTime;// * timerDisplayScale; //TIMING
        Rectangle r = getShape();
        this.timeStr = decimalFormat.format( seconds ) + " " + SimStrings.get( "TimeGraphic.SecondsText" );
        repaint( r, getShape() );
    }

    private void repaint( Rectangle r, Rectangle r2 ) {
        if( r == null || r2 == null ) {
            return;
        }
        Rectangle union = r2.union( r );
//        System.out.println( "union = " + union );
//        module.getApparatusPanel().repaint( union );
        apparatusPanel.repaint( union );
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

    protected Rectangle determineBounds() {
        return getShape();
    }
}
