/** Sam Reid*/
package edu.colorado.phet.theramp.common.scenegraph2.tests;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.theramp.common.scenegraph2.FillGraphic;
import edu.colorado.phet.theramp.common.scenegraph2.GraphicLayerNode;
import edu.colorado.phet.theramp.common.scenegraph2.SceneGraphPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 26, 2004
 * Time: 4:06:35 AM
 * Copyright (c) Jun 26, 2004 by Sam Reid
 */
public class TestSceneGraph4 {
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
            if( b ) {
                sg.setColor( Color.red );
            }
            else {
                sg.setColor( Color.black );
            }
//            sg.setColor( b ? Color.red : Color.black );
        }
    }

    static class SphereGraphic extends GraphicLayerNode {
        FillGraphic graphic;
        private Sphere sph;
        private Ellipse2D.Double shape;

        public SphereGraphic( Sphere sph ) {
            shape = new Ellipse2D.Double();
            this.graphic = new FillGraphic( shape, Color.blue );
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
            graphic.setRegionDirty();
        }

        public void paint( Graphics2D g ) {
            graphic.render( null );
        }

        public void setColor( Color color ) {
            graphic.setColor( color );
        }

        protected Rectangle determineBounds() {
            return shape.getBounds();
        }
    }

    static class CollisionModule extends Module {
        ArrayList spheres = new ArrayList();
        private SceneGraphPanel sceneGraphPanel;
        public GraphicLayerNode simRoot;

        public CollisionModule( String name, AbstractClock clock, boolean useAP2, boolean offscreen, int numParticles ) {
            super( name, clock );
            System.out.println( "useAP2 = " + useAP2 + ", offscreenBuffer=" + offscreen + ", numparticles=" + numParticles );

            sceneGraphPanel = new SceneGraphPanel();
            sceneGraphPanel.getGraphic().setAntialias( true );
            simRoot = new GraphicLayerNode();
            simRoot.scale( 0.5, 0.5 );
            sceneGraphPanel.getGraphic().addGraphic( simRoot );
            simRoot.setIgnoreMouse( true );
            setModel( new BaseModel() );
            for( int i = 0; i < numParticles; i++ ) {
                double x = Math.random() * 600;
                double y = Math.random() * 600;
                double r = 10;
                Sphere sph = new Sphere( x, y, r );
                SphereGraphic sg = new SphereGraphic( sph );
                spheres.add( sph );
                sph.setSphereGraphic( sg );
                simRoot.addGraphic( sg );
            }
            ModelElement me = new ModelElement() {
                public void stepInTime( double dt ) {
                    for( int i = 0; i < spheres.size(); i++ ) {
                        double vx = Math.random() * 2 - 1;
                        double vy = Math.random() * 2 - 1;
                        Sphere sphere = (Sphere)spheres.get( i );
                        sphere.setVelocity( vx, vy );
                        sphere.stepInTime( dt );
                    }
                    for( int i = 0; i < spheres.size(); i++ ) {
                        Sphere sph = (Sphere)spheres.get( i );
                        sph.setTouching( false );
                    }
                    for( int i = 0; i < spheres.size(); i++ ) {
                        for( int k = i; k < spheres.size(); k++ ) {
                            Sphere a = (Sphere)spheres.get( i );
                            Sphere b = (Sphere)spheres.get( k );
                            if( a != b && a.isTouching( b ) ) {
                                a.setTouching( true );
                                b.setTouching( true );
                            }
                        }
                    }
                }
            };
            addModelElement( me );
        }

        public Container getSceneGraphPanel() {
            return sceneGraphPanel;
        }
    }

    public static void main( String[] args ) {
        AbstractClock clock = new SwingTimerClock( 1, 30 );

        boolean useAP2 = true;
        boolean offscreen = true;
//        int numParticles = 300;
        int numParticles = 600;
        final CollisionModule module = new CollisionModule( "name", clock, useAP2, offscreen, numParticles );
        JFrame jf = new JFrame();
        jf.setSize( 600, 600 );
        jf.setContentPane( module.getSceneGraphPanel() );
        jf.setVisible( true );
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );


        final CollisionModule m2 = new CollisionModule( "m2", clock, true, true, 100 );
        module.sceneGraphPanel.addGraphic( m2.simRoot );
        m2.simRoot.translate( 800, 800 );
        m2.simRoot.scale( 0.3, 0.3 );

        clock.start();


        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                module.getModel().clockTicked( event );
                m2.getModel().clockTicked( event );
                module.sceneGraphPanel.repaint();
            }
        } );
//        module.sceneGraphPanel.getGraphic().scale( 0.5,0.5);
    }
}
