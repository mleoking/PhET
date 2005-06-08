/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph.tests;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.theramp.common.scenegraph.FillGraphic;
import edu.colorado.phet.theramp.common.scenegraph.GraphicLayerNode;
import edu.colorado.phet.theramp.common.scenegraph.SceneGraphPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * User: Sam Reid
 * Date: Jun 7, 2005
 * Time: 9:14:13 AM
 * Copyright (c) Jun 7, 2005 by Sam Reid
 */

public class BoidTest extends SceneGraphPanel {
    ArrayList boids = new ArrayList();

    static class Boid extends SimpleObservable {
        Particle state;
        BoidBrain boidBrain;
        private double maxMagnitude = 0.2;
        private BoidTest boidTest;
//        private double mass = 1.0;
        private double mass = 0.2;

        public Boid( BoidTest boidTest ) {
            this( boidTest, new BoidTest.DefaultBoidBrain() );
        }

        public Boid( BoidTest boidTest, BoidBrain boidBrain ) {
            this.boidTest = boidTest;
            this.boidBrain = boidBrain;
            state = new Particle();
        }

        public void setLocation( double x, double y ) {
            state.setPosition( x, y );
            notifyObservers();
        }

        public void step( ClockTickEvent event ) {
            Vector2D force = boidBrain.getForce( this, boidTest );
            if( force.getMagnitude() > maxMagnitude ) {
                force = force.scale( maxMagnitude / force.getMagnitude() );
            }
            state.setAcceleration( force.scale( 1.0 / mass ) );
            state.stepInTime( event.getDt() );
            notifyObservers();
        }

        public Point2D getLocation() {
            return state.getPosition();
        }

        public double distanceTo( Boid a ) {
            return new Vector2D.Double( a.getLocation(), this.getLocation() ).getMagnitude();
        }

        public double getAngle() {
            return state.getVelocity().getAngle();
        }
    }

    static interface BoidBrain {
        Vector2D getForce( Boid boid, BoidTest boidTest );
    }

    public BoidTest() {
        for( int i = 0; i < 40; i++ ) {
            addBoid( newBoid() );
        }
        getGraphic().setAntialias( true );
    }

    private Boid newBoid() {
        Boid b = new Boid( this );
        b.setLocation( Math.random() * 400, Math.random() * 400 );
        return b;
    }

    private void addBoid( Boid boid ) {
        boids.add( boid );
        BoidTest.BoidGraphic boidGraphic = new BoidGraphic( boid );
        addGraphic( boidGraphic );
    }

    private void tick( ClockTickEvent event ) {
        for( int i = 0; i < boids.size(); i++ ) {
            Boid boid = (Boid)boids.get( i );
            boid.step( event );
        }

        viewAll();
//        viewBoid( 0 );
    }

    private void viewBoid( int i ) {

        Boid b0 = (Boid)boids.get( i );
        Rectangle viewport = new Rectangle();
        viewport.setFrameFromCenter( b0.getLocation(), new Point2D.Double( b0.getLocation().getX() + getWidth(), b0.getLocation().getY() + getHeight() ) );
        getGraphic().setTransformViewport( new Rectangle( getWidth(), getHeight() ), viewport );
//        getGraphic().preConcatenate( AffineTransform.getRotateInstance( -b0.getAngle(), getWidth() / 2, getHeight() / 2 ) );
    }

    private void viewAll() {

        Rectangle2D bounds = getBoidBounds();
        bounds = fixAspectRatio( bounds );
        bounds = RectangleUtils.expandRectangle2D( bounds, 50, 50 );
        getGraphic().setTransformViewport( new Rectangle( getWidth(), getHeight() ), bounds );
    }

    private Rectangle2D fixAspectRatio( Rectangle2D bounds ) {
        double w = bounds.getWidth();
        double h = bounds.getHeight();
        double length = Math.max( w, h );
        Rectangle2D.Double rect = new Rectangle2D.Double();
        Point2D center = RectangleUtils.getCenter2D( bounds );

        rect.setFrameFromCenter( center, new Point2D.Double( center.getX() + length / 2, center.getY() + length / 2 ) );
        return rect;
    }

    public Rectangle2D getBoidBounds() {
        Rectangle2D r = null;
        for( int i = 0; i < boids.size(); i++ ) {
            Boid boid = (Boid)boids.get( i );
            if( i == 0 ) {
                r = new Rectangle2D.Double( boid.getLocation().getX(), boid.getLocation().getY(), 0, 0 );
            }
            else {
                r = r.createUnion( new Rectangle2D.Double( boid.getLocation().getX(), boid.getLocation().getY(), 0, 0 ) );
            }
        }
        return r;
    }

