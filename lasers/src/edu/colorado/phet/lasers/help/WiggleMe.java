/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.help;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * WiggleMe
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class WiggleMe {
    Random random = new Random();
    Point2D location;
    Vector2D v;
    double sInit = 1;
    Point2D target;
    PhetGraphic graphic;
    ArrayList nodes = new ArrayList();

    public WiggleMe( Rectangle bounds, AbstractClock clock, Point2D target, PhetGraphic graphic ) {
        this.graphic = graphic;
        clock.addClockTickListener( new TickListener() );
        this.target = target;

        nodes.add( new Node( target, 1 ) );

        int entrySide = random.nextInt( 4 );
        double x = 0, y = 0;
        switch( entrySide ) {
            case 0:
                x = bounds.getMinX();
                y = bounds.getMinY() + bounds.getHeight() * random.nextDouble();
                break;
            case 1:
                x = bounds.getMaxX();
                y = bounds.getMinY() + bounds.getHeight() * random.nextDouble();
                break;
            case 2:
                y = bounds.getMinY();
                x = bounds.getMinX() + bounds.getWidth() * random.nextDouble();
                break;
            case 3:
                y = bounds.getMaxY();
                x = bounds.getMinX() + bounds.getWidth() * random.nextDouble();
                break;
        }
        location = new Point2D.Double( x, y );
        v = new Vector2D.Double( x - bounds.getX() + bounds.getWidth() / 2,
                                 y - bounds.getY() + bounds.getHeight() / 2 );
        v.normalize().scale( sInit );

        update( 0 );
    }

    private void update( double dt ) {
        double x = location.getX() + v.getX() * dt;
        double y = location.getY() + v.getY() * dt;

        double f = 0.00001;
        double fx = 0, fy = 0;
        for( int i = 0; i < nodes.size(); i++ ) {
            Node node = (Node)nodes.get( i );
            Vector2D vf = node.getForce( location );
            fx += vf.getX();
            fy += vf.getY();
        }

        v.setComponents( v.getX() - f * fx,
                         v.getY() - f * fy );
//        v.setComponents( v.getX() - f *(location.getX() - target.getX()),
//                         v.getY() - f * (location.getY() - target.getY()));
        location.setLocation( x, y );

        System.out.println( "location = " + location );

        graphic.setLocation( (int)location.getX(), (int)location.getY() );
        graphic.setBoundsDirty();
        graphic.repaint();
    }

    class TickListener implements ClockTickListener {
        public void clockTicked( ClockTickEvent event ) {
            update( event.getDt() );
        }
    }

    class Node {
        Point2D location;
        double force;

        Node( Point2D location, double force ) {
            this.location = location;
            this.force = force;
        }

        Vector2D getForce( Point2D p ) {
            Vector2D f = new Vector2D.Double( p.getX() - location.getX() * force,
                                              p.getY() - location.getY() * force );
            return f;
        }
    }
}
