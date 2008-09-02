/**
 * Class: ScatterEventGraphic
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 17, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.greenhouse.phetcommon.view.graphics.Graphic;

public class ScatterEventGraphic implements Graphic {
    private ScatterEvent se;
    private double radius;
    private Point2D.Double loc;
    Ellipse2D.Double ellipse = new Ellipse2D.Double();

    public ScatterEventGraphic( ScatterEvent se ) {
        this.se = se;
        se.addObserver( new Observer() {
            public void update( Observable o, Object arg ) {
                doUpdate();
            }
        } );
        doUpdate();
    }

    private void doUpdate() {
        radius = se.getRadius();
        this.loc = se.getLocation();
        ellipse.setFrameFromCenter( loc.x, loc.y, loc.x + radius, loc.y + radius );
    }

    public void paint( Graphics2D graphics2D ) {
        graphics2D.setColor( new Color( 180, 180, 0 ) );
//        graphics2D.setColor(Color.yellow);
//        graphics2D.drawArc();
        graphics2D.fill( ellipse );
    }
}