    public static class DefaultBoidBrain implements BoidBrain {
        public Vector2D getForce( Boid boid, BoidTest boidTest ) {
            Boid[] nn = boidTest.getNearestNeighbors( boid, 4 );
            MathUtil.Average x = new MathUtil.Average();
            MathUtil.Average y = new MathUtil.Average();
            for( int i = 0; i < nn.length; i++ ) {
                x.update( nn[i].getLocation().getX() );
                y.update( nn[i].getLocation().getY() );
            }
            Point2D.Double a = new Point2D.Double( x.value(), y.value() );
            return new Vector2D.Double( boid.getLocation(), a );
        }
    }

    private Boid[] getNearestNeighbors( final Boid defaultBoidBrain, int num ) {
        ArrayList all = new ArrayList( boids );
        all.remove( defaultBoidBrain );
        Collections.sort( all, new Comparator() {
            public int compare( Object o1, Object o2 ) {
                Boid a = (Boid)o1;
                Boid b = (Boid)o2;
                return Double.compare( defaultBoidBrain.distanceTo( a ), defaultBoidBrain.distanceTo( b ) );

            }
        } );
        Boid[] nn = new Boid[num];
        for( int i = 0; i < nn.length; i++ ) {
            nn[i] = (Boid)all.get( i );
        }
        return nn;
    }

    public static class BoidGraphic extends GraphicLayerNode {
        private Boid boid;
        private ArrayList history = new ArrayList();
//        private int historySize = 3;
//        private int historySize = 7;
        private int historySize = 14;
        private ArrayList segments = new ArrayList();
//        public FillGraphic fillGraphic;

        public BoidGraphic( Boid boid ) {
            this.boid = boid;
            Color myColor = new Color( (float)Math.random(), (float)Math.random(), (float)Math.random() );
//            fillGraphic = new FillGraphic( new Ellipse2D.Double( -6, -6, 12, 12 ), myColor );
//            addGraphic( fillGraphic );

            Color tailColor = myColor;
            double dr = Math.random();
            double dg = Math.random();
            double db = Math.random();
            for( int i = 0; i < historySize; i++ ) {
                Ellipse2D.Double shape = new Ellipse2D.Double( -5, -5, 10, 10 );
                shape.setFrameFromCenter( 0, 0, i + 1, i + 1 );
//                shape.setFrameFromCenter( 0, 0, historySize - i, historySize - i );

                FillGraphic o = new FillGraphic( shape, tailColor );

                addGraphic( o );
                segments.add( o );

                tailColor = increaseTailColor( tailColor, dr, dg, db );
            }

            boid.addObserver( new SimpleObserver() {
                public void update() {
                    BoidGraphic.this.update();
                }
            } );
            update();
        }

        private Color increaseTailColor( Color tailColor, double dr, double dg, double db ) {
            Color c = new Color( increase( tailColor.getRed(), dr ),
                                 increase( tailColor.getGreen(), dg ), increase( tailColor.getBlue(), db ) );
            return c;
        }

        private int increase( int red, double dr ) {
            int dx = (int)( dr * 30 );
            red += dx;
            if( red > 255 ) {
                return 255;
            }
            if( red < 0 ) {
                return 0;
            }
            return red;
        }

        private void update() {
//            fillGraphic.setLocation( boid.getLocation() );

            history.add( new Point2D.Double( boid.getLocation().getX(), boid.getLocation().getY() ) );
            while( history.size() > historySize ) {
                history.remove( 0 );
            }
            for( int i = 0; i < history.size(); i++ ) {
                FillGraphic graphic = (FillGraphic)segments.get( i );
                graphic.setLocation( (Point2D)history.get( i ) );
            }
        }
    }

    public static void main( String[] args ) {
        AbstractClock clock = new SwingTimerClock( 1, 30 );

        final BoidTest boidTest = new BoidTest();
        JFrame jf = new JFrame();
        jf.setSize( 600, 600 );
        jf.setContentPane( boidTest );
        jf.setVisible( true );
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        boidTest.getGraphic().setTransformViewport( new Rectangle( 0, 0, boidTest.getWidth(), boidTest.getHeight() ),
                                                    new Rectangle2D.Double( -100, -100, 800, 800 ) );
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                boidTest.tick( event );
                boidTest.repaint();
            }
        } );
        clock.start();
    }
}
