// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetgraphics.test;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.Particle;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.application.PhetGraphicsModule;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.phetgraphics.view.util.BasicGraphicsSetup;

/**
 * User: Sam Reid
 * Date: Jun 26, 2004
 * Time: 4:06:35 AM
 */
public class CollisionTest {
    static class Sphere extends Particle {
        double radius;
        private SphereGraphic sg;

        public Sphere( double radius ) {
            this.radius = radius;
        }

        public Sphere( double x, double y, double r ) {
            setPosition( x, y );
            this.radius = r;
        }

        public void setSphereGraphic( SphereGraphic sg ) {
            this.sg = sg;
        }

        public double getRadius() {
            return radius;
        }

        public boolean isTouching( Sphere b ) {
            return b.getPosition().distance( getPosition() ) <= radius + b.getRadius();
        }

        public void setTouching( boolean b ) {
            if ( b ) {
                sg.setColor( Color.red );
            }
            else {
                sg.setColor( Color.black );
            }
//            sg.setColor( b ? Color.red : Color.black );
        }
    }

    static class SphereGraphic extends PhetGraphic {
        PhetShapeGraphic graphic;
        private Sphere sph;
        private Ellipse2D.Double shape;

        public SphereGraphic( Sphere sph, Component parent ) {
            super( parent );
            shape = new Ellipse2D.Double();
            this.graphic = new PhetShapeGraphic( parent, shape, Color.blue );
            this.sph = sph;
            sph.addObserver( new SimpleObserver() {
                public void update() {
                    changed();
                }
            } );
            changed();
        }

        private void changed() {
            shape.setFrame( sph.getPosition().getX() - sph.getRadius(), sph.getPosition().getY() - sph.getRadius(),
                            sph.getRadius() * 2, sph.getRadius() * 2 );
            graphic.repaint();
        }

        public void paint( Graphics2D g ) {
            graphic.paint( g );
        }

        public void setColor( Color color ) {
            graphic.setColor( color );
        }

        protected Rectangle determineBounds() {
            return shape.getBounds();
        }
    }

    static class CollisionModule extends PhetGraphicsModule {
        ArrayList spheres = new ArrayList();

        public CollisionModule( String name, IClock clock, boolean useAP2, boolean offscreen, int numParticles ) {
            super( name, clock );
            System.out.println( "useAP2 = " + useAP2 + ", offscreenBuffer=" + offscreen + ", numparticles=" + numParticles );

            if ( useAP2 ) {
                ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
                apparatusPanel.setUseOffscreenBuffer( offscreen );
                setApparatusPanel( apparatusPanel );
            }
            else {
                ApparatusPanel apparatusPanel = new ApparatusPanel();
                setApparatusPanel( apparatusPanel );
            }
            getApparatusPanel().addGraphicsSetup( new BasicGraphicsSetup() );
            setModel( new BaseModel() );
//            int numParticles = 600;
//            int numParticles = 3;
            for ( int i = 0; i < numParticles; i++ ) {
                double x = Math.random() * 600;
                double y = Math.random() * 600;
                double r = 10;
                Sphere sph = new Sphere( x, y, r );
                SphereGraphic sg = new SphereGraphic( sph, getApparatusPanel() );
                spheres.add( sph );
                sph.setSphereGraphic( sg );
                addGraphic( sg, 1 );
            }
            ModelElement me = new ModelElement() {
                public void stepInTime( double dt ) {
                    for ( int i = 0; i < spheres.size(); i++ ) {
                        double vx = Math.random() * 2 - 1;
                        double vy = Math.random() * 2 - 1;
                        Sphere sphere = (Sphere) spheres.get( i );
                        sphere.setVelocity( vx, vy );
                        sphere.stepInTime( dt );
                    }
                    for ( int i = 0; i < spheres.size(); i++ ) {
                        Sphere sph = (Sphere) spheres.get( i );
                        sph.setTouching( false );
                    }
                    for ( int i = 0; i < spheres.size(); i++ ) {
                        for ( int k = i; k < spheres.size(); k++ ) {
                            Sphere a = (Sphere) spheres.get( i );
                            Sphere b = (Sphere) spheres.get( k );
                            if ( a != b && a.isTouching( b ) ) {
                                a.setTouching( true );
                                b.setTouching( true );
                            }
                        }
                    }
                }
            };
            addModelElement( me );
        }
    }

    public static void main( String[] args ) {
        IClock clock = new SwingClock( 30, 1.0 );

        boolean useAP2 = true;
        boolean offscreen = true;
//        int numParticles = 300;
        int numParticles = 600;
        final CollisionModule module = new CollisionModule( "name", clock, useAP2, offscreen, numParticles );
        JFrame jf = new JFrame();
        jf.setSize( 600, 600 );
        jf.setContentPane( module.getApparatusPanel() );
        jf.setVisible( true );
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        clock.start();
    }
}
