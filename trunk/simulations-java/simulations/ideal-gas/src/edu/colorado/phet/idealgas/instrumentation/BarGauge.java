/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Nov 15, 2002
 * Time: 8:58:50 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.instrumentation;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class BarGauge extends AbstractGauge {

    private Rectangle2D.Double bar;
    private Rectangle2D.Double frame;
    private Color color;
    private boolean isVertical;
    private double offset;
    private double scale;
    private Point2D location;
    private double maxScreenLevel;
    private Color outlineColor = Color.black;


    /**
     * @param location
     * @param color
     * @param thickness
     * @param isVertical
     */
    public BarGauge( Point2D.Double location, double maxScreenLevel, Color color,
                     double thickness, boolean isVertical,
                     double minLevel, double maxLevel ) {
        this.location = location;
        this.maxScreenLevel = maxScreenLevel;
        this.color = color;
        double barWidth = 0;
        double barHeight = 0;
        double frameWidth = 0;
        double frameHeight = 0;
        scale = maxScreenLevel / ( maxLevel - minLevel );
        offset = minLevel * scale;
        this.isVertical = isVertical;
        if( isVertical ) {
            barWidth = thickness;
            frameWidth = thickness;
            frameHeight = maxScreenLevel;
        }
        else {
            barHeight = thickness;
            frameWidth = maxScreenLevel;
            frameHeight = thickness;
        }
        bar = new Rectangle2D.Double( location.getX(), location.getY(), barWidth, barHeight );
        frame = new Rectangle2D.Double( location.getX(), location.getY(), frameWidth, frameHeight );
    }

    /**
     * @return
     */
    public Rectangle getBounds() {
        return frame.getBounds();
    }

    /**
     * @param g
     */
    public void paint( Graphics2D g ) {
        Color oldColor = g.getColor();
        g.setColor( color );
        g.draw( bar );
        g.fill( bar );
        g.setColor( outlineColor );
        g.draw( frame );
        g.setColor( oldColor );
    }

    public void setOutlineColor( Color outlineColor ) {
        this.outlineColor = outlineColor;
    }

    public void setLevel( double level ) {

        double screenLevel = Math.min( Math.max( offset + scale * level, 0 ), maxScreenLevel );
        if( isVertical ) {
            bar.setRect( location.getX(),
                         location.getY() + maxScreenLevel - screenLevel,
                         bar.getWidth(),
                         screenLevel );
        }
        else {
            // todo: the hard-coded 1000 here is terrible!!!
            double newMaxX = bar.getMaxX() - level / 1000;
            bar.setRect( bar.getMinX(), bar.getMaxY(), newMaxX, bar.getMaxY() );
        }
    }

    public void setLocation( Point2D.Double location ) {
        this.location.setLocation( location );
    }
}
