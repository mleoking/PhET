/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Nov 15, 2002
 * Time: 8:58:50 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.instrumentation;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;

public class BarGauge extends AbstractGauge {

    private Rectangle2D.Float bar;
    private Rectangle2D.Float frame;
    private Color color;
    private float  level;
    private boolean isVertical;
    private float  offset;
    private float  scale;
    private Point2D location;
    private float  maxScreenLevel;


    /**
     *
     * @param location
     * @param color
     * @param thickness
     * @param isVertical
     */
    public BarGauge( Point2D.Float location, float  maxScreenLevel, Color color,
                     float  thickness, boolean isVertical,
                     float  minLevel, float  maxLevel ) {
        this.location = location;
        this.maxScreenLevel = maxScreenLevel;
        this.color = color;
        float  barWidth = 0;
        float  barHeight = 0;
        float  frameWidth = 0;
        float  frameHeight = 0;
        scale = maxScreenLevel / ( maxLevel - minLevel );
        offset = minLevel * scale;
        this.isVertical = isVertical;
        if( isVertical ) {
            barWidth = thickness;
            frameWidth = thickness;
            frameHeight = maxScreenLevel;
        } else {
            barHeight = thickness;
            frameWidth = maxScreenLevel;
            frameHeight = thickness;
        }
        bar = new Rectangle2D.Float( (float)location.getX(), (float)location.getY(), barWidth, barHeight );
        frame = new Rectangle2D.Float( (float)location.getX(), (float)location.getY(), frameWidth, frameHeight );
    }

    /**
     *
     * @param g
     */
    public void paint( Graphics2D g ) {
        Color oldColor = g.getColor();
        g.setColor( color );
        g.draw( bar );
        g.fill( bar );
        g.setColor( Color.black );
        g.draw( frame );
        g.setColor( oldColor );
    }

    /**
     *
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
    }

    public void setLevel( float  level ) {
        float  screenLevel = Math.min( Math.max( offset + scale * level, 0 ), maxScreenLevel );
        if( isVertical ) {
            bar.setRect( location.getX(),
                         location.getY() + maxScreenLevel - screenLevel,
                         bar.getWidth(),
                         screenLevel );
        } else {
            float  newMaxX = (float)bar.getMaxX() - level / 1000;
            bar.setRect( bar.getMinX(), bar.getMaxY(), newMaxX, bar.getMaxY() );
        }
    }
}
