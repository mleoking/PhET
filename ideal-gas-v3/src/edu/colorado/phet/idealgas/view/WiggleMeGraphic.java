/**
 * Class: WiggleMeGraphic
 * Package: edu.colorado.phet.idealgas.view
 * Author: Another Guy
 * Date: Sep 27, 2004
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class WiggleMeGraphic implements Graphic, Runnable {

    private Runnable loop;
    private Component component;
    Point2D.Double current = new Point2D.Double();
    String family = "Sans Serif";
    int style = Font.BOLD;
    int size = 16;
    Font font = new Font( family, style, size );
    private Point2D.Double startLocation;

    public WiggleMeGraphic( Component component, Point2D.Double startLocation ) {
        this.component = component;
        loop = this;
        this.startLocation = startLocation;
        current.setLocation( startLocation );
    }

    public void kill() {
        loop = null;
    }

    public void run() {
        double cnt = 0;
        while( loop == this ) {
            try {
                Thread.sleep( 50 );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
            cnt += 0.2;
            current.setLocation( startLocation.getX() + 30 * Math.cos( cnt ),
                                 startLocation.getY() + 15 * Math.sin( cnt ));
            component.repaint();
        }
    }

    public void paint( Graphics2D g ) {
        RenderingHints orgRH = g.getRenderingHints();
        GraphicsUtil.setAntiAliasingOn( g );
        g.setFont( font );
        g.setColor( new Color( 0, 100, 0 ) );
        String s1 = SimStrings.get( "WiggleMe.Pump_the" );
        String s2 = SimStrings.get( "WiggleMe.handle!" );
        g.drawString( s1, (int)current.getX(), (int)current.getY() - g.getFontMetrics( font ).getHeight() );
        g.drawString( s2, (int)current.getX(), (int)current.getY() );
        Point2D.Double arrowTail = new Point2D.Double( current.getX() + SwingUtilities.computeStringWidth( g.getFontMetrics( font ), s2 ) + 10,
                                                       (int)current.getY() - g.getFontMetrics( font ).getHeight() / 2 );
        Point2D.Double arrowTip = new Point2D.Double( arrowTail.getX() + 15, arrowTail.getY() + 12 );
        Arrow arrow = new Arrow( arrowTail, arrowTip, 6, 6, 2, 100, false );
        g.fill( arrow.getShape() );
        g.setRenderingHints( orgRH );
    }
}
